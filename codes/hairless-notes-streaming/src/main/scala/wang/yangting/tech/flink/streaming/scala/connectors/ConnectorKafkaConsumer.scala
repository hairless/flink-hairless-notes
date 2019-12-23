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
 * @date 2019-12-23
 *
 */
object ConnectorKafkaConsumer {
  def main(args: Array[String]): Unit = {
    // env
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    // properties
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    properties.setProperty("group.id", "ting-test-1")
    // topic
    val topic = "ting-topic-1"
    // schema
    val schema = new SimpleStringSchema()
    // Kafka Source
    val kafkaSource = new FlinkKafkaConsumer[String](topic, schema, properties)
//    kafkaSource.setStartFromEarliest()      // start from the earliest record possible
//    kafkaSource.setStartFromLatest()        // start from the latest record
//    kafkaSource.setStartFromTimestamp(1)    // start from specified epoch timestamp (milliseconds)
//    kafkaSource.setStartFromGroupOffsets()  // the default behaviour
    // Add Source
    val kafkaStream = env.addSource(kafkaSource)
      .print()  // print

    // execute
    env.execute("Flink Kafka Connector Consumer Demo")
  }
}
