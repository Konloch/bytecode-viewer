package the.bytecode.club.bytecodeviewer.plugin.strategies;

import java.io.File;
import org.codehaus.janino.SimpleCompiler;
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

public class JavaPluginLaunchStrategy implements PluginLaunchStrategy
{
    @Override
    public Plugin run(File file) throws Throwable
    {
        SimpleCompiler compiler = new SimpleCompiler(file.getCanonicalPath());

        //debug
        //System.out.println(file.getName().substring(0, file.getName().length() - (".java".length())));
        
        //get the class object from the compiler classloader
        Class<?> clazz = Class.forName(
                file.getName().substring(0, file.getName().length() - ".java".length()),
                true,
                compiler.getClassLoader()
        );

        //create a new instance of the class
        return (Plugin) clazz.getDeclaredConstructor().newInstance();
    }
}
