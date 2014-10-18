package the.bytecode.club.bytecodeviewer.searching;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Used to update the search pane that there's been a result found.
 * 
 * @author WaterWolf
 *
 */

public interface SearchResultNotifier {
    public void notifyOfResult(ClassNode clazz, MethodNode method,
            AbstractInsnNode insn);
}
