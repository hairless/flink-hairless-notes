package wang.yangting.tech.flink.streaming.scala.table

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.scala.{StreamTableEnvironment, _}
import org.apache.flink.types.Row

/**
 * TableKafkaJsonSQL
 *
 *
 * @author 那伊抹微笑
 * @github https://github.com/hairless/flink-hairless-notes
 * @date 2020-01-04
 *
 */
object TableKafkaJsonSQL {
  def main(args: Array[String]): Unit = {
    // Streaming Env
    val fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build()
    val fsEnv = StreamExecutionEnvironment.getExecutionEnvironment
    val fsTableEnv = StreamTableEnvironment.create(fsEnv, fsSettings)
    fsEnv.setParallelism(1)

    // SQL DDL
    val sqlDDL = "CREATE TABLE kafka_users (" +
      "    id VARCHAR," +
      "    name VARCHAR," +
      "    balance BIGINT" +
      ") WITH (" +
      "    'connector.type' = 'kafka'," +
      "    'connector.version' = 'universal'," +
      "    'connector.topic' = 'topic-hairless-flink-user-json-1'," +
      "    'connector.startup-mode' = 'earliest-offset'," +
      "    'connector.properties.0.key' = 'zookeeper.connect'," +
      "    'connector.properties.0.value' = 'localhost:2181'," +
      "    'connector.properties.1.key' = 'bootstrap.servers'," +
      "    'connector.properties.1.value' = 'localhost:9092'," +
      "    'update-mode' = 'append'," +
      "    'format.type' = 'json'," +
      "    'format.derive-schema' = 'true'" +
      ")"

    // DDL
    fsTableEnv.sqlUpdate(sqlDDL)

    // DML Query
    fsTableEnv.sqlQuery("SELECT * FROM kafka_users").toAppendStream[Row].print()

    fsTableEnv.sqlQuery("SELECT SUM(balance) as amt FROM kafka_users").toRetractStream[Row].print()

    // execute
    fsEnv.execute("TableKafkaJsonSQL")
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
