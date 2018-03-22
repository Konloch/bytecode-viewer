package org.objectweb.asm.commons.cfg.tree.node;

import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;

import java.util.LinkedList;
import java.util.List;

public class TargetNode extends AbstractNode {

    private final List<JumpNode> nodes = new LinkedList<>();

    public TargetNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
        super(tree, insn, collapsed, producing);
    }

    public void addTargeter(JumpNode jn) {
        nodes.add(jn);
    }

    public LabelNode label() {
        return (LabelNode) insn();
    }

    public void removeTargeter(JumpNode jn) {
        nodes.remove(jn);
    }

    public AbstractNode resolve() {
        AbstractNode n = this;
        while (n != null && n.opcode() == -1) {
            n = n.next();
        }
        return n == null ? parent() : n;
    }

    public JumpNode[] targeters() {
        return nodes.toArray(new JumpNode[nodes.size()]);
    }

    @Override
    public String toString(int tab) {
        return "Target@" + Integer.toHexString(label().hashCode());
    }
}