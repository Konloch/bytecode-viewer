package org.objectweb.asm.commons.cfg.tree.util;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.List;

//import org.nullbool.api.obfuscation.cfg.FlowBlock;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.asm.commons.cfg.tree.node.ArithmeticNode;
import org.objectweb.asm.commons.cfg.tree.node.ConstantNode;
import org.objectweb.asm.commons.cfg.tree.node.ConversionNode;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.IincNode;
import org.objectweb.asm.commons.cfg.tree.node.JumpNode;
import org.objectweb.asm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.NumberNode;
import org.objectweb.asm.commons.cfg.tree.node.TypeNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * @author Tyler Sedlar
 */
public class TreeBuilder {

    public static final int[] CDS, PDS;
    private int treeIndex = -1;
    public long create = 0;
    public long iterate = 0;

    static {
        CDS = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 4, 3, 4, 3, 3, 3, 3, 1, 2, 1, 2, 3, 2, 3, 4, 2, 2, 4, 2, 4, 2, 4, 2, 4, 2, 4, 2, 4, 2, 4, 2, 4, 2, 4, 2, 4, 1, 2, 1, 2, 2, 3, 2, 3, 2, 3, 2, 4, 2, 4, 2, 4, 0, 1, 1, 1, 2, 2, 2, 1, 1, 1, 2, 2, 2, 1, 1, 1, 4, 2, 2, 4, 4, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 1, 1, 1, 2, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0};
        PDS = new int[]{0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 0, 0, 1, 2, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 4, 4, 5, 6, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 0, 2, 1, 2, 1, 1, 2, 1, 2, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    public static TreeSize getTreeSize(AbstractInsnNode ain) {
        int c = 0, p = 0;
        if (ain instanceof InsnNode || ain instanceof IntInsnNode || ain instanceof VarInsnNode ||
                ain instanceof JumpInsnNode || ain instanceof TableSwitchInsnNode ||
                ain instanceof LookupSwitchInsnNode) {
            c = CDS[ain.opcode()];
            p = PDS[ain.opcode()];
        } else if (ain instanceof FieldInsnNode) {
            FieldInsnNode fin = (FieldInsnNode) ain;
            char d = fin.desc.charAt(0);
            switch (fin.opcode()) {
                case GETFIELD: {
                    c = 1;
                    p = d == 'D' || d == 'J' ? 2 : 1;
                    break;
                }
                case GETSTATIC: {
                    c = 0;
                    p = d == 'D' || d == 'J' ? 2 : 1;
                    break;
                }
                case PUTFIELD: {
                    c = d == 'D' || d == 'J' ? 3 : 2;
                    p = 0;
                    break;
                }
                case PUTSTATIC: {
                    c = d == 'D' || d == 'J' ? 2 : 1;
                    p = 0;
                    break;
                }
                default: {
                    c = 0;
                    p = 0;
                    break;
                }
            }
        } else if (ain instanceof MethodInsnNode) {
            MethodInsnNode min = (MethodInsnNode) ain;
            int as = Type.getArgumentsAndReturnSizes(min.desc);
            c = (as >> 2) - (min.opcode() == INVOKEDYNAMIC || min.opcode() == INVOKESTATIC ? 1 : 0);
            p = as & 0x03;
        } else if (ain instanceof LdcInsnNode) {
            Object cst = ((LdcInsnNode) ain).cst;
            p = cst instanceof Double || cst instanceof Long ? 2 : 1;
        } else if (ain instanceof MultiANewArrayInsnNode) {
            c = ((MultiANewArrayInsnNode) ain).dims;
            p = 1;
        }
        return new TreeSize(c, p);
    }

    private static AbstractNode createNode(AbstractInsnNode ain, NodeTree tree, TreeSize size) {
        int opcode = ain.opcode();
        if (ain instanceof IntInsnNode) {
            return new NumberNode(tree, ain, size.collapsing, size.producing);
        } else if (ain instanceof VarInsnNode) {
            return new VariableNode(tree, ain, size.collapsing, size.producing);
        } else if (ain instanceof JumpInsnNode) {
            return new JumpNode(tree, (JumpInsnNode) ain, size.collapsing, size.producing);
        } else if (ain instanceof FieldInsnNode) {
            return new FieldMemberNode(tree, ain, size.collapsing, size.producing);
        } else if (ain instanceof MethodInsnNode) {
            return new MethodMemberNode(tree, ain, size.collapsing, size.producing);
        } else if (ain instanceof LdcInsnNode) {
            Object cst = ((LdcInsnNode) ain).cst;
            if (cst instanceof Number) {
                return new NumberNode(tree, ain, size.collapsing, size.producing);
            } else {
                return new ConstantNode(tree, ain, size.collapsing, size.producing);
            }
        } else if (ain instanceof IincInsnNode) {
            return new IincNode(tree, ain, size.collapsing, size.producing);
        } else if (ain instanceof TypeInsnNode) {
            return new TypeNode(tree, ain, size.collapsing, size.producing);
        } else {
            if (opcode >= ICONST_M1 && opcode <= DCONST_1) {
                return new NumberNode(tree, ain, size.collapsing, size.producing);
            } else if (opcode >= I2L && opcode <= I2S) {
                return new ConversionNode(tree, ain, size.collapsing, size.producing);
            } else if (opcode >= IADD && opcode <= LXOR) {
                return new ArithmeticNode(tree, ain, size.collapsing, size.producing);
            } else {
                return new AbstractNode(tree, ain, size.collapsing, size.producing);
            }
        }
    }

    private AbstractNode iterate(List<AbstractNode> nodes) {
        if (treeIndex < 0) {
            return null;
        }
        AbstractNode node = nodes.get(treeIndex--);
        if (node.collapsed == 0) {
            return node;
        }
        int c = node.collapsed;
        while (c != 0) {
            AbstractNode n = iterate(nodes);
            if (n == null) {
                break;
            }
            int op = n.opcode();
            if (op == MONITOREXIT && node.opcode() == ATHROW)
                n.producing = 1;
            node.addFirst(n);
            int cr = c - n.producing;
            if (cr < 0) {
                node.producing += -cr;
                n.producing = 0;
                break;
            }
            c -= n.producing;
            n.producing = 0;
        }
        return node;
    }

    public NodeTree build(MethodNode mn) {
        NodeTree tree = new NodeTree(mn);
        List<AbstractNode> nodes = new ArrayList<>();
        long start = System.nanoTime();
        for (AbstractInsnNode ain : mn.instructions.toArray())
            nodes.add(createNode(ain, tree, getTreeSize(ain)));
        long end = System.nanoTime();
        create += (end - start);
        treeIndex = nodes.size() - 1;
        AbstractNode node;
        start = System.nanoTime();
        while ((node = iterate(nodes)) != null)
            tree.addFirst(node);
        end = System.nanoTime();
        iterate += (end - start);
        return tree;
    }

    public NodeTree build(Block block) {
        NodeTree tree = new NodeTree(block);
        List<AbstractNode> nodes = new ArrayList<>();
        long start = System.nanoTime();
        for (AbstractInsnNode ain : block.instructions)
            nodes.add(createNode(ain, tree, getTreeSize(ain)));
        long end = System.nanoTime();
        create += (end - start);
        treeIndex = nodes.size() - 1;
        AbstractNode node;
        start = System.nanoTime();
        while ((node = iterate(nodes)) != null)
            tree.addFirst(node);
        end = System.nanoTime();
        iterate += (end - start);
        return tree;
    }

//    public NodeTree build(MethodNode method, FlowBlock block) {
//        NodeTree tree = new NodeTree(method);
//        List<AbstractNode> nodes = new ArrayList<>();
//        long start = System.nanoTime();
//        for (AbstractInsnNode ain : block.insns())
//            nodes.add(createNode(ain, tree, getTreeSize(ain)));
//        long end = System.nanoTime();
//        create += (end - start);
//        treeIndex = nodes.size() - 1;
//        AbstractNode node;
//        start = System.nanoTime();
//        while ((node = iterate(nodes)) != null)
//            tree.addFirst(node);
//        end = System.nanoTime();
//        iterate += (end - start);
//        return tree;
//    }
}