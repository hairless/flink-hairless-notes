package wang.yangting.tech.flink.streaming.scala.BloomWithRedisDemo

/**
 * @Author sujinyang
 * @Date 2019/12/10 5:58 下午
 * @Version 1.0
 */

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.triggers.{Trigger, TriggerResult}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import redis.clients.jedis.Jedis


object UvWithBloomTask {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.setParallelism(1)

    // 用相对路径定义数据源
    val resource = getClass.getResource("/UserBehavior.csv")
    val dataStream = env.readTextFile(resource.getPath)
      .map(data => {
        val dataArray = data.split(",")
        UserBehavior(dataArray(0).trim.toLong, dataArray(1).trim.toLong, dataArray(2).trim.toInt, dataArray(3).trim, dataArray(4).trim.toLong)
      })
      .assignAscendingTimestamps(_.timestamp * 1000L)
      .filter(_.behavior == "pv") // 只统计pv操作
      .map(data => ("dummyKey", data.userId))
      .keyBy(_._1)
      .timeWindow(Time.hours(1),Time.minutes(1))
      .trigger(new PrepolymerizationWindowTrigger())
      .process(new UvCountWithBloom())

    dataStream.print()

    env.execute("uv with bloom job")
  }

}


// 自定义窗口触发器
class PrepolymerizationWindowTrigger() extends Trigger[(String, Long), TimeWindow] {
  override def onEventTime(time: Long, window: TimeWindow, ctx: Trigger.TriggerContext): TriggerResult = TriggerResult.CONTINUE

  override def onProcessingTime(time: Long, window: TimeWindow, ctx: Trigger.TriggerContext): TriggerResult = TriggerResult.CONTINUE

  override def clear(window: TimeWindow, ctx: Trigger.TriggerContext): Unit = {}

  override def onElement(element: (String, Long), timestamp: Long, window: TimeWindow, ctx: Trigger.TriggerContext): TriggerResult = {
    // 每来一条数据，就直接触发窗口操作，并清空所有窗口状态
    TriggerResult.FIRE_AND_PURGE
  }
}

// 定义一个布隆过滤器
class Bloom(size: Long) extends Serializable {
  // 位图的总大小，默认16M
  private val cap = if (size > 0) size else 1 << 27

  // 定义hash函数
  def hash(value: String, seed: Int): Long = {
    var result = 0L
    for( i <- 0 until value.length ){
      result = result * seed + value.charAt(i)
    }
    result  & ( cap - 1 )
  }
}

class UvCountWithBloom() extends ProcessWindowFunction[(String, Long), UvCount, String, TimeWindow]{
  // 定义redis连接
  lazy val jedis = new Jedis("localhost", 6379)
  // 2^29  64m
  lazy val bloom = new Bloom(1<<29)

  override def process(key: String, context: Context, elements: Iterable[(String, Long)], out: Collector[UvCount]): Unit = {
    // 位图的存储方式，key是windowEnd，value是bitmap
    val storeKey = context.window.getEnd.toString
    var count = 0L
    // 把每个窗口的uv count值也存入名为 的redis表，存放内容为（windowEnd -> uvCount），所以要先从redis中读取
    if( jedis.hget("count", storeKey) != null ){
      count = jedis.hget("count", storeKey).toLong
    }
    // 用布隆过滤器判断当前用户是否已经存在
    val userId = elements.last._2.toString
    val offset = bloom.hash(userId, 61)
    // 定义一个标识位，判断reids位图中有没有这一位
    val isExist = jedis.getbit(storeKey, offset)
    if(!isExist){
      // 如果不存在，位图对应位置1，count + 1
      jedis.setbit(storeKey, offset, true)
      jedis.hset("count", storeKey, (count + 1).toString)
      out.collect( UvCount(storeKey.toLong, count + 1) )
    } else {
      out.collect( UvCount(storeKey.toLong, count) )
    }
  }
}


case class UvCount( windowEnd: Long, uvCount: Long )
case class UserBehavior( userId: Long, itemId: Long, categoryId: Int, behavior: String, timestamp: Long )
