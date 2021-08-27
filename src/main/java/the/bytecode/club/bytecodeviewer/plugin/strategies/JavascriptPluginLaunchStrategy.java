package the.bytecode.club.bytecodeviewer.plugin.strategies;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.plugin.PluginLaunchStrategy;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * @author Konloch
 * @author Bibl (don't ban me pls)
 * @since 06/25/2021
 */
public class JavascriptPluginLaunchStrategy implements PluginLaunchStrategy
{
    //attempt to use nashorn
    public static final String firstPickEngine = "nashorn";
    //fallback to graal.js
    public static final String fallBackEngine = "graal.js";
    
    @Override
    public Plugin run(File file) throws Throwable
    {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName(firstPickEngine);
    
        //nashorn compatability with graal
        if (engine == null)
        {
            engine = manager.getEngineByName(fallBackEngine);
            
            if (engine == null)
                throw new Exception("Cannot find Javascript script engine! Please contact Konloch.");
            
            Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("polyglot.js.allowHostAccess", true);
            bindings.put("polyglot.js.allowAllAccess", true);
            bindings.put("polyglot.js.allowHostClassLookup", true);
        }

        Reader reader = new FileReader(file);
        engine.eval(reader);
    
        ScriptEngine finalEngine = engine;
        
        return new Plugin()
        {
            @Override
            public void execute(List<ClassNode> classNodeList)
            {
                try
                {
                    //add the active container as a global variable to the JS script
                    finalEngine.put("activeContainer", activeContainer);
                    
                    //invoke the JS function
                    ((Invocable) finalEngine).invokeFunction("execute", classNodeList);
                }
                catch (NoSuchMethodException | ScriptException e)
                {
                    BytecodeViewer.handleException(e);
                }
            }
        };
    }
}
