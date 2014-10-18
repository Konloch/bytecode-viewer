require 'java'

java_import 'the.bytecode.club.bytecodeviewer.plugins.Plugin'
java_import 'the.bytecode.club.bytecodeviewer.plugins.PluginConsole'
java_import 'java.lang.System'
java_import 'java.util.ArrayList'
java_import 'org.objectweb.asm.tree.ClassNode'

class Skeleton < Plugin
  def execute(classNodeList)
    gui = PluginConsole.new "Skeleton"
    gui.setVisible(true)
    gui.appendText("exceuted skeleton")
  end
end