package the.bytecode.club.bytecodeviewer.decompilers.bytecode;

import eu.bibl.banalysis.filter.InstructionFilter;
import eu.bibl.banalysis.filter.OpcodeFilter;
import eu.bibl.banalysis.filter.insn.FieldInstructionFilter;
import eu.bibl.banalysis.filter.insn.IincInstructionFilter;
import eu.bibl.banalysis.filter.insn.InsnInstructionFilter;
import eu.bibl.banalysis.filter.insn.JumpInstructionFilter;
import eu.bibl.banalysis.filter.insn.LdcInstructionFilter;
import eu.bibl.banalysis.filter.insn.MethodInstructionFilter;
import eu.bibl.banalysis.filter.insn.MultiANewArrayInstructionFilter;
import eu.bibl.banalysis.filter.insn.TypeInstructionFilter;
import eu.bibl.banalysis.filter.insn.VarInstructionFilter;
import java.util.Arrays;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

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
 * Pattern filter holder and stepper.
 *
 * @author Bibl
 */
public class InstructionPattern implements Opcodes {

    /**
     * Last instruction-match position pointer
     **/
    protected int pointer;
    /**
     * Filters/patterns/search criteria.
     **/
    protected InstructionFilter[] filters;
    /**
     * Last match found cache.
     **/
    protected AbstractInsnNode[] lastMatch;

    /**
     * Construct a new pattern from the specified instructions.
     *
     * @param insns {@link AbstractInsnNode} pattern array.
     */
    public InstructionPattern(AbstractInsnNode[] insns) {
        filters = translate(insns);
        lastMatch = new AbstractInsnNode[insns.length];
    }

    /**
     * Construct a new pattern from the specified opcode.
     *
     * @param opcodes Opcodes to convert to {@link OpcodeFilter}s.
     */
    public InstructionPattern(int[] opcodes) {
        filters = new InstructionFilter[opcodes.length];
        lastMatch = new AbstractInsnNode[opcodes.length];
        for (int i = 0; i < opcodes.length; i++) {
            filters[i] = new OpcodeFilter(opcodes[i]);
        }
    }

    /**
     * Construct an absolute pattern from user-defined filters.
     *
     * @param filters User-defined {@link InstructionFilter}s.
     */
    public InstructionPattern(InstructionFilter[] filters) {
        this.filters = filters;
        lastMatch = new AbstractInsnNode[filters.length];
    }

    /**
     * Steps through the instruction list checking if the current instruction
     * ended a successful pattern-match sequence.
     *
     * @param ain {@link AbstractInsnNode} to check.
     * @return True if this instruction successfully completed the pattern.
     */
    public boolean accept(AbstractInsnNode ain) {
        if (pointer >= filters.length)
            reset();

        InstructionFilter filter = filters[pointer];
        if (filter.accept(ain)) {
            lastMatch[pointer] = ain;
            if (pointer >= (filters.length - 1)) {
                return true;
            }
            pointer++;
        } else {
            reset();
        }
        return false;
    }

    /**
     * @return Last pattern sequence match equivilent from the inputted
     *         {@link AbstractInsnNode}s.
     */
    public AbstractInsnNode[] getLastMatch() {
        return lastMatch;
    }

    /**
     * Resets the instruction pointer and clears the last match cache data.
     */
    public void resetMatch() {
        reset();
        AbstractInsnNode[] match = lastMatch;
        lastMatch = new AbstractInsnNode[match.length];
    }

    /**
     * Sets the current instruction pointer to 0 (start of pattern).
     */
    public void reset() {
        pointer = 0;
    }

    /**
     * Converts an array of {@link AbstractInsnNode}s to their
     * {@link InstructionFilter} counterparts.
     *
     * @param ains {@link AbstractInsnNode}s to convert.
     * @return Array of {@link InstructionFilter}s.
     */
    public static InstructionFilter[] translate(AbstractInsnNode[] ains) {
        InstructionFilter[] filters = new InstructionFilter[ains.length];
        for (int i = 0; i < ains.length; i++) {
            filters[i] = translate(ains[i]);
        }
        return filters;
    }

    /**
     * Translate a single {@link AbstractInsnNode} to an
     * {@link InstructionFilter}.
     *
     * @param ain Instruction to convert.
     * @return A filter an an equivilent to the inputted instruction.
     */
    public static InstructionFilter translate(AbstractInsnNode ain) {
        if (ain instanceof LdcInsnNode) {
            return new LdcInstructionFilter(((LdcInsnNode) ain).cst);
        } else if (ain instanceof TypeInsnNode) {
            return new TypeInstructionFilter(ain.getOpcode(),
                    ((TypeInsnNode) ain).desc);
        } else if (ain instanceof FieldInsnNode) {
            return new FieldInstructionFilter(ain.getOpcode(),
                    ((FieldInsnNode) ain).owner, ((FieldInsnNode) ain).name,
                    ((FieldInsnNode) ain).desc);
        } else if (ain instanceof MethodInsnNode) {
            return new MethodInstructionFilter(ain.getOpcode(),
                    ((MethodInsnNode) ain).owner, ((MethodInsnNode) ain).name,
                    ((MethodInsnNode) ain).desc);
        } else if (ain instanceof VarInsnNode) {
            return new VarInstructionFilter(ain.getOpcode(),
                    ((VarInsnNode) ain).var);
        } else if (ain instanceof InsnNode) {
            return new InsnInstructionFilter(ain.getOpcode());
        } else if (ain instanceof IincInsnNode) {
            return new IincInstructionFilter(((IincInsnNode) ain).incr,
                    ((IincInsnNode) ain).var);
        } else if (ain instanceof JumpInsnNode) {
            return new JumpInstructionFilter(ain.getOpcode());
        } else if (ain instanceof LabelNode) {
            return InstructionFilter.ACCEPT_ALL; // TODO: Cache labels and
            // check. // TODO: That's a
            // fucking stupid idea.
        } else if (ain instanceof MultiANewArrayInsnNode) {
            return new MultiANewArrayInstructionFilter(
                    ((MultiANewArrayInsnNode) ain).desc,
                    ((MultiANewArrayInsnNode) ain).dims);
        } else {
            return InstructionFilter.ACCEPT_ALL;
        }
    }

    public static void main(String[] args) {
        AbstractInsnNode[] ains = new AbstractInsnNode[]{
                new LdcInsnNode("ldc"), new VarInsnNode(ASTORE, 0),
                new LdcInsnNode("ldc")};
        InstructionPattern pattern = new InstructionPattern(
                new AbstractInsnNode[]{new LdcInsnNode("ldc"),
                        new VarInsnNode(-1, -1)});
        for (AbstractInsnNode ain : ains) {
            if (pattern.accept(ain)) {
                System.out.println(Arrays.toString(pattern.getLastMatch()));
            }
        }
    }
}