package wang.yangting.tech.flink.streaming.scala

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.scala._

object WordCountTimeWindowWithEventProcessTime {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)
    val text = env.socketTextStream("localhost", 9999)
    val counts = text.flatMap { _.toLowerCase.split(" +") filter{ _.nonEmpty }}
      .map { (_, 1) }
      .keyBy(0)
      .timeWindow(Time.seconds(5))
      .sum(1)
    counts.print()

    env.execute("WordCountTimeWindowWithEventProcessTime")
  }

  def usage (): Unit = {
    // 1. 在 CMD 中输入 nc -L -p 9999 监控端口
    // 2. 启动该程序
    // 3. 在 cmd 中输入下面的数据即可。
  }

  def data(): Unit = {
    // 在 CMD 中输入
    // zhangsan,2019-12-9 17:44:01
    // zhangsan,2019-12-9 17:44:02
    // zhangsan,2019-12-9 17:44:04
    // zhangsan,2019-12-9 17:44:06
    // zhangsan,2019-12-9 17:44:08
    // zhangsan,2019-12-9 17:44:11
    // zhangsan,2019-12-9 17:44:12
    // zhangsan,2019-12-9 17:44:13
    // zhangsan,2019-12-9 17:44:14
    // zhangsan,2019-12-9 17:44:16
    // zhangsan,2019-12-9 17:44:23
    // zhangsan,2019-12-9 17:44:28
    // zhangsan,2019-12-9 17:45:01
    // zhangsan,2019-12-9 17:45:03
    // zhangsan,2019-12-9 17:45:29
  }
}
