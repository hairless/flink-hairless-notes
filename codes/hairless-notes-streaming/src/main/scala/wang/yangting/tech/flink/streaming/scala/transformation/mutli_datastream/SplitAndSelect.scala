package wang.yangting.tech.flink.streaming.scala.transformation.mutli_datastream

import org.apache.flink.streaming.api.scala.{DataStream, SplitStream, StreamExecutionEnvironment}


/**
  * @author yx.zhang
  * split[DataStream->SplitStream]  flink1.9版本已经弃用该方法
  * union的逆操作，分割成多个数据集
  *
  * select [SplitStream -> DataStream]
  * split函数只是对数据集进行标记，并未真正划分，因此用select函数对数据集进行切分
  */
object SplitAndSelect {
  def main(args: Array[String]): Unit = {
      val env  = StreamExecutionEnvironment.getExecutionEnvironment
      import org.apache.flink.api.scala._
      val dataStream1: DataStream[(String,Int)] = env.fromElements(("a",3),("d",4),("c",2),("c",5),("a",5))
      val splitStream:SplitStream[(String,Int)] = dataStream1
        .split(t => if (t._2 % 2 == 0) Seq("even") else Seq("old"))
      val evenStream : DataStream[(String,Int)] = splitStream.select("even")
      val oddStream : DataStream[(String,Int)] = splitStream.select("odd")
      evenStream.print()
      env.execute()

  }
}

