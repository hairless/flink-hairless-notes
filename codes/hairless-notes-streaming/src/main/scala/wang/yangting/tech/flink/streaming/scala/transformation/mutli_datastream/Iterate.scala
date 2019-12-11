package wang.yangting.tech.flink.streaming.scala.transformation.mutli_datastream

import org.apache.flink.streaming.api.scala.{ConnectedStreams, StreamExecutionEnvironment}

/**
  * @author yx.zhang
  * Iterate [DataStream-> IterativeStream->DataStream]
  * 适合迭代计算场景，通过每一次迭代计算，将迭代结果反馈到下一次迭代计算中
  *
  */
object Iterate {
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
