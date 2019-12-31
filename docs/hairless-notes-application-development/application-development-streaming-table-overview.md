# Table API & SQL Overview（概述）

Apache Flink 为同一流和批处理提供了两个关系型的 API - Table API 和 SQL.
Table API 是一个针对 Scala 和 Java 的语言集成的查询 API, 它能以一种非常直观的方式从相关的算子（像 selection, filter, join）来组合查询.
Flink 的 SQL 支持是基于 [Apache Calcite](https://calcite.apache.org/) 来实现的 SQL 标准.
无论输入的数据是批输入（DataSet）或流输入（DataStream）, 在这两个接口中指定的查询具有相同的语义并产生相同的结果.

## 建议
这一小节基本上只要看看官方文档就 OK 啦. 有不懂的地方，可以随时艾特我哟.

## 参考
* <https://ci.apache.org/projects/flink/flink-docs-release-1.9/dev/table/>