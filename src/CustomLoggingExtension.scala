package org.nlogo.extensions.logging

import org.nlogo.api.{ DefaultClassManager, PrimitiveManager }

class CustomLoggingExtension extends DefaultClassManager {
  def load(manager: PrimitiveManager) {
    manager.addPrimitive("log-message",         LogMessage)
    manager.addPrimitive("log-globals",         LogGlobals)
    manager.addPrimitive("log-all-globals",     LogAllGlobals)
    manager.addPrimitive("log-all-globals-but", LogAllGlobalsBut)
  }
}



