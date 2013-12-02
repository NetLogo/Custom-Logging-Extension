package org.nlogo.extensions.logging

import
  org.nlogo.api.{ Argument, Context, DefaultCommand, Syntax, Version },
    Syntax.{ commandSyntax, RepeatableType, StringType }


/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 6/21/13
 * Time: 4:44 PM
 */

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

  override def performLogging(args: Array[Argument], context: Context) {
    import scala.collection.JavaConversions.iterableAsScalaIterable
    val world              = context.getAgent.world
    val targets            = args map (_.getString.toUpperCase)
    val nameValuePairs     = world.program.globals zip (world.observer.variables map (_.toString) toSeq) toSeq
    val comparator: Filter = { case (global, _) => targets contains global.toUpperCase }
    context.logCustomGlobals(select(nameValuePairs, comparator))
  }

}

protected trait LoggingCommand extends DefaultCommand {

  override def getAgentClassString = "OTPL"
  override def getSyntax           = commandSyntax(primArgsSyntax)

  final override def perform(args: Array[Argument], context: Context) {
    if (Version.isLoggingEnabled)
      performLogging(args, context)
  }

  protected def primArgsSyntax: Array[Int]
  protected def performLogging(args: Array[Argument], context: Context)

}

