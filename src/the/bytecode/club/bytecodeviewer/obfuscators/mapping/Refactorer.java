package the.bytecode.club.bytecodeviewer.obfuscators.mapping;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

/**
 * @author sc4re
 */
public class Refactorer {

    protected HookMap hooks;

    public Refactorer() {
        hooks = new HookMap();
    }
    
    public HookMap getHooks() {
        return hooks;
    }

    public void run() {
        if (getHooks() == null)
            return;
        
        RefactorMapper mapper = new RefactorMapper(getHooks());
        Map<String, ClassNode> refactored = new HashMap<>();
        for (ClassNode cn : BytecodeViewer.getLoadedClasses()) {
            String oldName = cn.name;
            ClassReader cr = new ClassReader(getClassNodeBytes(cn));
            ClassWriter cw = new ClassWriter(cr, 0);
            RemappingClassAdapter rca = new RemappingClassAdapter(cw, mapper);
            cr.accept(rca, ClassReader.EXPAND_FRAMES);
            cr = new ClassReader(cw.toByteArray());
            cn  = new ClassNode();
            cr.accept(cn, 0);
            refactored.put(oldName, cn);
        }
        for (Map.Entry<String, ClassNode> factor : refactored.entrySet()) {
            BytecodeViewer.relocate(factor.getKey(), factor.getValue());
        }
        mapper.printMap();
    }

    private byte[] getClassNodeBytes(ClassNode cn) {
        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        byte[] b = cw.toByteArray();
        return b;
    }
}
