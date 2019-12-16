# Operators（操作器 / 算子）


## DataStream Transformations（转换）
### 1.Map [DataStream -> DataStream] 
  * 调用用户定义的MapFunction对DataStream[T]数据进行处理，形成新的DataStram[T]
  * 其中数据格式可能会发生变化，常用作对数据集内数据的清洗和转换
  * map方法不允许缺少数据，原来多少条数据，处理后依然是多少条数据
   [Map](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/transformation/TransformationMap.scala) 
   
### 2.FlatMap [DataStream->DataStream]
  * FlatMap之于Map在功能上是包含关系，FlatMap常用于将数据集压扁，类比于行转列的概念
  * 返回条数不受限制
   [FlatMap](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/transformation/TransformationFlatMap.scala) 
  
###  3.Filter [DataStream->DataStream]
  * 按照条件对输入数据集筛选
   [Filter](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/transformation/TransformationFilter.scala) 
  
###  4.KeyByAndReduceAndAggregations
  * keyby[DataStream -> KeyedStream]
  * 根据指定的key将输入的DataStream[T]数据格式转换为KeyedStream[T]
  * 也就是在数据集中执行partition操作，将相同的Key值的数据放置在相同的分区中。
  *
  * reduce[KeyedStream->DataStream]
  * 类似于MR中Reduce原理，将输入的KeyedStream通过ReduceFuction滚动地进行数据聚合处理
  *
  * aggregations[KeyedStream->DataStream]
  * 聚合算子，对reduce算子进行封装，封装的操作有sum,min，minBy,max,maxBy
    [KeyByAndReduceAndAggregations](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/transformation/TransformationKeyByAndReduceAndAggregations.scala) 
  
###  5.Connect [DataStream -> DataStream]
  * connect算子主要是为了合并两种或者多种不同数据类型的数据集 ，合并后会保留原来数据集的数据类型
  * 连接操作允许共享状态数据，意味着多个数据集之间可以操作和查看对方数据集的状态。
    [Connect](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/transformation/TransformationConnect.scala) 
  
###  6.Iterate [DataStream-> IterativeStream->DataStream]
  * 适合迭代计算场景，通过每一次迭代计算，将迭代结果反馈到下一次迭代计算中
    [Iterate](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/transformation/TransformationIterate.scala) 
  
###  7.Union[DataStream->DataStream]
  * Union算子主要将两个或者多个输入的数据集合并成一个数据集，要求输入数据集格式一致，输出格式和输入格式一致
    [Union](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/transformation/TransformationUnion.scala) 
  
### 8.SplitAndSelect
  * split[DataStream->SplitStream]  flink1.9版本已经弃用该方法
  * union的逆操作，分割成多个数据集
  *
  * select [SplitStream -> DataStream]
  * split函数只是对数据集进行标记，并未真正划分，因此用select函数对数据集进行切分 
    [SplitAndSelect](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/operators/transformation/TransformationSplitAndSelect.scala)
## Physical partitioning（物理分区）

## Task chaining and resource groups（任务链和资源组）
