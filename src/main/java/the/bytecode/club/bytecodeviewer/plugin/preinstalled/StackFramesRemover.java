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

package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;

public class StackFramesRemover extends Plugin
{
    @Override
    public void execute(List<ClassNode> classNodeList)
    {
        AtomicInteger counter = new AtomicInteger();
        PluginConsole frame = new PluginConsole("StackFrames Remover");
        for (ClassNode cn : classNodeList)
        {
            for (MethodNode mn : cn.methods)
            {
                for (AbstractInsnNode insn : mn.instructions.toArray())
                {
                    if (insn instanceof FrameNode)
                    {
                        mn.instructions.remove(insn);
                        counter.incrementAndGet();
                    }
                }
            }
        }

        frame.appendText(String.format("Removed %s stackframes.", counter));
        frame.setVisible(true);
    }
}
