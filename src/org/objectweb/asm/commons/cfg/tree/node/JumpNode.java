package org.objectweb.asm.commons.cfg.tree.node;

import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.commons.util.Assembly;
import org.objectweb.asm.tree.JumpInsnNode;

public class JumpNode extends AbstractNode {

    private TargetNode target;

    public JumpNode(NodeTree tree, JumpInsnNode insn, int collapsed, int producing) {
        super(tree, insn, collapsed, producing);
    }

    public JumpInsnNode insn() {
        return (JumpInsnNode) super.insn();
    }

    public AbstractNode resolve() {
        return target.resolve();
    }

    public void setTarget(TargetNode target) {
        if (this.target != null) {
            this.target.removeTargeter(this);
        }
        if (target != null) {
            target.addTargeter(this);
            insn().label = target.label();
        } else {
            insn().label = null;
        }
        this.target = target;
    }

    public TargetNode target() {
        return target;
    }

    @Override
    protected String toString(int tab) {
        StringBuilder sb = new StringBuilder();
        sb.append(Assembly.toString(insn()));
        sb.append(' ').append('>').append(' ');
        sb.append(target);
        for (AbstractNode n : this) {
            sb.append('\n');
            for (int i = 0; i < tab; i++) {
                sb.append('\t');
            }
            sb.append(n.toString(tab + 1));
        }
        return sb.toString();
    }
}