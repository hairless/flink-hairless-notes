package wang.yangting.tech.flink.streaming.scala.transformation.mutli_datastream

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

/**
  * @author yx.zhang
  * Union[DataStream->DataStream]
  * Union算子主要将两个或者多个输入的数据集合并成一个数据集，要求输入数据集格式一致，输出格式和输入格式一致
  */
object Union {
  def main(args: Array[String]): Unit = {
      val env = StreamExecutionEnvironment.getExecutionEnvironment
    
    import org.apache.flink.api.scala._
    val dataStream1:DataStream[(String,Int)]=env.fromElements(("a",1),("b",2),("c",3),("d",4),("e",5))
    val dataStream2:DataStream[(String,Int)]=env.fromElements(("a",6),("b",7),("c",8),("d",9),("e",10))
    val dataStream3:DataStream[(String,Int)]=env.fromElements(("a",11),("b",12),("c",13),("d",14),("e",15))
    val unionStream = dataStream1.union(dataStream2)
    val  unionStream2 = unionStream.union(dataStream3)
    unionStream2.print()
    env.execute()
  }
}
