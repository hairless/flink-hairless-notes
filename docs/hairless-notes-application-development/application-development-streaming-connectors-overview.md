# Streaming Connectors（连接器）
提供与其它系统进行数据交互的能力，如读取或写入能力，数据处理语义（至少一次，最多一次，仅一次）的保证等等 。

## 知识

### 知识点
* Predefined Sources and Sinks（预定义的 Source 和 Sink）
* Bundled Connectors（附带的连接器）
    * [Apache Kafka (source/sink)](application-development-streaming-connectors-kafka.md)
    * Apache Cassandra (sink)
    * Amazon Kinesis Streams (source/sink)
    * Elasticsearch (sink)
    * Hadoop FileSystem (sink)
    * RabbitMQ (source/sink)
    * Apache NiFi (source/sink)
    * Twitter Streaming API (source)
    * Google PubSub (source/sink)
* Connectors in Apache Bahir
* Other Ways to Connect to Flink（其它方式）
    * Data Enrichment via Async I/O（Async I/O）
    * Queryable State（可查询的状态）

## 参考
* <https://ci.apache.org/projects/flink/flink-docs-release-1.9/dev/connectors/>
