package org.objectweb.asm.commons.cfg.query;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * @author Tyler Sedlar
 */
public class NumberQuery extends InsnQuery {

    private int number = -1;

    public NumberQuery(int opcode) {
        super(opcode);
    }

    public NumberQuery(int opcode, int number) {
        this(opcode);
        this.number = number;
    }

    @Override
    public boolean matches(AbstractInsnNode ain) {
        if (!(ain instanceof IntInsnNode) && !(ain instanceof LdcInsnNode) && !(ain instanceof VarInsnNode))
            return false;
        if (ain instanceof IntInsnNode) {
            return number == -1 || ((IntInsnNode) ain).operand == number;
        } else if (ain instanceof LdcInsnNode) {
            Object cst = ((LdcInsnNode) ain).cst;
            return number == -1 || cst instanceof Number && ((Number) cst).intValue() == number;
        } else {
            return number == -1 || ((VarInsnNode) ain).var == number;
        }
    }
}
