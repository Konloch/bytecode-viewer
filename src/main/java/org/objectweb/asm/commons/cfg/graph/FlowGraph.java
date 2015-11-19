package org.objectweb.asm.commons.cfg.graph;

import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Tyler Sedlar
 */
public class FlowGraph extends Digraph<Block, Block> {

    private final MethodNode mn;

    public FlowGraph(MethodNode mn) {
        this.mn = mn;
    }

    public MethodNode method() {
        return mn;
    }
}
