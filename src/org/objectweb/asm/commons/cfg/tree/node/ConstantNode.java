package org.objectweb.asm.commons.cfg.tree.node;

import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

/**
 * @author Tyler Sedlar
 */
public class ConstantNode extends AbstractNode {

    public ConstantNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
        super(tree, insn, collapsed, producing);
    }

    @Override
    public LdcInsnNode insn() {
        return (LdcInsnNode) super.insn();
    }

    public Object cst() {
        return insn().cst;
    }
}
