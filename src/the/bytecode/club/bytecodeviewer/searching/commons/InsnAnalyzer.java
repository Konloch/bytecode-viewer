package the.bytecode.club.bytecodeviewer.searching.commons;

import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * 
 *  Bytecode instruction search baseclass
 * 
 * @author Waterwolf
 *
 */
public interface InsnAnalyzer {
    public boolean accept(AbstractInsnNode node);
}
