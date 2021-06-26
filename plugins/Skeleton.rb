require 'java'

java_import 'the.bytecode.club.bytecodeviewer.api.Plugin'
java_import 'the.bytecode.club.bytecodeviewer.api.PluginConsole'
java_import 'java.lang.System'
java_import 'java.util.ArrayList'
java_import 'org.objectweb.asm.tree.ClassNode'

class Skeleton < Plugin
  def execute(classNodeList)
    gui = PluginConsole.new "Skeleton"
    gui.setVisible(true)
    gui.appendText("executed skeleton")
  end
end