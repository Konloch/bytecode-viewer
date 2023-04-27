package the.bytecode.club.bytecodeviewer.classparser;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ConstantParser {

    private final byte[] classBytes;
    private final int constantPoolCount;

    private final List<String> cpList = new ArrayList<>();

    public ConstantParser(byte[] classBytes, int constantPoolCount){
        this.classBytes = classBytes;
        this.constantPoolCount = constantPoolCount;
    }

    public void parseConstantPool(){
        ByteBuffer buffer = ByteBuffer.wrap(classBytes);
        buffer.position(10);

        for (int i = 1; i < constantPoolCount; i++) {
            int tag = buffer.get() & 0xFF;
            switch(tag){
                case ConstantType.CONSTANT_Utf8:
                    int length = buffer.getShort() & 0xFFFF;
                    if (buffer.remaining() < length) {
                        throw new BufferUnderflowException();
                    }
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
                    int classIndex = buffer.getShort() & 0xFFFF;
                    int nameAndTypeIndex = buffer.getShort() & 0xFFFF;
                    cpList.add("[" + i + "] CONSTANT_Fieldref: #" + classIndex + ".#" + nameAndTypeIndex);
                    break;
                case ConstantType.CONSTANT_Methodref:
                    int classIndex1 = buffer.getShort() & 0xFFFF;
                    int nameAndTypeIndex1 = buffer.getShort() & 0xFFFF;
                    cpList.add("[" + i + "] CONSTANT_Methodref: #" + classIndex1 + ".#" + nameAndTypeIndex1);
                    break;
                case ConstantType.CONSTANT_InterfaceMethodref:
                    int classIndex2 = buffer.getShort() & 0xFFFF;
                    int nameAndTypeIndex2 = buffer.getShort() & 0xFFFF;
                    cpList.add("[" + i + "] CONSTANT_InterfaceMethodref: #" + classIndex2 + ".#" + nameAndTypeIndex2);
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

    public List<String> getCpList() {
        return cpList;
    }
}
