package org.objectweb.asm.commons.cfg.tree.util;

/**
 * @author Tyler Sedlar
 */
public class TreeSize {

    public final int collapsing, producing;

    public TreeSize(int collapsing, int producing) {
        this.collapsing = collapsing;
        this.producing = producing;
    }

    @Override
    public String toString() {
        return "[" + collapsing + "][" + producing + "]";
    }
}