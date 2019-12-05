# 术语

## 官方术语

### Flink Application Cluster（应用集群）
A Flink Application Cluster is a dedicated Flink Cluster that only executes a single Flink Job. The lifetime of the Flink Cluster is bound to the lifetime of the Flink Job. Formerly Flink Application Clusters were also known as Flink Clusters in job mode. Compare to Flink Session Cluster.

### Flink Cluster（集群）
A distributed system consisting of (typically) one Flink Master and one or more Flink TaskManager processes.  
一个分布式系统, 它由一个 Flink Master 和一个或多个 Flink TashManager 所构成.

### Event（事件）
An event is a statement about a change of the state of the domain modelled by the application. Events can be input and/or output of a stream or batch processing application. Events are special types of records.

### ExecutionGraph（执行图）
see Physical Graph

### Function（函数）
Functions are implemented by the user and encapsulate the application logic of a Flink program. Most Functions are wrapped by a corresponding Operator.

### Instance（实例）
The term instance is used to describe a specific instance of a specific type (usually Operator or Function) during runtime. As Apache Flink is mostly written in Java, this corresponds to the definition of Instance or Object in Java. In the context of Apache Flink, the term parallel instance is also frequently used to emphasize that multiple instances of the same Operator or Function type are running in parallel.

### Flink Job（作业）
A Flink Job is the runtime representation of a Flink program. A Flink Job can either be submitted to a long running Flink Session Cluster or it can be started as a self-contained Flink Application Cluster.

### JobGraph（作业图）
see Logical Graph

### Flink JobManager（作业管理器）
JobManagers are one of the components running in the Flink Master. A JobManager is responsible for supervising the execution of the Tasks of a single job. Historically, the whole Flink Master was called JobManager.

### Logical Graph（逻辑图）
A logical graph is a directed graph describing the high-level logic of a stream processing program. The nodes are Operators and the edges indicate input/output-relationships or data streams or data sets.

### Managed State（管理状态）
Managed State describes application state which has been registered with the framework. For Managed State, Apache Flink will take care about persistence and rescaling among other things.

### Flink Master（）
The Flink Master is the master of a Flink Cluster. It contains three distinct components: Flink Resource Manager, Flink Dispatcher and one Flink JobManager per running Flink Job.

### Operator（操作器）
Node of a Logical Graph. An Operator performs a certain operation, which is usually executed by a Function. Sources and Sinks are special Operators for data ingestion and data egress.

### Operator Chain（操作链）
An Operator Chain consists of two or more consecutive Operators without any repartitioning in between. Operators within the same Operator Chain forward records to each other directly without going through serialization or Flink’s network stack.

### Partition（分区）
A partition is an independent subset of the overall data stream or data set. A data stream or data set is divided into partitions by assigning each record to one or more partitions. Partitions of data streams or data sets are consumed by Tasks during runtime. A transformation which changes the way a data stream or data set is partitioned is often called repartitioning.

### Physical Graph（物理图）
A physical graph is the result of translating a Logical Graph for execution in a distributed runtime. The nodes are Tasks and the edges indicate input/output-relationships or partitions of data streams or data sets.

### Record（记录）
Records are the constituent elements of a data set or data stream. Operators and Functions receive records as input and emit records as output.

### Flink Session Cluster（会话集群）
A long-running Flink Cluster which accepts multiple Flink Jobs for execution. The lifetime of this Flink Cluster is not bound to the lifetime of any Flink Job. Formerly, a Flink Session Cluster was also known as a Flink Cluster in session mode. Compare to Flink Application Cluster.

### State Backend（状态后端）
For stream processing programs, the State Backend of a Flink Job determines how its state is stored on each TaskManager (Java Heap of TaskManager or (embedded) RocksDB) as well as where it is written upon a checkpoint (Java Heap of Flink Master or Filesystem).

### Sub-Task（子任务）
A Sub-Task is a Task responsible for processing a partition of the data stream. The term “Sub-Task” emphasizes that there are multiple parallel Tasks for the same Operator or Operator Chain.

### Task（任务）
Node of a Physical Graph. A task is the basic unit of work, which is executed by Flink’s runtime. Tasks encapsulate exactly one parallel instance of an Operator or Operator Chain.

### Flink TaskManager（任务管理器）
TaskManagers are the worker processes of a Flink Cluster. Tasks are scheduled to TaskManagers for execution. They communicate with each other to exchange data between subsequent Tasks.

### Transformation（转换）
A Transformation is applied on one or more data streams or data sets and results in one or more output data streams or data sets. A transformation might change a data stream or data set on a per-record basis, but might also only change its partitioning or perform an aggregation. While Operators and Functions) are the “physical” parts of Flink’s API, Transformations are only an API concept. Specifically, most - but not all - transformations are implemented by certain Operators.

## 参考
* <https://ci.apache.org/projects/flink/flink-docs-release-1.9/concepts/glossary.html>