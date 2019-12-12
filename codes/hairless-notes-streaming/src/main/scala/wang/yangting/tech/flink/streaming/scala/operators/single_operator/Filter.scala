package wang.yangting.tech.flink.streaming.scala.operators.single_operator

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

/**
  * @author yx.zhang
  */
object Filter {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    import org.apache.flink.api.scala._
    val dataStream = env.fromElements(("a",1),("b",2),("a",3),("c",4))
    //筛选出第二个元素是偶数的集合
    val filter:DataStream[(String, Int)]=dataStream.filter(_._2 % 2 ==0)
    filter.print()

    env.execute()
  }
}
