package org.objectweb.asm.commons.cfg.tree.node;

import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

/**
 * @author Tyler Sedlar
 */
public class FieldMemberNode extends ReferenceNode {

    public FieldMemberNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
        super(tree, insn, collapsed, producing);
    }

    public FieldInsnNode fin() {
        return (FieldInsnNode) insn();
    }

    public boolean getting() {
        return opcode() == GETFIELD || opcode() == GETSTATIC;
    }

    public boolean putting() {
        return opcode() == PUTFIELD || opcode() == PUTSTATIC;
    }
}
