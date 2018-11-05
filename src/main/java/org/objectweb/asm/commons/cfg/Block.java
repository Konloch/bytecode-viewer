package org.objectweb.asm.commons.cfg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.objectweb.asm.Label;
import org.objectweb.asm.commons.cfg.query.InsnQuery;
import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.commons.cfg.tree.util.TreeBuilder;
import org.objectweb.asm.commons.util.Assembly;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Tyler Sedlar
 */
public class Block implements Comparable<Block> {

    public static boolean PRINT_INSNS = true;

    public MethodNode owner = null;

    public final Label label;
    public final List<AbstractInsnNode> instructions = new LinkedList<>();
    public final List<Block> preds = new ArrayList<>();

    public Block next, target;

    public Stack<AbstractInsnNode> stack = new Stack<>();

    private NodeTree tree;

    private int index = -1;

    /**
     * Constructs a block for the given label.
     *
     * @param label The label in which to create a block from.
     */
    public Block(Label label) {
        this.label = label;
        this.instructions.add(new LabelNode(label));
    }

    /**
     * Constructs a NodeTree for the current block.
     */
    public NodeTree tree() {
        if (tree != null) return tree;
        return (tree = new TreeBuilder().build(this));
    }

    /**
     * Sets this block's index.
     *
     * @param index The index to set.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Checks if the block is empty.
     *
     * @return <t>true</t> if the block is empty, otherwise <t>false.</t>
     */
    public boolean isEmpty() {
        return preds.isEmpty() && instructions.size() <= 1;
    }

    /**
     * Gets the amount of times the given opcodes has been matched
     *
     * @param opcode The opcode to match
     * @return The amount of times the given opcode has been matched.
     */
    public int count(int opcode) {
        int count = 0;
        for (AbstractInsnNode ain : instructions) {
            if (ain.opcode() == opcode)
                count++;
        }
        return count;
    }

    /**
     * Gets the amount of times the given query has been matched
     *
     * @param query The query to match
     * @return The amount of times the given query has been matched.
     */
    public int count(InsnQuery query) {
        int count = 0;
        for (AbstractInsnNode ain : instructions) {
            if (query.matches(ain))
                count++;
        }
        return count;
    }

    /**
     * Gets the matched instruction at the given index
     *
     * @param opcode The opcode of the instruction to match
     * @param index  The index to match at
     * @return The matched instruction at the given index
     */
    public AbstractInsnNode get(int opcode, int index) {
        int i = 0;
        for (AbstractInsnNode ain : instructions) {
            if (ain.opcode() == opcode) {
                if (i == index)
                    return ain;
                i++;
            }
        }
        return null;
    }

    /**
     * Gets the first matched instruction
     *
     * @param opcode The opcode of the instruction to match
     * @return The first matched instruction
     */
    public AbstractInsnNode get(int opcode) {
        return get(opcode, 0);
    }

    /**
     * Gets the matched instruction at the given index
     *
     * @param query The query to match
     * @param index The index to match at
     * @return The matched instruction at the given index
     */
    public AbstractInsnNode get(InsnQuery query, int index) {
        int i = 0;
        for (AbstractInsnNode ain : instructions) {
            if (query.matches(ain)) {
                if (i == index)
                    return ain;
                i++;
            }
        }
        return null;
    }

    /**
     * Gets the first matched instruction
     *
     * @param query The query to match
     * @return The first matched instruction
     */
    public AbstractInsnNode get(InsnQuery query) {
        return get(query, 0);
    }

    @Override
    public int compareTo(Block block) {
        return index > block.index ? 1 : -1;
    }

    public int size() {
        //there is always 1 label which identifies the block, so we dont count that.
        return instructions.size() - 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(headerString()).append(String.format(" (len=%d)", size()));

        if (PRINT_INSNS && size() > 0) {
            sb.append(System.lineSeparator());

            Iterator<AbstractInsnNode> it = instructions.iterator();
            while (it.hasNext()) {
                AbstractInsnNode ain = it.next();
                if (!(ain instanceof LabelNode)) {
                    if (it.hasNext()) {
                        sb.append(String.format("   %s%n", Assembly.toString(ain)));
                    } else {
                        sb.append(String.format("   %s", Assembly.toString(ain)));
                    }
                }
            }
        }

        sb.append(System.lineSeparator());

        for (Block b : preds) {
            sb.append("   pred: ").append(b.headerString()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    public String headerString() {
        return String.format("Block #%d", index);
    }
}