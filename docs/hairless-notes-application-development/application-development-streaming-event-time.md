# Event Time（事件时间）
在前面，我们补习了各种 Flink 相关的概念，在初次的 Stream 学习中，当然是按照国际惯例跑第一个加强版的 WordCount 了。  
跑代码前，请先阅读 **参考** 部分，了解相关概念哟。

## 知识点
1. 事件时间的概念理解
2. 如何抽取时间戳
3. 如何分配水印
4. Scala API Extension 和隐式转换
5. 滚动窗口的了解和使用
6. apply 方法使用
7. 。。。等等（自行体会）

## 代码
[UserCountWithEventTimeAndPeriodicWatermarks](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/UserCountWithEventTimeAndPeriodicWatermarks.scala)

```scala
package wang.yangting.tech.flink.streaming.scala

import java.text.SimpleDateFormat

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.{AssignerWithPeriodicWatermarks, AssignerWithPunctuatedWatermarks}
import org.apache.flink.streaming.api.scala.extensions._
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

object UserCountWithEventTimeAndPeriodicWatermarks {
  case class User(name: String, date: String, ts: Long)

  def main(args: Array[String]): Unit = {
    val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)  // 并行度为 1 会立马生成数据，否则会延迟生成数据
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    val text = env.socketTextStream("localhost", 9999)
    val counts = text.mapWith {
      value => {
        // row format : zhangsan,2019-12-9 13:00:10.888
        val splits = value.split(",")
        val name = splits(0)
        val date = splits(1)
        val time = df.parse(date).getTime
        val user = User(name, date, time)
        println("input : " + user)
        user
      }
    }.assignTimestampsAndWatermarks(new PeriodicAssigner)
      .keyBy(_.name)
      .window(TumblingEventTimeWindows.of(Time.seconds(10)))  // 滚动时间窗口, 时间为 10 秒
//      .allowedLateness(Time.seconds(10))  // 允许延迟为 10 秒
      .apply((key: String, window: TimeWindow, users: Iterable[User], out: Collector[String]) => {
        var count = 0
        for (user <- users) {
          count += 1
        }
        out.collect(key + " -> " + count + " | [" + df.format(window.getStart) + ", " + df.format(window.getEnd) + ")")
      })

    counts.print()

    env.execute("UserCountWithEventTimeAndPeriodicWatermarks")
  }

  class PeriodicAssigner extends  AssignerWithPeriodicWatermarks[User] {
    var currentMaxTimestamp = 0L  // 当前最大的时间戳
    val maxOutOfOrderless = 5000  // 延迟时间 5 秒
    var lastEmittedWatermark = Long.MinValue  // 初始化水印时间

    override def getCurrentWatermark: Watermark = {
      val ts = currentMaxTimestamp - maxOutOfOrderless
      if (ts >= lastEmittedWatermark) {
        lastEmittedWatermark = ts
      }
      new Watermark(lastEmittedWatermark)
    }

    override def extractTimestamp(element: User, previousElementTimestamp: Long): Long = {
      val time = element.ts
      if (time > currentMaxTimestamp) {
        currentMaxTimestamp = time
      }
      time
    }
  }

  class PunctuatedAssigner extends AssignerWithPunctuatedWatermarks[User] {
    @Override
    override def extractTimestamp(user: User, previousElementTimestamp: Long): Long = {
//      println("---> extractTimestamp : " + user + " : " + previousElementTimestamp)
      user.ts
    }
    @Override
    override def checkAndGetNextWatermark(lastUser: User, extractedTimestamp: Long): Watermark = {
//      println("---> checkAndGetNextWatermark : " + lastUser + " : " + extractedTimestamp)
      new Watermark(extractedTimestamp)
    }
  }

  // 使用方法
  def usage (): Unit = {
    // 1. 在 CMD 中输入 nc -L -p 9999 监控端口
    // 2. 启动该程序
    // 3. 在 cmd 中输入一下数据即可。
    // 4. 对比测试结果
  }

  // 数据
  def data(): Unit = {
    // 在 CMD 中输入（有序的）
    // zhangsan,2019-12-9 13:00:02.888
    // zhangsan,2019-12-9 13:00:01.888
    // zhangsan,2019-12-9 13:00:09.888
    // zhangsan,2019-12-9 13:00:08.888
    // zhangsan,2019-12-9 13:00:04.888
    // zhangsan,2019-12-9 13:00:12.888
    // zhangsan,2019-12-9 13:00:17.888
    // zhangsan,2019-12-9 13:00:16.888
    // zhangsan,2019-12-9 13:00:25.888
  }

  // 结果
  def result() : Unit  ={
    // input : User(zhangsan,2019-12-9 13:00:02.888,1575867602888)
    // input : User(zhangsan,2019-12-9 13:00:01.888,1575867601888)
    // input : User(zhangsan,2019-12-9 13:00:09.888,1575867609888)
    // input : User(zhangsan,2019-12-9 13:00:08.888,1575867608888)
    // input : User(zhangsan,2019-12-9 13:00:04.888,1575867604888)
    // input : User(zhangsan,2019-12-9 13:00:12.888,1575867612888)
    // input : User(zhangsan,2019-12-9 13:00:17.888,1575867617888)
    // zhangsan -> 5 | [2019-12-09 13:00:00.000, 2019-12-09 13:00:10.000)
    // input : User(zhangsan,2019-12-9 13:00:16.888,1575867616888)
    // input : User(zhangsan,2019-12-9 13:00:25.888,1575867625888)
    // zhangsan -> 3 | [2019-12-09 13:00:10.000, 2019-12-09 13:00:20.000)
  }
}
```

输入

```bash
C:\Users\Administrator>nc -L -p 9999
zhangsan,2019-12-9 13:00:02.888
zhangsan,2019-12-9 13:00:01.888
zhangsan,2019-12-9 13:00:09.888
zhangsan,2019-12-9 13:00:08.888
zhangsan,2019-12-9 13:00:04.888
zhangsan,2019-12-9 13:00:12.888
zhangsan,2019-12-9 13:00:17.888
zhangsan,2019-12-9 13:00:16.888
zhangsan,2019-12-9 13:00:25.888
```

输出

```bash
input : User(zhangsan,2019-12-9 13:00:02.888,1575867602888)
input : User(zhangsan,2019-12-9 13:00:01.888,1575867601888)
input : User(zhangsan,2019-12-9 13:00:09.888,1575867609888)
input : User(zhangsan,2019-12-9 13:00:08.888,1575867608888)
input : User(zhangsan,2019-12-9 13:00:04.888,1575867604888)
input : User(zhangsan,2019-12-9 13:00:12.888,1575867612888)
input : User(zhangsan,2019-12-9 13:00:17.888,1575867617888)
zhangsan -> 5 | [2019-12-09 13:00:00.000, 2019-12-09 13:00:10.000)
input : User(zhangsan,2019-12-9 13:00:16.888,1575867616888)
input : User(zhangsan,2019-12-9 13:00:25.888,1575867625888)
zhangsan -> 3 | [2019-12-09 13:00:10.000, 2019-12-09 13:00:20.000)
```


## 参考
* <https://ci.apache.org/projects/flink/flink-docs-release-1.9/dev/event_time.html>
* [Flink 的 Event Time（事件时间）](https://www.jianshu.com/p/68ab40c7f347)
* [Flink 的事件时间和 Watermarks](https://www.jianshu.com/p/f90831c1e96d)
