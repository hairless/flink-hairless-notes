package wang.yangting.tech.flink.streaming.scala.operators.transformation

import org.apache.flink.streaming.api.functions.co.CoMapFunction
import org.apache.flink.streaming.api.scala.{ConnectedStreams, DataStream, StreamExecutionEnvironment}

/**
  * @author yx.zhang
  */
object TransformationConnect {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    import org.apache.flink.api.scala._
    val dataStream1:DataStream[(String,Int)] = env.fromElements(("a",1),("b",2),("c",3),("d",4),("e",5))
    val dataStream2:DataStream[Int] =env.fromElements(1,2,3,4,5)
    val connectdeStream:ConnectedStreams[(String, Int), Int] = dataStream1.connect(dataStream2)

    val resultStream = connectdeStream.map(new CoMapFunction[(String,Int),Int,(Int,String)] {
      override def map1(in1: (String, Int)): (Int, String) = {
        (in1._2,in1._1)
      }

      override def map2(in2: Int): (Int, String) = {
        (in2,"default")
      }
    })

    resultStream.print()

    env.execute()
  }
}
