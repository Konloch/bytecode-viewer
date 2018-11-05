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
package org.objectweb.asm.tree;

import java.util.Map;

import org.objectweb.asm.MethodVisitor;

/**
 * A node that represents a field instruction. A field instruction is an
 * instruction that loads or stores the value of a field of an object.
 *
 * @author Eric Bruneton
 */
public class FieldInsnNode extends AbstractInsnNode {

    /**
     * The internal name of the field's owner class (see
     * {@link org.objectweb.asm.Type#getInternalName() getInternalName}).
     */
    public String owner;

    /**
     * The field's name.
     */
    public String name;

    /**
     * The field's descriptor (see {@link org.objectweb.asm.Type}).
     */
    public String desc;

    /**
     * Constructs a new {@link FieldInsnNode}.
     *
     * @param opcode the opcode of the type instruction to be constructed. This
     *               opcode must be GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
     * @param owner  the internal name of the field's owner class (see
     *               {@link org.objectweb.asm.Type#getInternalName()
     *               getInternalName}).
     * @param name   the field's name.
     * @param desc   the field's descriptor (see {@link org.objectweb.asm.Type}).
     */
    public FieldInsnNode(final int opcode, final String owner,
                         final String name, final String desc) {
        super(opcode);
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    /**
     * Sets the opcode of this instruction.
     *
     * @param opcode the new instruction opcode. This opcode must be GETSTATIC,
     *               PUTSTATIC, GETFIELD or PUTFIELD.
     */
    public void setOpcode(final int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int type() {
        return FIELD_INSN;
    }

    @Override
    public void accept(final MethodVisitor mv) {
        mv.visitFieldInsn(opcode, owner, name, desc);
        acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> labels) {
        return new FieldInsnNode(opcode, owner, name, desc)
                .cloneAnnotations(this);
    }

    public String key() {
        return owner + "." + name + " " + desc;
    }

    @Override
    public String toString() {
        return key();
    }

    public String halfKey() {
        return name + " " + desc;
    }
}