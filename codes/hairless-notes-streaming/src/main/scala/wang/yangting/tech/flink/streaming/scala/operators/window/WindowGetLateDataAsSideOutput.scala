package wang.yangting.tech.flink.streaming.scala.operators.window

import java.text.SimpleDateFormat

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

/**
 * Side Output Demo, 获取延迟的数据
 *
 * @author 那伊抹微笑
 * @github https://github.com/hairless/flink-hairless-notes
 * @date 2019-12-18
 *
 */
object WindowGetLateDataAsSideOutput {
  case class User(name: String, date: String, ts: Long)

  def main(args: Array[String]): Unit = {
    // df
    val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    // env
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    // socket stream
    val text = env.socketTextStream("localhost", 9999)
    // 延迟输出
    val lateOutputTag = OutputTag[User]("late-data")
    // Users Input
    val userCounts = text.map(str => {
      val splits = str.split(",")
      val user = User(splits(0), splits(1), df.parse(splits(1)).getTime)
      println("input : " + user)
      user
    }).assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks[User] {
        var currentMaxTimestamp = 0L  // 当前最大的时间戳
        val maxOutOfOrderless = 5000  // 延迟时间 5 秒
        var lastEmittedWatermark = Long.MinValue  // 初始化水印时间

        override def getCurrentWatermark: Watermark = {
          val ts = currentMaxTimestamp - maxOutOfOrderless
          if (ts >= lastEmittedWatermark) {
            lastEmittedWatermark = ts
          }
          new Watermark(lastEmittedWatermark)
        }

        override def extractTimestamp(element: User, previousElementTimestamp: Long): Long = {
          val time = element.ts
          if (time > currentMaxTimestamp) {
            currentMaxTimestamp = time
          }
          time
        }
    })
      .keyBy(_.name)
      .window(TumblingEventTimeWindows.of(Time.seconds(10))) // 滚动时间窗口为 10 秒
//      .allowedLateness(Time.seconds(5)) // 允许延迟 5 秒
      .sideOutputLateData(lateOutputTag)
      .apply((key: String, window: TimeWindow, users: Iterable[User], out: Collector[String]) => {
        var count = 0
        for (user <- users) {
          count += 1
        }
        out.collect(key + " -> " + count + " | [" + df.format(window.getStart) + ", " + df.format(window.getEnd) + ")")
      })

    userCounts.print()

    // User Late Data
    val userLate = userCounts.getSideOutput(lateOutputTag)
    userLate.map( u => {
      println("late data : " + u)
      u
    })

    // execute
    env.execute("WindowGetLateDataAsASideOutput")
  }

  // 使用方法
  def usage (): Unit = {
    // 1. 在 CMD 中输入 nc -L -p 9999 监控端口
    // 2. 启动该程序
    // 3. 在 cmd 中输入以下数据即可。
    // 4. 对比输出结果
  }

  // 数据
  def data(): Unit = {
    // 在 CMD 中输入（有序的）
    // zhangsan,2019-12-9 13:00:02.888
    // zhangsan,2019-12-9 13:00:08.888
    // zhangsan,2019-12-9 13:00:04.888
    // zhangsan,2019-12-9 13:00:12.888
    // zhangsan,2019-12-9 13:00:17.888
    // zhangsan,2019-12-9 13:00:28.888
    // zhangsan,2019-12-9 13:00:03.888
    // zhangsan,2019-12-9 13:00:19.888
    // zhangsan,2019-12-9 13:00:47.888
  }

  // 输出结果
  def output() : Unit  ={
//    input : User(zhangsan,2019-12-9 13:00:02.888,1575867602888)
//    input : User(zhangsan,2019-12-9 13:00:08.888,1575867608888)
//    input : User(zhangsan,2019-12-9 13:00:04.888,1575867604888)
//    input : User(zhangsan,2019-12-9 13:00:12.888,1575867612888)
//    input : User(zhangsan,2019-12-9 13:00:17.888,1575867617888)
//    zhangsan -> 3 | [2019-12-09 13:00:00.000, 2019-12-09 13:00:10.000)
//    input : User(zhangsan,2019-12-9 13:00:28.888,1575867628888)
//    zhangsan -> 2 | [2019-12-09 13:00:10.000, 2019-12-09 13:00:20.000)
//    input : User(zhangsan,2019-12-9 13:00:03.888,1575867603888)
//    late data : User(zhangsan,2019-12-9 13:00:03.888,1575867603888)
//    input : User(zhangsan,2019-12-9 13:00:19.888,1575867619888)
//    late data : User(zhangsan,2019-12-9 13:00:19.888,1575867619888)
//    input : User(zhangsan,2019-12-9 13:00:47.888,1575867647888)
//    zhangsan -> 1 | [2019-12-09 13:00:20.000, 2019-12-09 13:00:30.000)
  }
}