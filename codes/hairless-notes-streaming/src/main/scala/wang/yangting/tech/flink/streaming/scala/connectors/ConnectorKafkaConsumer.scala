package wang.yangting.tech.flink.streaming.scala.connectors

import java.util.Properties

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.streaming.util.serialization.SimpleStringSchema

/**
 * Flink Kafka Connector Consumer Demo
 *
 * @author 那伊抹微笑
 * @github https://github.com/hairless/flink-hairless-notes
 * @date 2019-12-30
 *
 */
object ConnectorKafkaConsumer {
  def main(args: Array[String]): Unit = {
    // env
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    // properties
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "127.0.0.1:9092")
    properties.setProperty("group.id", "group-id-topic-hairless-flink-notes-1")
    // topic
    val topic = "topic-hairless-flink-notes-1"
    // schema
    val schema = new SimpleStringSchema()
    // Kafka Source
    val kafkaSource = new FlinkKafkaConsumer[String](topic, schema, properties)
    kafkaSource.setStartFromEarliest()      // start from the earliest record possible
//    kafkaSource.setStartFromLatest()        // start from the latest record
//    kafkaSource.setStartFromTimestamp(1)    // start from specified epoch timestamp (milliseconds)
//    kafkaSource.setStartFromGroupOffsets()  // the default behaviour
    // Add Source
    val kafkaStream = env.addSource(kafkaSource)
      .print()  // print

    // execute
    env.execute("Flink Kafka Connector Consumer Demo")
  }

  /**
   * 数据，在 docker 的 kafka 容器中使用 Kafka 的命令来准备测试数据
   */
  def data(): Unit = {
//     // 创建 topic
//     bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic topic-hairless-flink-notes-1
//     // 查看 toopic
//     bin/kafka-topics.sh --list --bootstrap-server localhost:9092
//     // 准备测试数据
//     bin/kafka-console-producer.sh --broker-list localhost:9092 --topic topic-hairless-flink-notes-1
//      >hello
//      >world
//      >for
//      >hairless
//      >flink
//      >notes
//     // 查看测试数据
//     bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic topic-hairless-flink-notes-1 --from-beginning
  }

  /**
   * 输出
   */
  def output(): Unit ={
//     hello
//     world
//     for
//     hairless
//     flink
//     notes
  }
}
