package the.bytecode.club.bytecodeviewer.deobfuscator;

import org.junit.Test;
import org.objectweb.asm.tree.ClassNode;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class DeobfuscatorTest {
    @Test
    public void testGenerateNewName() {
        Deobfuscator deobfuscator = new Deobfuscator();
        List<ClassNode> nodes = new ArrayList<>();
        // Concrete class
        ClassNode classNode = new ClassNode();
        classNode.name = "a";
        classNode.access = 0; // No abstract/interface
        nodes.add(classNode);
        // Abstract class
        ClassNode abstractNode = new ClassNode();
        abstractNode.name = "b";
        abstractNode.access = org.objectweb.asm.Opcodes.ACC_ABSTRACT;
        nodes.add(abstractNode);
        // Interface
        ClassNode interfaceNode = new ClassNode();
        interfaceNode.name = "c";
        interfaceNode.access = org.objectweb.asm.Opcodes.ACC_INTERFACE;
        nodes.add(interfaceNode);
        // Simulate BytecodeViewer.getLoadedClasses()
        for (ClassNode cn : nodes) {
            String newName = deobfuscator.generateNewName(cn);
            if (cn.access == 0) {
                assertTrue(newName.startsWith("C"));
            } else if ((cn.access & org.objectweb.asm.Opcodes.ACC_ABSTRACT) != 0) {
                assertTrue(newName.startsWith("AC"));
            } else if ((cn.access & org.objectweb.asm.Opcodes.ACC_INTERFACE) != 0) {
                assertTrue(newName.startsWith("I"));
            }
        }
    }
}
