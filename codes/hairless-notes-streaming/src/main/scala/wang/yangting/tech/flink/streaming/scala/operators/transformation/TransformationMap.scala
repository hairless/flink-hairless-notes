package wang.yangting.tech.flink.streaming.scala.operators.transformation

import org.apache.flink.streaming.api.scala
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

/**
  * @author yx.zhang
  */
object TransformationMap {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    import org.apache.flink.api.scala._
    val dataStream = env.fromElements(("a",1),("b",2),("a",3),("c",4))

    //指定map计算表达式
    val mapStream:scala.DataStream[(String, Int)]  =dataStream.map(t => (t._1,t._2+1))

    mapStream.print()

    env.execute()
  }
}
