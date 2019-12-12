package wang.yangting.tech.flink.streaming.scala.operators.single_operator

import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.scala.{KeyedStream, StreamExecutionEnvironment}

/**
  * @author yx.zhang
  */
object KeyByAndReduceAndAggregations {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    import org.apache.flink.api.scala._
    val dataStream = env.fromElements((1, 1), (1, 2), (2, 3), (2, 4))
    //指定第一个字段为分区key
    val keyedStream: KeyedStream[(Int, Int), Tuple] = dataStream.keyBy(0)
    //滚动对第二个字段进行reduce相加求和
    val reduceStream = keyedStream.reduce { (t1, t2) =>
      (t1._1, t1._2 + t2._2)
    }
    reduceStream.print("reduce") //reduce:8> (2,7) reduce:6> (1,3)

    //按照分区对第二个字段求和
    val sumStream = keyedStream.sum(1)
    sumStream.print("sum") //sum:8> (2,7) sum:6> (1,3)

    //滚动计算指定key最小值
    val minStream = keyedStream.min(1)
    minStream.print("min")

    //滚动计算指定key的最小值，返回最小值对应的元素
    val minByStream = keyedStream.minBy(1)
    minByStream.print("minBy")
    
    env.execute()
  }
}
