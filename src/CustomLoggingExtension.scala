package org.nlogo.extensions.logging

import org.nlogo.api._, Syntax.{RepeatableType, StringType}
import scala.collection.JavaConversions.iterableAsScalaIterable

class CustomLoggingExtension extends DefaultClassManager {
  def load(manager: PrimitiveManager) {
    manager.addPrimitive("log-message",         new LogMessage)
    manager.addPrimitive("log-globals",         new LogGlobals)
    manager.addPrimitive("log-all-globals",     new LogAllGlobals)
    manager.addPrimitive("log-all-globals-but", new LogAllGlobalsBut)
  }
}

/**
 *
 * Purpose: To allow insertion of a customized logging message into the NetLogo logs.
 *          Also allows the user/model creator to place markers in the logs, and allows logging of
 *          expression values (through use of the 'word' primitive)
 *
 * Syntax: log-message {message: String}
 *
 * Arguments:
 * -msg:       Your logging message.  Can be handy for logging an expression (i.e. 'log-event (word "Turtle count is: " (count turtles)').
 *
 */
class LogMessage extends DefaultCommand {
  override def getAgentClassString = "O---"
  override def getSyntax = Syntax.commandSyntax(Array(StringType), 1, getAgentClassString, null, false)
  override def perform(args: Array[Argument], context: Context) {
    if (Version.isLoggingEnabled) context.logCustomMessage(args(0).getString)
  }
}

protected abstract class GlobalLoggingCommand extends DefaultCommand {

  override def getAgentClassString = "O---"
  override def perform(args: Array[Argument], context: Context) {
    if (Version.isLoggingEnabled) {
      val world                 = context.getAgent.world
      val specifiedGlobals      = args map (_.getString.toUpperCase)
      val nameValuePairs        = world.program.globals zip (world.observer.variables map (_.toString) toSeq) toSeq
      val comparator            = specifiedGlobals contains (_: (String, _))._1.toUpperCase
      val (filterIn, filterOut) = (() => nameValuePairs filter comparator, () => nameValuePairs filterNot comparator)
      val desiredGlobals        = if (selectsAllByDefault) filterOut else filterIn
      context.logCustomGlobals(desiredGlobals())
    }
  }

   override def getSyntax: Syntax
  protected def selectsAllByDefault: Boolean

}


/**
 *
 * Purpose: To allow more control over insertion of globals into the NetLogo logs.  Selects globals additively.
 *
 * Syntax: log-globals [{global1: String} {global2: String} ...]
 *
 * Arguments:
 * -globals:   Each 'global' will be interpreted as the name of a global variable that should be added to the log event created by this call.
 *             'global's are ignored if no global variable with the name given by the 'global' exists.
 *             NOTE: Due to how NetLogo parses variadic primitives, using this primitive with with more than one 'global' requires wrapping
 *                   the entire primitive call in parentheses (i.e. 'log-globals "a" "b"' WILL NOT COMPILE; instead, use '(log-event "a" "b")').
 *
 */
class LogGlobals extends GlobalLoggingCommand {
            override def getSyntax = Syntax.commandSyntax(Array(RepeatableType | StringType), 1, getAgentClassString, null, false)
  protected override def selectsAllByDefault = false
}


/**
 *
 * Purpose: To allow more control over insertion of globals into the NetLogo logs.  Selects globals universally.
 *
 * Syntax: log-all-globals
 *
 */
class LogAllGlobals extends GlobalLoggingCommand {
            override def getSyntax = Syntax.commandSyntax(Array[Int](), 0, getAgentClassString, null, false)
  protected override def selectsAllByDefault = true
}


/**
 *
 * Purpose: To allow more control over insertion of globals into the NetLogo logs.
 *          Begins by selecting globals universally, then deselects globals additively.
 *
 * Syntax: log-all-globals-but [{global1: String} {global2: String} ...]
 *
 * Arguments:
 * -globals:   Each 'global' will be interpreted as the name of a global variable that should _not_ be added to the log event created by this call.
 *             'global's are ignored if no global variable with the name given by the 'global' exists.
 *             NOTE: Due to how NetLogo parses variadic primitives, using this primitive with filters requires wrapping the
 *                   entire primitive call in parentheses (i.e. 'log-globals-but "a" "b"' WILL NOT COMPILE;
 *                   instead, use '(log-globals-but "a" "b")').
 *
 */
class LogAllGlobalsBut extends GlobalLoggingCommand {
            override def getSyntax = Syntax.commandSyntax(Array(RepeatableType | StringType), 1, getAgentClassString, null, false)
  protected override def selectsAllByDefault = true
}
