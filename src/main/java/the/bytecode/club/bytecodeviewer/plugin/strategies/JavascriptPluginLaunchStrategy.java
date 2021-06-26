package the.bytecode.club.bytecodeviewer.plugin.strategies;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.plugin.PluginLaunchStrategy;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

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
    public static final String firstPickEngine = "rhino";
    public static final String fallBackEngine = "nashorn";
    
    @Override
    public Plugin run(File file) throws Throwable
    {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName(firstPickEngine);

        if (engine == null)
            engine = manager.getEngineByName(fallBackEngine);
        
        if (engine == null)
            throw new Exception("Cannot find Javascript script engine! Please contact Konloch.");

        Reader reader = new FileReader(file);
        engine.eval(reader);
    
        ScriptEngine finalEngine = engine;
        return new Plugin()
        {
            @Override
            public void execute(ArrayList<ClassNode> classNodeList)
            {
                try
                {
                    ((Invocable) finalEngine).invokeFunction("execute", classNodeList);
                }
                catch (NoSuchMethodException | ScriptException e)
                {
                    new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
                }
            }
        };
    }
}