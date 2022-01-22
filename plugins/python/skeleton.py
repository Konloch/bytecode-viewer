from the.bytecode.club.bytecodeviewer.api import Plugin
from the.bytecode.club.bytecodeviewer.api import PluginConsole
from java.lang import System
from java.lang import Boolean
from java.util import ArrayList
from org.objectweb.asm.tree import ClassNode

#
# This is a skeleton template for BCV's Ruby Plugin System
#
# @author [Your Name Goes Here]
#

class skeleton(Plugin):
	def execute(classNodeList, notUsed): #for some reason it requires a second arg
		gui = PluginConsole("Skeleton Title")
		gui.setVisible(Boolean.TRUE)
		gui.appendText("executed skeleton example")