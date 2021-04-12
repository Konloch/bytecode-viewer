package the.bytecode.club.bytecodeviewer.decompilers.bytecode;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LineNumberNode;

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
 * @author Bibl
 */

public class InstructionSearcher implements Opcodes {

    protected InsnList insns;
    protected InstructionPattern pattern;

    protected List<AbstractInsnNode[]> matches;

    public InstructionSearcher(InsnList insns, int[] opcodes) {
        this(insns, new InstructionPattern(opcodes));
    }

    public InstructionSearcher(InsnList insns, AbstractInsnNode[] ains) {
        this(insns, new InstructionPattern(ains));
    }

    public InstructionSearcher(InsnList insns, InstructionPattern pattern) {
        this.insns = insns;
        this.pattern = pattern;
        matches = new ArrayList<>();
    }

    public boolean search() {
        for (AbstractInsnNode ain : insns.toArray()) {
            if (ain instanceof LineNumberNode || ain instanceof FrameNode)
                continue;
            if (pattern.accept(ain)) {
                matches.add(pattern.getLastMatch());
                pattern.resetMatch();
            }
        }
        return size() != 0;
    }

    public List<AbstractInsnNode[]> getMatches() {
        return matches;
    }

    public int size() {
        return matches.size();
    }
}