package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import org.objectweb.asm.tree.*;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 * *
 * This program is free software: you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by  *
 * the Free Software Foundation, either version 3 of the License, or     *
 * (at your option) any later version.                                   *
 * *
 * This program is distributed in the hope that it will be useful,       *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 * GNU General Public License for more details.                          *
 * *
 * You should have received a copy of the GNU General Public License     *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * Simply shows all the non-empty strings in every single class
 *
 * @author Konloch
 */
public class ShowAllStrings extends Plugin {
    @Override
    public void execute(final ArrayList<ClassNode> classNodeList) {
        final PluginConsole frame = new PluginConsole("Show All Strings");
        final AtomicBoolean complete = new AtomicBoolean(false);
        final Thread backgroundThread = new Thread() {
            public void run() {
                try {
                    for (ClassNode classNode : classNodeList) {
                        for (Object o : classNode.fields.toArray()) {
                            FieldNode f = (FieldNode) o;
                            Object v = f.value;
                            if (v instanceof String) {
                                String s = (String) v;
                                if (!s.isEmpty()) {
                                    frame.appendText(String.format("%s.%s%s -> \"%s\"", classNode.name, f.name, f.desc, s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r")));
                                }
                            }
                            if (v instanceof String[]) {
                                for (int i = 0; i < ((String[]) v).length; i++) {
                                    String s = ((String[]) v)[i];
                                    if (!s.isEmpty()) {
                                        frame.appendText(String.format("%s.%s%s[%s] -> \"%s\"", classNode.name, f.name, f.desc, i, s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r")));
                                    }
                                }
                            }
                        }
                        for (Object o : classNode.methods.toArray()) {
                            MethodNode m = (MethodNode) o;
                            InsnList iList = m.instructions;
                            for (AbstractInsnNode a : iList.toArray()) {
                                if (a instanceof LdcInsnNode) {
                                    if (((LdcInsnNode) a).cst instanceof String) {
                                        final String s = (String) ((LdcInsnNode) a).cst;
                                        if (!s.isEmpty()) {
                                            frame.appendText(String.format("%s.%s%s -> \"%s\"", classNode.name, m.name, m.desc, s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r")));
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    new ExceptionUI(e, "konloch@gmail.com");
                } finally {
                    complete.set(true);
                }
            }
        };
        frame.setVisible(true);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                backgroundThread.stop();
                complete.set(true);
            }

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
        backgroundThread.start();
        while (!complete.get()) ;
    }
}
