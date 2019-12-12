package wang.yangting.tech.flink.streaming.scala.operators.transformation

import org.apache.flink.streaming.api.scala.{ConnectedStreams, StreamExecutionEnvironment}

/**
  * @author yx.zhang
  */
object TransformationIterate {
  def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        import org.apache.flink.api.scala._
        val dataStream = env.fromElements(3,1,2,1,5).map{t:Int => t}

        val iterated = dataStream.iterate((input : ConnectedStreams[Int,Int]) => {
            //分别定义两个map方法完成对输入ConnectedStreams数据集收集的处理
          val head = input.map(i => (i+1), s => s )
          (head.filter(_ % 2 ==0), head.filter(_ % 2 != 0)) },1000)

    //输出
    //5> 3
        iterated.print()
       env.execute()
  }
}
