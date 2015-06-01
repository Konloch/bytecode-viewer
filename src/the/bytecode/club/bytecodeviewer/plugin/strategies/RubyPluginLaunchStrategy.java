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
public class RubyPluginLaunchStrategy implements PluginLaunchStrategy {

	@Override
	public Plugin run(File file) throws Throwable {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("jruby");

		if (engine == null)
			throw new Exception(
					"Cannot find jRuby script engine! Please contact Konloch.");

		Reader reader = new FileReader(file);
		engine.eval(reader);

		return (Plugin) engine.eval(file.getName().replace(".rb", "").replace(".ruby", "") + ".new");
	}
}