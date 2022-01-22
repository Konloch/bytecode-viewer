require 'java'

java_import 'the.bytecode.club.bytecodeviewer.api.Plugin'
java_import 'the.bytecode.club.bytecodeviewer.api.PluginConsole'
java_import 'java.lang.System'
java_import 'java.util.ArrayList'
java_import 'org.objectweb.asm.tree.ClassNode'

#
# This is a skeleton template for BCV's Ruby Plugin System
#
# @author [Your Name Goes Here]
#

class Skeleton < Plugin
  def execute(classNodeList)
    gui = PluginConsole.new "Skeleton Title"
    gui.setVisible(true)
    gui.appendText("executed skeleton example")
  end
end