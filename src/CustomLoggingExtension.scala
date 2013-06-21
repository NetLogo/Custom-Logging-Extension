package org.nlogo.extensions.logging

import org.nlogo.api._, Syntax.{RepeatableType, StringType}
import scala.collection.JavaConversions.iterableAsScalaIterable

class CustomLoggingExtension extends DefaultClassManager {
  def load(manager: PrimitiveManager) {
    manager.addPrimitive("log-message",         LogMessage)
    manager.addPrimitive("log-globals",         LogGlobals)
    manager.addPrimitive("log-all-globals",     LogAllGlobals)
    manager.addPrimitive("log-all-globals-but", LogAllGlobalsBut)
  }
}

object LogMessage extends LoggingCommand {
  override protected def primArgsSyntax = Array(StringType)
  override protected def performLogging(args: Array[Argument], context: Context) {
    context.logCustomMessage(args(0).getString)
  }
}

object LogGlobals extends GlobalLoggingCommand with LazySelector {
  override protected def primArgsSyntax = Array(RepeatableType | StringType)
}

object LogAllGlobals extends GlobalLoggingCommand with GreedySelector {
  override protected def primArgsSyntax = Array()
}

object LogAllGlobalsBut extends GlobalLoggingCommand with GreedySelector {
  override protected def primArgsSyntax = Array(RepeatableType | StringType)
}

protected trait GlobalLoggingCommand extends LoggingCommand {

  self: Selector =>

  private type PF = PartialFunction[(String, String), Boolean]

  override def performLogging(args: Array[Argument], context: Context) {
    val world          = context.getAgent.world
    val targets        = args map (_.getString.toUpperCase)
    val nameValuePairs = world.program.globals zip (world.observer.variables map (_.toString) toSeq) toSeq
    val comparator: PF = { case (global, _) => targets contains global.toUpperCase }
    context.logCustomGlobals(select(nameValuePairs, comparator))
  }

}

protected trait LoggingCommand extends DefaultCommand {

  override def getAgentClassString = "O---"
  override def getSyntax           = Syntax.commandSyntax(primArgsSyntax)

  final override def perform(args: Array[Argument], context: Context) {
    if (Version.isLoggingEnabled)
      performLogging(args, context)
  }

  protected def primArgsSyntax: Array[Int]
  protected def performLogging(args: Array[Argument], context: Context)

}

protected trait Selector {
  def select(pairs: Seq[(String, String)], comparator: ((String, String)) => Boolean) : Seq[(String, String)]
}

protected trait GreedySelector extends Selector {
  override def select(pairs: Seq[(String, String)], comparator: ((String, String)) => Boolean) : Seq[(String, String)] = pairs filterNot comparator
}

protected trait LazySelector extends Selector {
  override def select(pairs: Seq[(String, String)], comparator: ((String, String)) => Boolean) : Seq[(String, String)] = pairs filter comparator
}
