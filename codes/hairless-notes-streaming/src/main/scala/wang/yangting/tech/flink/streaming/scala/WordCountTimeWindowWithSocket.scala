package wang.yangting.tech.flink.streaming.scala

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.scala._

object WordCountTimeWindowWithSocket {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.socketTextStream("localhost", 9999)
    val counts = text.flatMap { _.toLowerCase.split(" +") filter{ _.nonEmpty }}
      .map { (_, 1) }
      .keyBy(0)
      .timeWindow(Time.seconds(5))
      .sum(1)
    counts.print()

    env.execute("Window Stream WordCount")
  }
}
