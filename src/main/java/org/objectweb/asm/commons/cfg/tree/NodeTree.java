package org.objectweb.asm.commons.cfg.tree;

import static org.objectweb.asm.tree.AbstractInsnNode.LABEL;

import java.util.Arrays;

import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Tyler Sedlar
 */
public class NodeTree extends AbstractNode {

    private final MethodNode mn;

    public NodeTree(MethodNode mn) {
        super(null, null, -1, -1);
        this.mn = mn;
    }

    public NodeTree(Block block) {
        this(block.owner);
    }

    @Override
    public MethodNode method() {
        return mn;
    }

    @Override
    public void accept(NodeVisitor nv) {
        if (!nv.validate()) return;
        nv.visitCode();
        for (AbstractNode node : this)
            accept(nv, node);
        nv.visitEnd();
    }

    private void accept(NodeVisitor nv, AbstractNode n) {
        if (!nv.validate()) return;
        n.accept(nv);
        for (AbstractNode node : n)
            accept(nv, node);
    }

    @Override
    public AbstractInsnNode[] collapse() {
        AbstractInsnNode[] instructions = super.collapse();
        int i = instructions.length > 1 && instructions[instructions.length - 2].type() == LABEL ? 2 : 1;
        return Arrays.copyOf(instructions, instructions.length - i);
    }
}