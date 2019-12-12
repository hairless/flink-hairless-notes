 DataStream转换操作
    即通过一个或多个DataStream生成新的DataStream的过程被称为Transformation操作。
    在转换过程中，每种操作类型被定义为不同的Operator,Flink程序能够将多个Transformation
    组成一个DataFlow的拓扑。
    所有DataStream的转换操作可以分为single-DataStream、Multi-DataStream、物理分区三类类型。
    single-DataStream定义了对单个DataStream数据集元素的处理逻辑，Multi-DataStream定义了对多个
    DataStream数据集元素的处理逻辑。物理分区定义了对数据集中地并行度和数据分区调整转换的处理逻辑
# Operators（操作器 / 算子）
 ## 1.Map [DataStream -> DataStream] 
   * 调用用户定义的MapFunction对DataStream[T]数据进行处理，形成新的DataStram[T]
   * 其中数据格式可能会发生变化，常用作对数据集内数据的清洗和转换
   * map方法不允许缺少数据，原来多少条数据，处理后依然是多少条数据
     [Map](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/single_operator/Map.scala) 
 
 ## 2.FlatMap[DataStream->DataStream]
   * FlatMap之于Map在功能上是包含关系，FlatMap常用于将数据集压扁，类比于行转列的概念
   * 返回条数不受限制
     [FlatMap](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/single_operator/FlatMap.scala) 

 ##  3.Filter[DataStream->DataStream]
   * 按照条件对输入数据集筛选
     [Filter](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/single_operator/Filter.scala) 

 ##  4.KeyByAndReduceAndAggregations
   * keyby[DataStream -> KeyedStream]
   * 根据指定的key将输入的DataStream[T]数据格式转换为KeyedStream[T]
   * 也就是在数据集中执行partition操作，将相同的Key值的数据放置在相同的分区中。
   *
   * reduce[KeyedStream->DataStream]
   * 类似于MR中Reduce原理，将输入的KeyedStream通过ReduceFuction滚动地进行数据聚合处理
   *
   * aggregations[KeyedStream->DataStream]
   * 聚合算子，对reduce算子进行封装，封装的操作有sum,min，minBy,max,maxBy
     [KeyByAndReduceAndAggregations](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/single_operator/KeyByAndReduceAndAggregations.scala) 

 ##  5.Connect[DataStream -> DataStream]
   * connect算子主要是为了合并两种或者多种不同数据类型的数据集 ，合并后会保留原来数据集的数据类型
   * 连接操作允许共享状态数据，意味着多个数据集之间可以操作和查看对方数据集的状态。
     [Connect](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/mutli_operator/Connect.scala) 

 ##  6.Iterate [DataStream-> IterativeStream->DataStream]
   * 适合迭代计算场景，通过每一次迭代计算，将迭代结果反馈到下一次迭代计算中
     [Iterate](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/mutli_operator/Iterate.scala) 

 ##  7.Union[DataStream->DataStream]
   * Union算子主要将两个或者多个输入的数据集合并成一个数据集，要求输入数据集格式一致，输出格式和输入格式一致
     [Union](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/mutli_operator/Union.scala) 

  ## 8.SplitAndSelect
   * split[DataStream->SplitStream]  flink1.9版本已经弃用该方法
   * union的逆操作，分割成多个数据集
   *
   * select [SplitStream -> DataStream]
   * split函数只是对数据集进行标记，并未真正划分，因此用select函数对数据集进行切分 
     [SplitAndSelect](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/mutli_operator/SplitAndSelect.scala) 

## DataStream Transformations（转换）

## Physical partitioning（物理分区）

## Task chaining and resource groups（任务链和资源组）
