package the.bytecode.club.bytecodeviewer.plugins;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.Plugin;

/**
 * Supports loading of groovy, python or ruby scripts.
 * 
 * Only allows one plugin to be running at once.
 * 
 * @author Konloch
 *
 */

public class PluginManager {

	private static Plugin pluginInstance;
	
	public static void runPlugin(Plugin newPluginInstance) {
		if(pluginInstance == null || pluginInstance.isFinished()) {
			pluginInstance = newPluginInstance;
			pluginInstance.start(); //start the thread
		} else if(!pluginInstance.isFinished()) {
			BytecodeViewer.showMessage("There is currently another plugin running right now, please wait for that to finish executing.");
		}
	}
	
	public static void runPlugin(File f) throws Exception {
		Plugin p = null;
		if(f.getName().endsWith(".gy") || f.getName().endsWith(".groovy")) {
			p = loadGroovyScript(f);
		}
		if(f.getName().endsWith(".py") || f.getName().endsWith(".python")) {
			p = loadPythonScript(f);
		}
		if(f.getName().endsWith(".rb") || f.getName().endsWith(".ruby")) {
			p = loadRubyScript(f);
		}
		if(p != null) {
			runPlugin(p);
		}
	}
    
    /**
     * Loads a groovy file as a Script
     * @param file
     * @return
     * @throws Exception
     */
    private static Plugin loadGroovyScript(File file) throws Exception {
		ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("groovy");
        
        if(engine == null)
        	throw new Exception("Cannot find Groovy script engine! Please contact Konloch.");
        
        Reader reader = new FileReader(file);
        engine.eval(reader);
        
        return (Plugin)engine.eval("new " + file.getName().replace(".gy", "").replace(".groovy", "") + "();");
    }

    /**
     * Loads a python file as a Script
     * @param file
     * @return
     * @throws Exception
     */
    private static Plugin loadPythonScript(File file) throws Exception {
    	ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("python");
        
        if(engine == null)
        	throw new Exception("Cannot find Jython script engine! Please contact Konloch.");
        
        Reader reader = new FileReader(file);
        engine.eval(reader);
        
        return (Plugin)engine.eval(file.getName().replace(".py", "").replace(".python", "") + "()");
    }

    /**
     * Loads a ruby file as a Script
     * @param file
     * @return
     * @throws Exception
     */
    private static Plugin loadRubyScript(File file) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("jruby");
        
        if(engine == null)
        	throw new Exception("Cannot find jRuby script engine! Please contact Konloch.");
        
        Reader reader = new FileReader(file);
        engine.eval(reader);
        
        return (Plugin)engine.eval(file.getName().replace(".rb", "").replace(".ruby", "") + ".new");
    }
}
