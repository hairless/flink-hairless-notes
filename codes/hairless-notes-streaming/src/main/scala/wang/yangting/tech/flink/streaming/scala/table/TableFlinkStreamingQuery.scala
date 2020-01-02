package wang.yangting.tech.flink.streaming.scala.table

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.scala.StreamTableEnvironment
import org.apache.flink.table.api.scala._

/**
 * TableFlinkStreamingQuery
 *
 *
 * @author 那伊抹微笑
 * @github https://github.com/hairless/flink-hairless-notes
 * @date 2020-01-02
 *
 */
object TableFlinkStreamingQuery {
  def main(args: Array[String]): Unit = {
    // Streaming Env
    val fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build()
    val fsEnv = StreamExecutionEnvironment.getExecutionEnvironment
    val fsTableEnv = StreamTableEnvironment.create(fsEnv, fsSettings)
    fsEnv.setParallelism(1)

    // words stream
    val words = fsEnv.fromElements(
      (1, "zhangsan"),
      (1, "lisi"),
      (1, "wangwu"),
      (1, "zhangsan"),
      (1, "lisi"),
      (1, "zhangsan")
    )
//    words.print()


    // register table from DataStream
    /*val table = fsTableEnv.fromDataStream(words, 'count, 'name)
    fsTableEnv.registerTable("users", table)*/
    fsTableEnv.registerDataStream("users", words, 'id, 'name)

    // toAppendStream
    val selectAll = fsTableEnv.sqlQuery("SELECT name, id FROM users")
    selectAll.toAppendStream[(String, Int)].print()

    // toRetractStream
    val selectGroupByAll = fsTableEnv.sqlQuery("SELECT name, count(1) AS cnt FROM users GROUP BY name")
    selectGroupByAll.toRetractStream[(String, Long)].print()

    // execute
    fsEnv.execute("TableFlinkStreamingQuery")
  }
}
