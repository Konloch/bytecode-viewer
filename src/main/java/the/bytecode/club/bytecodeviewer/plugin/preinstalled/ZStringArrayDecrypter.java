package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.BCV;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;
import the.bytecode.club.bytecodeviewer.gui.components.MultipleChoiceDialog;

import static the.bytecode.club.bytecodeviewer.Constants.nl;

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
 * Runs the classes then simply grabs the static String[] z
 *
 * @author Konloch
 * @author Righteous
 */

public class ZStringArrayDecrypter extends Plugin
{
    @Override
    public void execute(List<ClassNode> classNodeList)
    {
        PluginConsole gui = new PluginConsole("ZStringArray Decrypter");
        StringBuilder out = new StringBuilder();
    
        MultipleChoiceDialog dialog = new MultipleChoiceDialog("Bytecode Viewer - WARNING",
                "WARNING: This will load the classes into the JVM and execute the initialize function"
                        + nl + "for each class. IF THE FILE YOU'RE LOADING IS MALICIOUS, DO NOT CONTINUE.",
                new String[]{"Continue", "Cancel"});

        if (dialog.promptChoice() == 0)
        {
            boolean needsWarning = false;
            for (Class<?> cn :
                    Objects.requireNonNull(BCV.loadClassesIntoClassLoader()))
            {
                try
                {
                    Field[] fields = cn.getDeclaredFields();
                    for (Field field : fields)
                    {
                        if (field.getName().equals("z"))
                        {
                            out.append(cn.getName()).append(":").append(nl);
                            field.setAccessible(true);
                            if (field.get(null) != null && field.get(null) instanceof String[]
                                    && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()))
                            {
                                String[] fieldVal = (String[]) field.get(null);
                                for (int i = 0; i < fieldVal.length; i++)
                                    out.append("  z[").append(i).append("] = ").append(fieldVal[i]).append(nl);
                            }
                        }
                    }
                }
                catch (NoClassDefFoundError | Exception e)
                {
                    System.err.println("Failed loading class " + cn.getName());
                    e.printStackTrace();
                    needsWarning = true;
                }
            }

            if (needsWarning)
            {
                BytecodeViewer.showMessage("Some classes failed to decrypt, if you'd like to decrypt all of them"
                        + nl + "makes sure you include ALL the libraries it requires.");
            }

            gui.setText(out.toString());
            gui.setVisible(true);
        }
    }
}
