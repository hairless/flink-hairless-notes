package wang.yangting.tech.flink.streaming.scala.operators.transformation

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

/**
  * @author yx.zhang
  */
object TransformationFlatMap {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    import org.apache.flink.api.scala._
    val dataStream: DataStream[String] = env.fromElements("aabbccdd")
    val result = dataStream.flatMap(str => str.split(""))
    result.print()
    env.execute()
  }
}
