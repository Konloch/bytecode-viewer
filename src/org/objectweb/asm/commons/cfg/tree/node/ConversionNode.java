package org.objectweb.asm.commons.cfg.tree.node;

import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * @author Tyler Sedlar
 */
public class ConversionNode extends AbstractNode {

    public ConversionNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
        super(tree, insn, collapsed, producing);
    }

    public boolean fromInt() {
        return opcode() == I2B || opcode() == I2C || opcode() == I2S || opcode() == I2L || opcode() == I2D;
    }

    public boolean toInt() {
        return opcode() == D2I || opcode() == L2I || opcode() == F2I;
    }

    public boolean toChar() {
        return opcode() == I2C;
    }

    public boolean toShort() {
        return opcode() == I2S;
    }

    public boolean fromDouble() {
        return opcode() == D2I || opcode() == D2F || opcode() == D2L;
    }

    public boolean toDouble() {
        return opcode() == I2D || opcode() == L2D || opcode() == F2D;
    }

    public boolean fromLong() {
        return opcode() == L2I || opcode() == L2F || opcode() == L2D;
    }

    public boolean toLong() {
        return opcode() == I2L || opcode() == D2L || opcode() == F2L;
    }

    public boolean fromFloat() {
        return opcode() == F2I || opcode() == F2D || opcode() == F2L;
    }

    public boolean toFloat() {
        return opcode() == I2F || opcode() == D2F || opcode() == L2F;
    }
}
