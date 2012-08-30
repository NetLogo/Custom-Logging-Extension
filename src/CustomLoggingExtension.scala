package org.nlogo.extensions.logging

import org.nlogo.api._, Syntax.{RepeatableType, StringType}
import scala.collection.JavaConversions.iterableAsScalaIterable

class CustomLoggingExtension extends DefaultClassManager {
  def load(manager: PrimitiveManager) {
    manager.addPrimitive("log-event", new LogEvent())
  }
}

/**
 *
 * Purpose: To allow insertion of more-customized logging events in NetLogo logs.
 *          Allows the user/model creator to choose when a log event in triggered, and what goes into it.
 *
 * Syntax: log-event {msg} [{filter1}, {filter2}, ...]
 *
 * Semantics:
 * -log-event: The name of this primitive
 * -msg:       Your logging message.  Can be handy for logging an expression (i.e. 'log-event (word "Turtle count is: " (count turtles)').
 * -filters:   Filters are optional extra strings that can be specified when calling this primitive.
 *             Each filter will be interpreted as the name of a global that is to be added to the log event
 *             Filters are ignored if no primitive with the name given by the filter exists.
 *             This behavior changes a bit when "*" is one of filters passed in, though.
 *             Specifying "*" as the only filter will cause this primitive to include _all_ globals in the log event.
 *             Specifying "*" as a filter, along with {x}, {y}, and {z} will include all globals in the log event, except for {x}, {y}, and {z}.
 *             NOTE: Due to how NetLogo parses variadic primitives, using this primitive with filters requires wrapping the
 *                   entire primitive call in parentheses (i.e. 'log-event "Hi" "*"' WILL NOT COMPILE; instead, use '(log-event "Hi" "*")').
 *
 */
class LogEvent extends DefaultCommand {
  override def getAgentClassString = "O---"
  override def getSyntax = Syntax.commandSyntax(Array(StringType, RepeatableType | StringType), 1, getAgentClassString, null, false)
  override def perform(args: Array[Argument], context: Context) {
    if(Version.isLoggingEnabled) {
      val world                 = context.getAgent.world
      val (msg, globalFilters)  = (args splitAt 1) match { case (msgList, filters) => (msgList.head.getString, filters map (_.getString.toUpperCase)) }
      val isSelectingAll        = globalFilters.contains("*")
      val nameValuePairs        = world.program.globals zip (world.observer.variables map (_.toString) toSeq) toSeq
      val comparator            = globalFilters contains (_: (String, _))._1.toUpperCase
      val (filterIn, filterOut) = (() => nameValuePairs filter comparator, () => nameValuePairs filterNot comparator)
      val desiredGlobals        = if (isSelectingAll) filterOut else filterIn
      org.nlogo.api.Logger.logCustomMessage(msg, desiredGlobals(): _*)
    }
  }
}
