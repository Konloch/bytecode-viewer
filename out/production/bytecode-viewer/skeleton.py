from the.bytecode.club.bytecodeviewer.api import Plugin
from the.bytecode.club.bytecodeviewer.api import PluginConsole
from java.lang import System
from java.lang import Boolean
from java.util import ArrayList
from org.objectweb.asm.tree import ClassNode

class skeleton(Plugin):

	def execute(classNodeList, poop): #for some reason it requires a second arg
		gui = PluginConsole("Skeleton")
		gui.setVisible(Boolean.TRUE)
		gui.appendText("exceuted skeleton")