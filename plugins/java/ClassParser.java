import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class Parser
 *
 * @author Damir37
 */
public class ClassParser extends Plugin
{

    private PluginConsole pluginConsole;

    @Override
    public void execute(List<ClassNode> list)
    {
        if(!BytecodeViewer.isActiveClassActive())
        {
            BytecodeViewer.showMessage("Open A Classfile First");
            return;
        }

        ClassNode c = BytecodeViewer.getCurrentlyOpenedClassNode();

        ClassFileParser classFileParser = new ClassFileParser(classNodeToByte(c));

        pluginConsole = new PluginConsole("ClassParser");
        pluginConsole.setVisible(true);

        print("Parsing class: " + c.name + ".class");
        print("MAGIC VALUE: " + classFileParser.parseMagicValue());
        print("Class version: " + classFileParser.parseVersionClass());
        print("Constant pool count: " + classFileParser.parseConstantPoolCount()
            + " If not all constants were parsed, most likely the constant is not used in the bytecode.");
        print("Then use the javap utility to view the constant pool of a class file.");
        print("Last modified class: " + classFileParser.parseClassModificationDate());
        print("Hash sum class md5: " + classFileParser.getHash("MD5"));
        print("Hash sum class sha1: " + classFileParser.getHash("SHA-1"));
        print("Hash sum class sha256: " + classFileParser.getHash("SHA-256"));
        print("Hash sum class sha512: " + classFileParser.getHash("SHA-512"));
        print("Constant pool ->");

        classFileParser.getConstantPool().parseConstantPool();

        if (classFileParser.getConstantPool().getCpList() != null && !classFileParser.getConstantPool().getCpList().isEmpty())
        {
            for (String s : classFileParser.getConstantPool().getCpList())
            {
                print(s);
            }
        }
    }

    private byte[] classNodeToByte(ClassNode classNode)
    {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classNode.accept(cw);
        return cw.toByteArray();
    }

    public void print(String text)
    {
        pluginConsole.appendText(text);
        pluginConsole.repaint();
    }

    private static class ClassFileParser
    {
        private final ByteBuffer buffer;
        private final ConstantParser cpParser;

        public ClassFileParser(byte[] classBytes)
        {
            this.buffer = ByteBuffer.wrap(classBytes);
            cpParser = new ConstantParser(buffer, parseConstantPoolCount());
        }

        public String parseMagicValue()
        {
            buffer.position(0);
            int magicValue = buffer.getInt();
            return "0x" + Integer.toHexString(magicValue).toUpperCase();
        }

        public ClassVersion parseVersionClass()
        {
            buffer.position(4);
            int minor = buffer.getShort() & 0xFFFF;
            int major = buffer.getShort() & 0xFFFF;
            return ClassVersion.check(major, minor);
        }

        public Date parseClassModificationDate()
        {
            buffer.position(8);
            long modificationTime = buffer.getInt() & 0xFFFFFFFFL;
            return new Date(modificationTime * 1000L);
        }

        public int parseConstantPoolCount()
        {
            buffer.position(8);
            return buffer.getShort() & 0xFFFF;
        }

        public String getHash(String algorithm)
        {
            try
            {
                MessageDigest md = MessageDigest.getInstance(algorithm);
                md.update(buffer.array());
                byte[] digest = md.digest();
                return convertToHex(digest);
            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
            }

            return "null";
        }

        private String convertToHex(byte[] bytes)
        {
            StringBuilder hexString = new StringBuilder();

            for (byte b : bytes)
            {
                hexString.append(String.format("%02X", b));
            }

            return hexString.toString();
        }

        public ConstantParser getConstantPool()
        {
            return cpParser;
        }
    }

    private enum ClassVersion
    {
        UNKNOWN(0, 0),
        JAVA_1_1(45, 3),
        JAVA_1_2(46, 0),
        JAVA_1_3(47, 0),
        JAVA_1_4(48, 0),
        JAVA_5(49, 0),
        JAVA_6(50, 0),
        JAVA_7(51, 0),
        JAVA_8(52, 0),
        JAVA_9(53, 0),
        JAVA_10(54, 0),
        AVA_11(55, 0),
        JAVA_12(56, 0),
        JAVA_13(57, 0),
        JAVA_14(58, 0),
        JAVA_15(59, 0),
        JAVA_16(60, 0),
        JAVA_17(61, 0),
        JAVA_18(62, 0),
        JAVA_19(63, 0),
        JAVA_20(64, 0),
        JAVA_21(65, 0),
        JAVA_22(66, 0),
        JAVA_23(67, 0),
        JAVA_24(68, 0),
        JAVA_25(69, 0),
        JAVA_26(70, 0),
        JAVA_27(71, 0),
        JAVA_28(72, 0),
        JAVA_29(73, 0),
        JAVA_30(74, 0);

        public final int major;
        public final int minor;

        ClassVersion(int major, int minor)
        {
            this.major = major;
            this.minor = minor;
        }

        public static ClassVersion check(int major, int minor)
        {
            for (ClassVersion v : ClassVersion.values())
            {
                if (v.major == major && v.minor == minor)
                    return v;
            }

            return UNKNOWN;
        }
    }

    private static class ConstantParser
    {
        private final ByteBuffer buffer;
        private final int constantPoolCount;
        private final List<String> cpList = new ArrayList<String>();

        public ConstantParser(ByteBuffer buffer, int constantPoolCount)
        {
            this.buffer = buffer;
            this.constantPoolCount = constantPoolCount;
        }

        public void parseConstantPool()
        {
            buffer.position(10);

            for (int i = 1; i < constantPoolCount; i++)
            {
                int tag = buffer.get() & 0xFF;
                switch (tag)
                {
                    case ConstantType.CONSTANT_Utf8:
                        int length = buffer.getShort() & 0xFFFF;
                        byte[] bytes = new byte[length];
                        buffer.get(bytes);
                        String string = new String(bytes);
                        cpList.add("[" + i + "] CONSTANT_Utf8: " + string);
                        break;

                    case ConstantType.CONSTANT_Integer:
                        int value = buffer.getInt();
                        cpList.add("[" + i + "] CONSTANT_Integer: " + value);
                        break;

                    case ConstantType.CONSTANT_Float:
                        float floatValue = buffer.getFloat();
                        cpList.add("[" + i + "] CONSTANT_Float: " + floatValue);
                        break;

                    case ConstantType.CONSTANT_Long:
                        long longValue = buffer.getLong();
                        cpList.add("[" + i + "] CONSTANT_Long: " + longValue);
                        i++;
                        break;

                    case ConstantType.CONSTANT_Double:
                        double doubleValue = buffer.getDouble();
                        cpList.add("[" + i + "] CONSTANT_Double: " + doubleValue);
                        i++;
                        break;

                    case ConstantType.CONSTANT_Class:
                        int nameIndex = buffer.getShort() & 0xFFFF;
                        cpList.add("[" + i + "] CONSTANT_Class: #" + nameIndex);
                        break;

                    case ConstantType.CONSTANT_String:
                        int stringIndex = buffer.getShort() & 0xFFFF;
                        cpList.add("[" + i + "] CONSTANT_String: #" + stringIndex);
                        break;

                    case ConstantType.CONSTANT_Fieldref:
                    case ConstantType.CONSTANT_Methodref:
                    case ConstantType.CONSTANT_InterfaceMethodref:
                        int classIndex = buffer.getShort() & 0xFFFF;
                        int nameAndTypeIndex = buffer.getShort() & 0xFFFF;
                        cpList.add("[" + i + "] CONSTANT_" + getRefTypeName(tag) + ": #" + classIndex + ".#" + nameAndTypeIndex);
                        break;

                    case ConstantType.CONSTANT_NameAndType:
                        int nameIndex1 = buffer.getShort() & 0xFFFF;
                        int descriptorIndex = buffer.getShort() & 0xFFFF;
                        cpList.add("[" + i + "] CONSTANT_NameAndType: #" + nameIndex1 + ":#" + descriptorIndex);
                        break;

                    case ConstantType.CONSTANT_MethodHandle:
                        int referenceKind = buffer.get() & 0xFF;
                        int referenceIndex = buffer.getShort() & 0xFFFF;
                        cpList.add("[" + i + "] CONSTANT_MethodHandle: " + referenceKind + ":#" + referenceIndex);
                        break;

                    case ConstantType.CONSTANT_MethodType:
                        int descriptorIndex1 = buffer.getShort() & 0xFFFF;
                        cpList.add("[" + i + "] CONSTANT_MethodType: #" + descriptorIndex1);
                        break;

                    case ConstantType.CONSTANT_InvokeDynamic:
                        int bootstrapMethodAttrIndex = buffer.getShort() & 0xFFFF;
                        int nameAndTypeIndex3 = buffer.getShort() & 0xFFFF;
                        cpList.add("[" + i + "] CONSTANT_InvokeDynamic: #" + bootstrapMethodAttrIndex + ":#" + nameAndTypeIndex3);
                        break;

                    default:
                        throw new IllegalArgumentException("Unknown constant pool tag " + tag);
                }
            }
        }

        private String getRefTypeName(int tag)
        {
            switch (tag)
            {
                case ConstantType.CONSTANT_Fieldref:
                    return "Fieldref";
                case ConstantType.CONSTANT_Methodref:
                    return "Methodref";
                case ConstantType.CONSTANT_InterfaceMethodref:
                    return "InterfaceMethodref";
                default:
                    return "Unknown";
            }
        }

        public List<String> getCpList()
        {
            return cpList;
        }
    }

    private interface ConstantType
    {
        public static final byte CONSTANT_Utf8 = 1;
        public static final byte CONSTANT_Class = 7;
        public static final byte CONSTANT_Fieldref = 9;
        public static final byte CONSTANT_Methodref = 10;
        public static final byte CONSTANT_InterfaceMethodref = 11;
        public static final byte CONSTANT_String = 8;
        public static final byte CONSTANT_Integer = 3;
        public static final byte CONSTANT_Float = 4;
        public static final byte CONSTANT_Long = 5;
        public static final byte CONSTANT_Double = 6;
        public static final byte CONSTANT_NameAndType = 12;
        public static final byte CONSTANT_MethodHandle = 15;
        public static final byte CONSTANT_MethodType = 16;
        public static final byte CONSTANT_InvokeDynamic = 18;
    }
}
