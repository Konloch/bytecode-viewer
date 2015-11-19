package org.objectweb.asm.commons.cfg.query;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * @author Tyler Sedlar
 */
public class InsnQuery implements Opcodes {

    public int distance = -1;

    public final int opcode;
    protected AbstractInsnNode insn;

    public InsnQuery(int opcode) {
        this.opcode = opcode;
    }

    public boolean matches(AbstractInsnNode ain) {
        return ain.opcode() == opcode;
    }

    public void setInstruction(AbstractInsnNode insn) {
        this.insn = insn;
    }

    public AbstractInsnNode insn() {
        return insn;
    }

    public InsnQuery distance(int distance) {
        this.distance = distance;
        return this;
    }
}
