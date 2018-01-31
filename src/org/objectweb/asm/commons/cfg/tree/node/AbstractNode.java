package org.objectweb.asm.commons.cfg.tree.node;

import static org.objectweb.asm.tree.AbstractInsnNode.FIELD_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.FRAME;
import static org.objectweb.asm.tree.AbstractInsnNode.IINC_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.INT_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.JUMP_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.LABEL;
import static org.objectweb.asm.tree.AbstractInsnNode.LDC_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.LINE;
import static org.objectweb.asm.tree.AbstractInsnNode.LOOKUPSWITCH_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.METHOD_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.MULTIANEWARRAY_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.TABLESWITCH_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.TYPE_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.VAR_INSN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.Tree;
import org.objectweb.asm.commons.util.Assembly;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class AbstractNode extends Tree<AbstractNode> implements Opcodes {

    public static final String CHILD = ">";
    public static final String NUMBER = "#";
    public static final String[] QUERIES = {NUMBER};

    public static final String ARITHMETIC_NODE = "ARITHMETIC";
    public static final String[] NODE_QUERIES = {ARITHMETIC_NODE};

    public static final Class<?> ARITHMETIC_NODE_CLASS = ArithmeticNode.class;
    public static final Class<?>[] NODE_QUERY_CLASSES = {ARITHMETIC_NODE_CLASS};

    public int collapsed, producing;

    private NodeTree tree;
    private AbstractInsnNode insn;
    private int produceCount;
    private boolean handler;

    private AbstractInsnNode[] instructions;

    public AbstractNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
        this.tree = tree;
        this.insn = insn;
        this.collapsed = collapsed;
        this.producing = (produceCount = producing);
    }

    public ClassNode caller() {
        return tree.method().owner;
    }

    public void accept(NodeVisitor nv) {
        nv.visitAny(this);

        switch (insn.type()) {
            case INSN: {
                if (opcode() >= ICONST_M1 && opcode() <= DCONST_1) {
                    nv.visitNumber((NumberNode) this);
                } else if (opcode() >= I2L && opcode() <= I2S) {
                    nv.visitConversion((ConversionNode) this);
                } else if (opcode() >= IADD && opcode() <= LXOR) {
                    nv.visitOperation((ArithmeticNode) this);
                } else {
                    nv.visit(this);
                }
                break;
            }
            case INT_INSN: {
                nv.visitNumber((NumberNode) this);
                break;
            }
            case VAR_INSN: {
                nv.visitVariable((VariableNode) this);
                break;
            }
            case TYPE_INSN: {
                nv.visitType((TypeNode) this);
                break;
            }
            case FIELD_INSN: {
                nv.visitField((FieldMemberNode) this);
                break;
            }
            case METHOD_INSN: {
                nv.visitMethod((MethodMemberNode) this);
                break;
            }
            case JUMP_INSN: {
                nv.visitJump((JumpNode) this);
                break;
            }
            case LABEL: {
                nv.visitLabel(this);
                break;
            }
            case LDC_INSN: {
                Object cst = ((LdcInsnNode) insn()).cst;
                if (cst != null && cst instanceof Number) {
                    nv.visitNumber((NumberNode) this);
                } else {
                    nv.visitConstant((ConstantNode) this);
                }
                break;
            }
            case IINC_INSN: {
                nv.visitIinc((IincNode) this);
                break;
            }
            case TABLESWITCH_INSN: {
                nv.visitTableSwitch(this);
                break;
            }
            case LOOKUPSWITCH_INSN: {
                nv.visitLookupSwitch(this);
                break;
            }
            case MULTIANEWARRAY_INSN: {
                nv.visitMultiANewArray(this);
                break;
            }
            case FRAME: {
                nv.visitFrame(this);
                break;
            }
            case LINE: {
                nv.visitLine(this);
                break;
            }
        }
    }

    public AbstractInsnNode[] collapse() {
        if (instructions != null) {
            return instructions;
        }
        instructions = new AbstractInsnNode[total()];
        int i = 0;
        for (AbstractNode n : this) {
            AbstractInsnNode[] nodes = n.collapse();
            System.arraycopy(nodes, 0, instructions, i, nodes.length);
            i += nodes.length;
        }
        if (instructions.length - i != 1) {
            throw new RuntimeException();
        }
        instructions[i] = insn();
        return instructions;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractNode && Assembly.instructionsEqual(((AbstractNode) obj).insn(), insn());
    }

    public AbstractInsnNode insn() {
        return insn;
    }

    public boolean isHandler() {
        return handler;
    }

    public MethodNode method() {
        return tree.method();
    }

    public int opcode() {
        return insn != null ? insn.opcode() : -1;
    }

    public void pop() {
        parent().remove(this);
    }

    public boolean hasFirst() {
        return first() != null;
    }

    public AbstractNode first() {
        return child(0);
    }

    public AbstractNode child(int idx) {
        int i = 0;
        for (AbstractNode n : this) {
            if (i == idx) return n;
            i++;
        }
        return null;
    }

    public AbstractNode[] producing() {
        AbstractNode[] nodes = new AbstractNode[size()];
        int i = 0;
        for (AbstractNode n : this) {
            if (n.produceCount > 0) {
                nodes[i++] = n;
            }
        }
        return Arrays.copyOf(nodes, i);
    }

    public void delete() {
        parent().remove(this);
    }

    public void setHandler(boolean handler) {
        this.handler = handler;
    }

    public void setInstruction(AbstractInsnNode insn) {
        this.insn = insn;
    }

    @Override
    public String toString() {
        return toString(1);
    }

    protected String toString(int tab) {
        StringBuilder sb = new StringBuilder();
        sb.append(Assembly.toString(insn));
        for (AbstractNode n : this) {
            sb.append('\n');
            for (int i = 0; i < tab; i++) {
                sb.append('\t');
            }
            sb.append(n.toString(tab + 1));
        }
        return sb.toString();
    }

    public int total() {
        int size = 1;
        for (AbstractNode n : this) {
            size += n.total();
        }
        return size;
    }

    public int children() {
        return producing().length;
    }

    public NodeTree tree() {
        return tree;
    }

    public int index() {
        return method().instructions.indexOf(insn());//insn.insnIndex;
    }

    public AbstractNode first(int opcode) {
        for (AbstractNode n : this) {
            if (n.opcode() == opcode) return n;
        }
        return null;
    }

    public AbstractNode find(int opcode, int index) {
        int i = 0;
        for (AbstractNode n : this) {
            if (n.opcode() == opcode) {
                if (i++ == index) return n;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractNode> T first(Class<? extends AbstractNode> clazz) {
        for (AbstractNode n : this) {
            if (n.getClass().equals(clazz)) return (T) n;
        }
        return null;
    }

    public NumberNode firstNumber() {
        return first(NumberNode.class);
    }

    public ArithmeticNode firstOperation() {
        return first(ArithmeticNode.class);
    }

    public ReferenceNode firstReference() {
        return first(ReferenceNode.class);
    }

    public FieldMemberNode firstField() {
        for (AbstractNode n : this) {
            if (n instanceof ReferenceNode) {
                if (n.insn() instanceof FieldInsnNode) return (FieldMemberNode) n;
            }
        }
        return null;
    }

    public MethodMemberNode firstMethod() {
        for (AbstractNode n : this) {
            if (n instanceof ReferenceNode) {
                if (n.insn() instanceof MethodInsnNode) return (MethodMemberNode) n;
            }
        }
        return null;
    }

    public VariableNode firstVariable() {
        return first(VariableNode.class);
    }

    public ConstantNode firstConstant() {
        return first(ConstantNode.class);
    }

    public TypeNode firstType() {
        return first(TypeNode.class);
    }

    public JumpNode firstJump() {
        return first(JumpNode.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractNode> T next(Class<? extends AbstractNode> clazz, int max) {
        int i = 0;
        AbstractNode next = this;
        while ((next = next.next()) != null && i++ < max) {
            if (next.getClass().equals(clazz)) return (T) next;
        }
        return null;
    }

    public NumberNode nextNumber() {
        return next(NumberNode.class, 3);
    }

    public ArithmeticNode nextOperation() {
        return next(ArithmeticNode.class, 3);
    }

    public FieldMemberNode nextField(int max) {
        return next(FieldMemberNode.class, max);
    }

    public FieldMemberNode nextField() {
        return nextField(1);
    }

    public MethodMemberNode nextMethod(int max) {
        return next(MethodMemberNode.class, max);
    }

    public ReferenceNode nextMethod() {
        return nextMethod(1);
    }

    public JumpNode nextJump(int max) {
        return next(JumpNode.class, max);
    }

    public JumpNode nextJump() {
        return nextJump(1);
    }

    public VariableNode nextVariable(int max) {
        return next(VariableNode.class, max);
    }

    public VariableNode nextVariable() {
        return nextVariable(1);
    }

    public ConstantNode nextConstant(int max) {
        return next(ConstantNode.class, max);
    }

    public ConstantNode nextConstant() {
        return nextConstant(1);
    }

    public TypeNode nextType(int max) {
        return next(TypeNode.class, max);
    }


    public TypeNode nextType() {
        return nextType(1);
    }

    public AbstractNode next(int opcode, int max) {
        int i = 0;
        AbstractNode next = this;
        while ((next = next.next()) != null && i++ < max) {
            if (next.opcode() == opcode) return next;
        }
        return null;
    }

    public AbstractNode next(int opcode) {
        return next(opcode, 5);
    }

    public AbstractNode previous(int opcode, int max) {
        int i = 0;
        AbstractNode prev = this;
        while ((prev = prev.previous()) != null && i++ < max) {
            if (prev.opcode() == opcode) return prev;
        }
        return null;
    }

    public AbstractNode previous(int opcode) {
        return previous(opcode, 5);
    }

    public List<AbstractNode> findChildren(int opcode) {
        List<AbstractNode> children = new ArrayList<>();
        for (AbstractNode n : this) {
            if (n.opcode() == opcode) children.add(n);
        }
        return !children.isEmpty() ? children : null;
    }

    public List<AbstractNode> layerAll(int... opcodes) {
        List<AbstractNode> children = findChildren(opcodes[0]);
        if (children == null) return null;
        if (opcodes.length == 1) return children;
        for (int i = 1; i < opcodes.length; i++) {
            List<AbstractNode> next = new ArrayList<>();
            for (AbstractNode n : children) {
                List<AbstractNode> match = n.findChildren(opcodes[i]);
                if (match == null) continue;
                next.addAll(match);
            }
            if (next.isEmpty()) {
                return null;
            } else {
                children.clear();
                children.addAll(next);
            }
        }
        return children;
    }

    public AbstractNode layer(int... opcodes) {
        List<AbstractNode> nodes = layerAll(opcodes);
        return nodes != null ? nodes.get(0) : null;
    }

    public AbstractNode preLayer(int... opcodes) {
        AbstractNode node = this;
        for (int opcode : opcodes) {
            node = node.parent();
            if (node == null || node.opcode() != opcode) return null;
        }
        return node;
    }

    public boolean hasChild(int opcode) {
        return first(opcode) != null;
    }

    public String opname() {
        try {
            return Assembly.OPCODES[opcode()];
        } catch (IndexOutOfBoundsException e) {
            try {
                return insn().getClass().getSimpleName();
            } catch (Exception err) {
                return insn().toString();
            }
        }
    }
}