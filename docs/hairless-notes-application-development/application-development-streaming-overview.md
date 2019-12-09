# Streaming 概述
在前面，我们补习了各种 Flink 相关的概念，在初次的 Stream 学习中，当然是按照国际惯例跑第一个 WordCount 了。

## 示例程序 WordCount
千万要注意，别导错包，导错包，导错包了，不然就是代码两分钟，环境五小时的悲伤故事。。。

Scala 版本如下 :

```scala
package wang.yangting.tech.flink.streaming.scala

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time

object WordCountTimeWindowWithSocket {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.socketTextStream("localhost", 9999)
    val counts = text.flatMap { _.toLowerCase.split(" +") filter{ _.nonEmpty }}
      .map { (_, 1) }
      .keyBy(0)
      .timeWindow(Time.seconds(5))
      .sum(1)
    counts.print()

    env.execute("Window Stream WordCount")
  }
}
```

Java 版本如下 :

```java
package wang.yangting.tech.flink.streaming.java;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class WordCountTimeWindowWithSocket {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Tuple2<String, Integer>> dataStream = env
                .socketTextStream("localhost", 9999)
                .flatMap(new Splitter())
                .keyBy(0)
                .timeWindow(Time.seconds(5))
                .sum(1);

        dataStream.print();

        env.execute("Window WordCount");
    }

    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String word: sentence.split(" +")) {
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }
    }
}
```

在运行该代码之前，我们需要首先使用（Windows） nc 工具启监听 9999 端口（如果没有安装，可阅读最后的 **参考** 中的资料）:

```bash
# Windows
nc -L -p 9999

# Linux
nc -lk 9999
```

下面的代码地址中有完整的代码示例（Scala / Java 版本），已经使用 Maven 整合好了相关的 Java，Scala 环境和 Junit。直接使用即可哟。  
有问题也可以 [私聊我](https://github.com/wangyangting)。

## 代码地址
> 可直接在 IDEA 中右键 Run 起来。  
> 后续更新的代码会以 Scala 为准。
  
* Scala 版 : [WordCountTimeWindowWithSocket](../../codes/hairless-notes-streaming/src/main/scala/wang/yangting/tech/flink/streaming/scala/WordCountTimeWindowWithSocket.scala)
* Java 版 : [WordCountTimeWindowWithSocket](../../codes/hairless-notes-streaming/src/main/java/wang/yangting/tech/flink/streaming/java/WordCountTimeWindowWithSocket.java)

## 参考
* <https://ci.apache.org/projects/flink/flink-docs-release-1.9/dev/datastream_api.html>
* [Wnidows 下安装 NC 工具](https://blog.csdn.net/nicolewjt/article/details/88898735)
