package the.bytecode.club.bytecodeviewer.plugin.strategies;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.plugin.PluginLaunchStrategy;

/**
 * @author Konloch
 * @author Bibl (don't ban me pls)
 * @created 1 Jun 2015
 */
public class PythonPluginLaunchStrategy implements PluginLaunchStrategy {

	@Override
	public Plugin run(File file) throws Throwable {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("python");

		if (engine == null)
			throw new Exception(
					"Cannot find Jython script engine! Please contact Konloch.");

		Reader reader = new FileReader(file);
		engine.eval(reader);

		return (Plugin) engine.eval(file.getName().replace(".py", "").replace(".python", "") + "()");
	}
}