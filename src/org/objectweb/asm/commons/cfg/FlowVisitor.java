//TODO: fix
//package org.objectweb.asm.commons.cfg;
//
//import static org.objectweb.asm.Opcodes.ARETURN;
//import static org.objectweb.asm.Opcodes.ATHROW;
//import static org.objectweb.asm.Opcodes.DRETURN;
//import static org.objectweb.asm.Opcodes.FRETURN;
//import static org.objectweb.asm.Opcodes.GOTO;
//import static org.objectweb.asm.Opcodes.IRETURN;
//import static org.objectweb.asm.Opcodes.LRETURN;
//import static org.objectweb.asm.Opcodes.RETURN;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Stack;
//
//import org.objectweb.asm.Label;
//import org.objectweb.asm.MethodVisitor;
//import org.objectweb.asm.commons.cfg.graph.Digraph;
//import org.objectweb.asm.tree.AbstractInsnNode;
//import org.objectweb.asm.tree.FieldInsnNode;
//import org.objectweb.asm.tree.IincInsnNode;
//import org.objectweb.asm.tree.InsnNode;
//import org.objectweb.asm.tree.IntInsnNode;
//import org.objectweb.asm.tree.InvokeDynamicInsnNode;
//import org.objectweb.asm.tree.JumpInsnNode;
//import org.objectweb.asm.tree.LabelNode;
//import org.objectweb.asm.tree.LdcInsnNode;
//import org.objectweb.asm.tree.LookupSwitchInsnNode;
//import org.objectweb.asm.tree.MethodInsnNode;
//import org.objectweb.asm.tree.MethodNode;
//import org.objectweb.asm.tree.MultiANewArrayInsnNode;
//import org.objectweb.asm.tree.TableSwitchInsnNode;
//import org.objectweb.asm.tree.TypeInsnNode;
//import org.objectweb.asm.tree.VarInsnNode;
//
///**
// * @author Tyler Sedlar
// */
//public class FlowVisitor extends MethodVisitor {
//
//	private MethodNode mn;
//    private Block current = new Block(new Label());
//    public final List<Block> blocks = new ArrayList<>();
//    public final Digraph<Block, Block> graph = new Digraph<>();
//    
//    public FlowVisitor() {
//		super(Opcodes.ASM5);
//	}
//    
//    public void accept(MethodNode mn) {
//        current = new Block(new Label());
//        blocks.clear();
//        blocks.add(current);
//        graph.flush();
//        (this.mn = mn).accept(this);
//    }
//
//    /**
//     * Constructs blocks for all given labels.
//     *
//     * @param labels The labels in which to construct blocks for.
//     * @return The constructed blocks.
//     */
//    private Block[] constructAll(List<LabelNode> labels) {
//        Block[] blocks = new Block[labels.size()];
//        for (int i = 0; i < blocks.length; i++)
//            blocks[i] = construct(labels.get(i));
//        return blocks;
//    }
//
//    /**
//     * Constructs a block for the given label.
//     *
//     * @param ln The label to get a block from.
//     * @return The given label's block.
//     */
//    private Block construct(LabelNode ln) {
//        return construct(ln, true);
//    }
//
//    /**
//     * Constructs a block for the given label.
//     *
//     * @param ln The label to get a block from.
//     * @param add <t>true</t> to add the block to the next preds, otherwise <t>false.</t>
//     * @return A block for the given label.
//     */
//    private Block construct(LabelNode ln, boolean add) {
//        Label label = ln.getLabel();
//        if (!(label.info instanceof Block)) {
//            label.info = new Block(label);
//            if (add) {
//                current.next = ((Block) label.info);
//                current.next.preds.add(current.next);
//            }
//            blocks.add((Block) label.info);
//        }
//        return (Block) label.info;
//    }
//
//    @Override
//    public void visitInsn(InsnNode in) {
//        current.instructions.add(in);
//        switch (in.opcode()) {
//            case RETURN:
//            case IRETURN:
//            case ARETURN:
//            case FRETURN:
//            case DRETURN:
//            case LRETURN:
//            case ATHROW: {
//                current = construct(new LabelNode(new Label()), false);
//                break;
//            }
//        }
//    }
//
//    @Override
//    public void visitIntInsn(IntInsnNode iin) {
//        current.instructions.add(iin);
//    }
//
//    @Override
//    public void visitVarInsn(VarInsnNode vin) {
//        current.instructions.add(vin);
//    }
//
//    @Override
//    public void visitTypeInsn(TypeInsnNode tin) {
//        current.instructions.add(tin);
//    }
//
//    @Override
//    public void visitFieldInsn(FieldInsnNode fin) {
//        current.instructions.add(fin);
//    }
//
//    @Override
//    public void visitMethodInsn(MethodInsnNode min) {
//        current.instructions.add(min);
//    }
//
//    @Override
//    public void visitInvokeDynamicInsn(InvokeDynamicInsnNode idin) {
//        current.instructions.add(idin);
//    }
//
//    @Override
//    public void visitJumpInsn(JumpInsnNode jin) {
//        int opcode = jin.opcode();
//        current.target = construct(jin.label);
//        current.target.preds.add(current.target);
//        if (opcode != GOTO)
//            current.instructions.add(jin);
//        Stack<AbstractInsnNode> stack = current.stack;
//        current = construct(new LabelNode(new Label()), opcode != GOTO);
//        current.stack = stack;
//    }
//
//    @Override
//    public void visitLabel(Label label) {
//        if (label == null || label.info == null) return;
//        Stack<AbstractInsnNode> stack = current == null ? new Stack<AbstractInsnNode>() : current.stack;
//        current = construct(new LabelNode(label));
//        current.stack = stack;
//    }
//
//    @Override
//    public void visitLdcInsn(LdcInsnNode ldc) {
//        current.instructions.add(ldc);
//    }
//
//    @Override
//    public void visitIincInsn(IincInsnNode iin) {
//        current.instructions.add(iin);
//    }
//
//    @Override
//    public void visitTableSwitchInsn(TableSwitchInsnNode tsin) {
//        construct(tsin.dflt);
//        constructAll(tsin.labels);
//        current.instructions.add(tsin);
//    }
//
//    @Override
//    public void visitLookupSwitchInsn(LookupSwitchInsnNode lsin) {
//        construct(lsin.dflt);
//        constructAll(lsin.labels);
//        current.instructions.add(lsin);
//    }
//
//    @Override
//    public void visitMultiANewArrayInsn(MultiANewArrayInsnNode manain) {
//        current.instructions.add(manain);
//    }
//
//    @Override
//    public void visitEnd() {
//        List<Block> empty = new ArrayList<>();
//        for (Block block : blocks) {
//            block.owner = mn;
//            if (block.isEmpty())
//                empty.add(block);
//        }
//        blocks.removeAll(empty);
//        Collections.sort(blocks, new Comparator<Block>() {
//            @Override
//			public int compare(Block b1, Block b2) {
//                return mn.instructions.indexOf(new LabelNode(b1.label)) - mn.instructions.indexOf(new LabelNode(b2.label));
//            }
//        });
//        for (Block block : blocks) {
//            block.setIndex(blocks.indexOf(block));
//            if (!graph.containsVertex(block))
//                graph.addVertex(block);
//            if (block.target != null && block.target != block) {
//                if (!graph.containsVertex(block.target))
//                    graph.addVertex(block.target);
//                graph.addEdge(block, block.target);
//            }
//            if (block.next != null) {
//                if (!graph.containsVertex(block.next))
//                    graph.addVertex(block.next);
//                graph.addEdge(block, block.next);
//            }
//        }
//    }
//}
