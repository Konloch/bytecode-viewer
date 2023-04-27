package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;
import the.bytecode.club.bytecodeviewer.classparser.ClassFileParser;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

import java.util.List;

public class ClassParser extends Plugin {

    private PluginConsole pluginConsole;

    @Override
    public void execute(List<ClassNode> list) {
        if (!BytecodeViewer.isActiveResourceClass()) {
            BytecodeViewer.showMessage(TranslatedStrings.FIRST_VIEW_A_CLASS.toString());
            return;
        }

        ClassNode c = BytecodeViewer.getCurrentlyOpenedClassNode();

        ClassFileParser classFileParser = new ClassFileParser(classNodeToByte(c));

        pluginConsole = new PluginConsole("ClassParser");
        pluginConsole.setVisible(true);

        print("Parsing class: " + c.name + ".class");
        print("MAGIC VALUE: " + classFileParser.parseMagicValue());
        print("Class version: " + classFileParser.parseVersionClass());
        print("Constant pool count: " + classFileParser.parseConstantPoolCount() + " If not all constants were parsed, most likely the constant is not used in the bytecode.");
        print("Then use the javap utility to view the constant pool of a class file.");
        print("Last modified class: " + classFileParser.parseClassModificationDate());
        print("Hash sum class md5: " + classFileParser.getHash("MD5"));
        print("Hash sum class sha1: " + classFileParser.getHash("SHA-1"));
        print("Hash sum class sha256: " + classFileParser.getHash("SHA-256"));
        print("Hash sum class sha512: " + classFileParser.getHash("SHA-512"));
        print("Constant pool ->");

        classFileParser.getConstantPool().parseConstantPool();

        if(classFileParser.getConstantPool().getCpList() != null && !classFileParser.getConstantPool().getCpList().isEmpty()) {
            for(String s : classFileParser.getConstantPool().getCpList()) {
                print(s);
            }
        }
    }

    private byte[] classNodeToByte(ClassNode classNode){
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classNode.accept(cw);
        return cw.toByteArray();
    }

    public void print(String text) {
        pluginConsole.appendText(text);
        pluginConsole.repaint();
    }

}
