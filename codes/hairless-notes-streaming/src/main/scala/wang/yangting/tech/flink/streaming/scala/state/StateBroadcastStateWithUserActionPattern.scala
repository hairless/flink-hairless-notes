package wang.yangting.tech.flink.streaming.scala.state

import org.apache.flink.api.common.state.{MapStateDescriptor, ValueState, ValueStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.util.Collector

/**
 * Broadcast State Demo，用户行为模式匹配
 *
 * 流 actions 是用户行为
 * 流 patterns 是模式匹配
 *
 * @author 那伊抹微笑
 * @github https://github.com/hairless/flink-hairless-notes
 * @date 2019-12-17
 * @reference https://www.jianshu.com/p/6228bd6e7095
 *
 */
object StateBroadcastStateWithUserActionPattern {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    /*// Actions 用户行为
    val actions = env.fromCollection(List(
      Action(1001, "UserLogin"),  // 登录
      Action(1003, "PaymentComplete"),  // 付款完成
      Action(1002, "AddToCart"),  // 添加到购物车
      Action(1001, "UserLogout"),  // 注销
      Action(1003, "AddToCart"),  // 添加到购物车
      Action(1002, "UserLogin")  // 登录
    ))

    // Patterns 行为模式
    val patterns = env.fromCollection(List(
      Pattern("AddToCart", "UserLogout"),
        Pattern("UserLogin", "UserLogout")
    ))

    // KeyBy UserId
    val actionsByUser = actions.keyBy(_.userId)*/

    /**
     * Actions 用户行为
     *
     * // 输入
     * 1001,UserLogin
     * 1003,PaymentComplete
     * 1002,AddToCart
     * 1001,UserLogout
     * 1003,AddToCart
     * 1002,UserLogin
     *
     */
    val actionsSocket = env.socketTextStream("localhost", 9991)
    val actionsByUser = actionsSocket.map(str => {
      val splits = str.split(",")
      val action = Action(splits(0).toLong, splits(1))
      println("input action : " + action)
      action
    })
      // key by userId
      .keyBy(_.userId)

    /**
     * Patterns 模式
     *
     * // 输入
     * UserLogin,UserLogout
     * AddToCart,UserLogout
     */
    val patternSocket = env.socketTextStream("localhost", 9996)
    val patterns = patternSocket.map(str => {
      val splits = str.split(",")
      val pattern = Pattern(splits(0), splits(1))
      println("input pattern : " + pattern)
      pattern
    })

    // Broadcast State
    val bcStateDescriptor = new MapStateDescriptor("patterns", createTypeInformation[Void], createTypeInformation[Pattern])
    val bcedPatterns = patterns.broadcast(bcStateDescriptor)

    // Connect And Process
    actionsByUser
      .connect(bcedPatterns)
      .process(new PatternEvaluator())
      .print()

    env.execute("UserActionPatter")
  }

  /**
   * 输入和输入
   */
  def inputAndOutput(): Unit = {
    //    open
    //    input pattern : Pattern(UserLogin,UserLogout)
    //    processBroadcastElement : Pattern(UserLogin,UserLogout)
    //    input action : Action(1001,UserLogin)
    //    processElement : Action(1001,UserLogin)
    //    input action : Action(1001,UserLogout)
    //    processElement : Action(1001,UserLogout)
    //    matching ...[UserLogin,UserLogout] <-> [UserLogin,UserLogout]
    //    match : 1001 | Pattern(UserLogin,UserLogout)
    //    (1001,Pattern(UserLogin,UserLogout))
  }
}

/**
 * 模式评估器
 */
class PatternEvaluator extends KeyedBroadcastProcessFunction[Long, Action, Pattern, (Long, Pattern)] {
  // handle for keyed state (per user)
  var prevActionState: ValueState[String] = _
  // broadcast state descriptor
  var patternDesc: MapStateDescriptor[Void, Pattern] = _

  override def open(parameters: Configuration): Unit = {
    println("open")

    // 初始化
    prevActionState = getRuntimeContext.getState(new ValueStateDescriptor("lastAction", createTypeInformation[String]))
    patternDesc = new MapStateDescriptor("patterns", createTypeInformation[Void], createTypeInformation[Pattern])
  }

  /**
   * 每一个 User Action 都会调用该方法.
   * 根据用户以前的 Action 和当前的 Action 来计算当前的 Pattern
   *
   * @param value
   * @param ctx
   * @param out
   */
  override def processElement(value: Action, ctx: KeyedBroadcastProcessFunction[Long, Action, Pattern, (Long, Pattern)]#ReadOnlyContext, out: Collector[(Long, Pattern)]): Unit = {
    println("processElement : " + value)

    // 从 broadcast state 中获取当前的 pattern
    val bcState = ctx.getBroadcastState(patternDesc)

    val pattern = bcState.get(null)

    // 获取当前用户先前的 Action
    val prevAction = prevActionState.value()

    // 模式
    if (pattern != null && prevAction != null) {
      // 用户先前有一个 Action, 检测是否模式匹配
      println("matching ...[" + prevAction+","+value.action+"] <->" + " ["+pattern.firstAction+","+pattern.secondAction+"]")
      if (pattern.firstAction.equals(prevAction) && pattern.secondAction.equals(value.action)) {
        // 匹配成功
        println("match : " + ctx.getCurrentKey + " | " + pattern)
        out.collect((ctx.getCurrentKey, pattern))
      }
    }

    // 更新状态
    prevActionState.update(value.action)
  }

  /**
   * 每一个新的 Pattern 都会调用该方法.
   * 使用最新的 Pattern 来覆盖先前的.
   *
   * @param value
   * @param ctx
   * @param out
   */
  override def processBroadcastElement(value: Pattern, ctx: KeyedBroadcastProcessFunction[Long, Action, Pattern, (Long, Pattern)]#Context, out: Collector[(Long, Pattern)]): Unit = {
    println("processBroadcastElement : " + value)

    val bcState = ctx.getBroadcastState(patternDesc)
    bcState.put(null, value)
  }
}

/**
 * 用户动作
 *
 * @param userId
 * @param action
 */
case class Action(userId: Long, action: String)

/**
 * 匹配模式
 *
 * @param firstAction
 * @param secondAction
 */
case class Pattern(firstAction: String, secondAction: String)
