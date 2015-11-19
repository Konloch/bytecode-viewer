package org.objectweb.asm.commons.cfg.tree.node;

import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * @author Tyler Sedlar
 */
public class ArithmeticNode extends AbstractNode {

    public ArithmeticNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
        super(tree, insn, collapsed, producing);
    }

    public boolean isInt() {
        return opcode() == IADD || opcode() == ISUB || opcode() == IMUL || opcode() == IDIV;
    }

    public boolean isDouble() {
        return opcode() == DADD || opcode() == DSUB || opcode() == DMUL || opcode() == DDIV;
    }

    public boolean isLong() {
        return opcode() == LADD || opcode() == LSUB || opcode() == LMUL || opcode() == LDIV;
    }

    public boolean isFloat() {
        return opcode() == FADD || opcode() == FSUB || opcode() == FMUL || opcode() == FDIV;
    }

    public boolean adding() {
        return opcode() == IADD || opcode() == DADD || opcode() == LADD || opcode() == FADD;
    }

    public boolean subtracting() {
        return opcode() == ISUB || opcode() == DSUB || opcode() == LSUB || opcode() == FSUB;
    }

    public boolean multiplying() {
        return opcode() == IMUL || opcode() == DMUL || opcode() == LMUL || opcode() == FMUL;
    }

    public boolean dividing() {
        return opcode() == IDIV || opcode() == DDIV || opcode() == LDIV || opcode() == FDIV;
    }

    public boolean negating() {
        return opcode() == INEG || opcode() == DNEG || opcode() == LNEG || opcode() == FNEG;
    }

    public boolean remainding() {
        return opcode() == IREM || opcode() == DREM || opcode() == LREM || opcode() == FREM;
    }

    public boolean shifting() {
        return rightShifting() || leftShifiting();
    }

    public boolean rightShifting() {
        return opcode() == ISHR || opcode() == LSHR || opcode() == IUSHR || opcode() == LUSHR;
    }

    public boolean leftShifiting() {
        return opcode() == ISHL || opcode() == LSHL;
    }

    public boolean including() {
        return opcode() == IAND || opcode() == LAND;
    }

    public boolean comparing() {
        return opcode() == IXOR || opcode() == LXOR || opcode() == IOR || opcode() == LOR;
    }

    public boolean bitwise() {
        return negating() || remainding() || shifting() || including() || comparing();
    }
}
