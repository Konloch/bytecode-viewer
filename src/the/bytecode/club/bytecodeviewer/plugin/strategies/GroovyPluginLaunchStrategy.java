package the.bytecode.club.bytecodeviewer.plugin.strategies;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.plugin.PluginLaunchStrategy;

/**
 * @author Bibl (don't ban me pls)
 * @created 1 Jun 2015
 */
public class GroovyPluginLaunchStrategy implements PluginLaunchStrategy {

	@Override
	public Plugin run(File file) throws Throwable {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("groovy");

		if (engine == null)
			throw new Exception(
					"Cannot find Groovy script engine! Please contact Konloch.");

		Reader reader = new FileReader(file);
		engine.eval(reader);

		return (Plugin) engine.eval("new " + file.getName().replace(".gy", "").replace(".groovy", "") + "();");
	}
}