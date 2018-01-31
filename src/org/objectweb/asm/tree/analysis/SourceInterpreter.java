/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.objectweb.asm.tree.analysis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

/**
 * An {@link Interpreter} for {@link SourceValue} values.
 *
 * @author Eric Bruneton
 */
public class SourceInterpreter extends Interpreter<SourceValue> implements
        Opcodes {

    public SourceInterpreter() {
        super(ASM5);
    }

    protected SourceInterpreter(final int api) {
        super(api);
    }

    @Override
    public SourceValue newValue(final Type type) {
        if (type == Type.VOID_TYPE) {
            return null;
        }
        return new SourceValue(type == null ? 1 : type.getSize());
    }

    @Override
    public SourceValue newOperation(final AbstractInsnNode insn) {
        int size;
        switch (insn.opcode()) {
            case LCONST_0:
            case LCONST_1:
            case DCONST_0:
            case DCONST_1:
                size = 2;
                break;
            case LDC:
                Object cst = ((LdcInsnNode) insn).cst;
                size = cst instanceof Long || cst instanceof Double ? 2 : 1;
                break;
            case GETSTATIC:
                size = Type.getType(((FieldInsnNode) insn).desc).getSize();
                break;
            default:
                size = 1;
        }
        return new SourceValue(size, insn);
    }

    @Override
    public SourceValue copyOperation(final AbstractInsnNode insn,
                                     final SourceValue value) {
        return new SourceValue(value.getSize(), insn);
    }

    @Override
    public SourceValue unaryOperation(final AbstractInsnNode insn,
                                      final SourceValue value) {
        int size;
        switch (insn.opcode()) {
            case LNEG:
            case DNEG:
            case I2L:
            case I2D:
            case L2D:
            case F2L:
            case F2D:
            case D2L:
                size = 2;
                break;
            case GETFIELD:
                size = Type.getType(((FieldInsnNode) insn).desc).getSize();
                break;
            default:
                size = 1;
        }
        return new SourceValue(size, insn);
    }

    @Override
    public SourceValue binaryOperation(final AbstractInsnNode insn,
                                       final SourceValue value1, final SourceValue value2) {
        int size;
        switch (insn.opcode()) {
            case LALOAD:
            case DALOAD:
            case LADD:
            case DADD:
            case LSUB:
            case DSUB:
            case LMUL:
            case DMUL:
            case LDIV:
            case DDIV:
            case LREM:
            case DREM:
            case LSHL:
            case LSHR:
            case LUSHR:
            case LAND:
            case LOR:
            case LXOR:
                size = 2;
                break;
            default:
                size = 1;
        }
        return new SourceValue(size, insn);
    }

    @Override
    public SourceValue ternaryOperation(final AbstractInsnNode insn,
                                        final SourceValue value1, final SourceValue value2,
                                        final SourceValue value3) {
        return new SourceValue(1, insn);
    }

    @Override
    public SourceValue naryOperation(final AbstractInsnNode insn,
                                     final List<? extends SourceValue> values) {
        int size;
        int opcode = insn.opcode();
        if (opcode == MULTIANEWARRAY) {
            size = 1;
        } else {
            String desc = (opcode == INVOKEDYNAMIC) ? ((InvokeDynamicInsnNode) insn).desc
                    : ((MethodInsnNode) insn).desc;
            size = Type.getReturnType(desc).getSize();
        }
        return new SourceValue(size, insn);
    }

    @Override
    public void returnOperation(final AbstractInsnNode insn,
                                final SourceValue value, final SourceValue expected) {
    }

    @Override
    public SourceValue merge(final SourceValue d, final SourceValue w) {
        if (d.insns instanceof SmallSet && w.insns instanceof SmallSet) {
            Set<AbstractInsnNode> s = ((SmallSet<AbstractInsnNode>) d.insns)
                    .union((SmallSet<AbstractInsnNode>) w.insns);
            if (s == d.insns && d.size == w.size) {
                return d;
            } else {
                return new SourceValue(Math.min(d.size, w.size), s);
            }
        }
        if (d.size != w.size || !d.insns.containsAll(w.insns)) {
            HashSet<AbstractInsnNode> s = new HashSet<AbstractInsnNode>();
            s.addAll(d.insns);
            s.addAll(w.insns);
            return new SourceValue(Math.min(d.size, w.size), s);
        }
        return d;
    }
}
