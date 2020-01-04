package wang.yangting.tech.flink.streaming.scala.table

import org.apache.flink.api.scala.typeutils.Types
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.scala.{StreamTableEnvironment, _}
import org.apache.flink.table.descriptors.{Json, Kafka, Schema}
import org.apache.flink.types.Row

/**
 * TableKafkaJsonConnector
 *
 *
 * @author 那伊抹微笑
 * @github https://github.com/hairless/flink-hairless-notes
 * @date 2020-01-03
 *
 */
object TableKafkaJsonConnector {
  def main(args: Array[String]): Unit = {
    // Streaming Env
    val fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build()
    val fsEnv = StreamExecutionEnvironment.getExecutionEnvironment
    val fsTableEnv = StreamTableEnvironment.create(fsEnv, fsSettings)
    fsEnv.setParallelism(1)

    // Connect to Kafka
    fsTableEnv.connect(
      new Kafka()
        .version("universal")
        .topic("topic-hairless-flink-user-json-1")
        .property("zookeeper.connect", "localhost:2181")
        .property("bootstrap.servers", "localhost:9092")
        .property("group.id", "group-id-topic-hairless-flink-user-json-1")
        .startFromEarliest()
      )
      .withFormat(
          new Json()
            .deriveSchema()
      )
      .withSchema(
        new Schema()
          .field("id", Types.STRING)
          .field("name", Types.STRING)
          .field("balance", Types.LONG)
      )
      .inAppendMode()
      .registerTableSource("kafka_users")

    // scan and print
    fsTableEnv.scan("kafka_users").toAppendStream[Row].print().setParallelism(1)
    /*val userStream = fsTableEnv.scan("kafka_users")
    fsTableEnv.toAppendStream[Row](userStream).print().setParallelism(1)*/

    fsTableEnv.sqlQuery("SELECT SUM(balance) as amt FROM kafka_users").toRetractStream[Row].print().setParallelism(1)

    // execute
    fsEnv.execute("TableFlinkStreamingQuery")
  }

  /**
   * Kafka 的数据
   */
  def input(): Unit = {
//    {"id":"1001","name":"zhangsan","balance":100}
//    {"id":"1002","name":"lisi","balance":2000}
//    {"id":"1003","name":"wangwu","balance":1200}
  }

  /**
   * 输出
   */
  def output(): Unit = {
//    1001,zhangsan,100
//    1002,lisi,2000
//    1003,wangwu,1200
//    (true,100)
//    (false,100)
//    (true,2100)
//    (false,2100)
//    (true,3300)
  }
}
