package wang.yangting.tech.flink.streaming.scala.table

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.scala.{StreamTableEnvironment, _}
import org.apache.flink.types.Row

/**
 * TableKafkaToMysqlJsonSQL
 *
 * @author 那伊抹微笑
 * @github https://github.com/hairless/flink-hairless-notes
 * @date 2020-01-05
 *
 */
object TableKafkaJsonToMysqlSQL {
  def main(args: Array[String]): Unit = {
    // Streaming Env
    val fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build()
    val fsEnv = StreamExecutionEnvironment.getExecutionEnvironment
    val fsTableEnv = StreamTableEnvironment.create(fsEnv, fsSettings)
    fsEnv.setParallelism(1)

    // Kafka SQL DDL
    val kafkaUserDDL = "CREATE TABLE kafka_users (" +
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

    // mysql_users
    val mysqlUserDDL = "CREATE TABLE mysql_users (" +
      "    id VARCHAR," +
      "    name VARCHAR," +
      "    balance BIGINT" +
      ") WITH (" +
      "    'connector.type' = 'jdbc'," +
      "    'connector.url' = 'jdbc:mysql://localhost:3306/fless?characterEncoding=utf-8&useSSL=false'," +
      "    'connector.table' = 'mysql_users'," +
      "    'connector.username' = 'root'," +
      "    'connector.password' = 'Abc1234567'," +
      "    'connector.write.flush.max-rows' = '1'" +
      ")"

    // mysql_users_group_by_name
    val mysqlUserGroupByNameDDL = "CREATE TABLE mysql_users_group_by_name (" +
      "    name VARCHAR," +
      "    accu_amount BIGINT" +
      ") WITH (" +
      "    'connector.type' = 'jdbc'," +
      "    'connector.url' = 'jdbc:mysql://localhost:3306/fless?characterEncoding=utf-8&useSSL=false'," +
      "    'connector.table' = 'mysql_users_group_by_name'," +
      "    'connector.username' = 'root'," +
      "    'connector.password' = 'Abc1234567'," +
      "    'connector.write.flush.max-rows' = '1'" +
      ")"

    // DDL for create table
    fsTableEnv.sqlUpdate(kafkaUserDDL)
    fsTableEnv.sqlUpdate(mysqlUserDDL)
    fsTableEnv.sqlUpdate(mysqlUserGroupByNameDDL)

    // DML Query
    fsTableEnv.sqlQuery("SELECT id, name, balance FROM kafka_users").toAppendStream[Row].print()
    fsTableEnv.sqlQuery("SELECT name, SUM(balance) accu_amount FROM kafka_users GROUP BY name").toRetractStream[Row].print()

    // DML Insert
    // insert into mysql_users
    fsTableEnv.sqlUpdate("INSERT INTO mysql_users SELECT id, name, balance FROM kafka_users")
    // insert into mysql_users_group_by_name
    fsTableEnv.sqlUpdate("INSERT INTO mysql_users_group_by_name SELECT name, SUM(balance) accu_amount FROM kafka_users GROUP BY name")

    // execut
    fsEnv.execute("TableKafkaToMysqlJsonSQL")
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
    // Console
//    1001,zhangsan,100
//    1002,lisi,2000
//    1003,wangwu,1200
//    (true,zhangsan,100)
//    (true,lisi,2000)
//    (true,wangwu,1200)

    // mysql_users
//    1001	zhangsan	100
//    1002	lisi	2000
//    1003	wangwu	1200

    // mysql_users_group_by_name
//    zhangsan	100
//    lisi	2000
//    wangwu	1200
  }
}
