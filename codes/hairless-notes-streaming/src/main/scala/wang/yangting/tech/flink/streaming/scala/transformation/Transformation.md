DataStream转换操作
  即通过一个或多个DataStream生成新的DataStream的过程被称为Transformation操作。
  在转换过程中，每种操作类型被定义为不同的Operator,Flink程序能够将多个Transformation
  组成一个DataFlow的拓扑。
  所有DataStream的转换操作可以分为单single-DataStream、Multi-DataStream、物理分区三类类型。
  