/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
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

package the.bytecode.club.bytecodeviewer.plugin.strategies;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.plugin.PluginLaunchStrategy;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

/**
 * @author Konloch
 * @author Bibl (don't ban me pls)
 * @since 06/25/2021
 */
public class JavascriptPluginLaunchStrategy implements PluginLaunchStrategy
{
    //attempt to use nashorn
    public static final String FIRST_PICK_ENGINE = "nashorn";

    //fallback to graal.js
    public static final String FALL_BACK_ENGINE = "graal.js";

    //can we use the JS engine
    public static final boolean IS_JS_ENGINE_IN_CLASSPATH = isJSEngineInClassPath();

    @Override
    public Plugin run(File file) throws Throwable
    {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName(FIRST_PICK_ENGINE);

        //nashorn compatability with graal
        if (engine == null)
        {
            engine = manager.getEngineByName(FALL_BACK_ENGINE);

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

    public static boolean isJSEngineInClassPath()
    {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName(FIRST_PICK_ENGINE);

        //check fallback
        if (engine == null)
        {
            engine = manager.getEngineByName(FALL_BACK_ENGINE);

            return engine != null;
        }

        return true;
    }
}
