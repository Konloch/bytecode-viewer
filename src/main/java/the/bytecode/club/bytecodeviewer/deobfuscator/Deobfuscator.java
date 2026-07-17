package the.bytecode.club.bytecodeviewer.deobfuscator;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.ASMResourceUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * Deobfuscator logic for renaming obfuscated class names to semantic names.
 * Prefixes: C for Class, AC for Abstract Class, I for Interface.
 */
public class Deobfuscator {
    private final Map<String, String> nameMapping = new HashMap<>();
    private int classCount = 1;
    private int abstractClassCount = 1;
    private int interfaceCount = 1;

    public void run() {
        for (ClassNode cn : BytecodeViewer.getLoadedClasses()) {
            String newName = generateNewName(cn);
            // Handle inner/anonymous classes
            if (cn.name.contains("$")) {
                // Split outer and inner
                String[] parts = cn.name.split("\\$");
                String outer = parts[0];
                String inner = parts[1];
                String outerNew = nameMapping.getOrDefault(outer, generateNewName(cn));
                // If anonymous (numeric), keep numeric
                if (inner.matches("\\d+")) {
                    newName = outerNew + "$" + inner;
                } else {
                    newName = outerNew + "$" + inner;
                }
            }
            // Optionally handle package renaming (preserve for now)
            nameMapping.put(cn.name, newName);
            ASMResourceUtil.renameClassNode(cn.name, newName);
        }
    }

    String generateNewName(ClassNode cn) {
        int access = cn.access;
        if ((access & org.objectweb.asm.Opcodes.ACC_INTERFACE) != 0) {
            return "I" + String.format("%03d", interfaceCount++);
        } else if ((access & org.objectweb.asm.Opcodes.ACC_ABSTRACT) != 0) {
            return "AC" + String.format("%03d", abstractClassCount++);
        } else {
            return "C" + String.format("%03d", classCount++);
        }
    }

    public Map<String, String> getNameMapping() {
        return nameMapping;
    }
}
