package the.bytecode.club.bytecodeviewer.plugin.strategies;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
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