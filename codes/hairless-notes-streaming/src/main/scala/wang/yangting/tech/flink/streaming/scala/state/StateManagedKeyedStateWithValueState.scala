package wang.yangting.tech.flink.streaming.scala.state

import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state.{StateTtlConfig, ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.time.Time
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.util.Collector

object StateManagedKeyedStateWithValueState {
  class CountWindowAverage extends RichFlatMapFunction[(Long, Long), (Long, Long)] {

    private var sum: ValueState[(Long, Long)] = _

    override def flatMap(input: (Long, Long), out: Collector[(Long, Long)]): Unit = {

      // 访问状态的值
      val tmpCurrentSum = sum.value

      // 如果它之前没有被使用过，则为 null
      val currentSum = if (tmpCurrentSum != null) {
        tmpCurrentSum
      } else {
        (0L, 0L)
      }

      // 更新 count
      val newSum = (currentSum._1 + 1, currentSum._2 + input._2)

      // 更新状态
      sum.update(newSum)

      // 如果统计次数 >= 2, 发射平均数，并调用 clear 方法来清空状态
      if (newSum._1 >= 2) {
        out.collect((input._1, (newSum._2 / newSum._1)))
        sum.clear()
      }
    }

    override def open(parameters: Configuration): Unit = {
      /*// 设置状态的 TLL，这个通常用的不太多，随着状态的增加，也需要来管理，通过不同场景调用 cleanupFullSnapshot，cleanupInBackground，cleanupIncrementally 即可。
      val ttlConfig = StateTtlConfig
        .newBuilder(Time.seconds(1))
        .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
        .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
        .build
      val valueStateDescriptor = new ValueStateDescriptor[(Long, Long)]("average", createTypeInformation[(Long, Long)])
      valueStateDescriptor.enableTimeToLive(ttlConfig)*/
      sum = getRuntimeContext.getState(
        new ValueStateDescriptor[(Long, Long)]("average", createTypeInformation[(Long, Long)])
      )
    }
  }

  def main(args: Array[String]): Unit = {
    // 获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    // 从集合获取数据
    env.fromCollection(
      List(
        (1L, 3L),
        (1L, 5L),
        (1L, 7L),
        (1L, 4L),
        (1L, 2L))
    ).keyBy(_._1)
      .flatMap(new CountWindowAverage())
      .print()
    // execute
    env.execute("Example With Managed State(ValueState)")
  }

  // 结果
  def result(): Unit = {
    // 直接输入如下结果
    // (1,4)
    // (1,5)
  }
}
