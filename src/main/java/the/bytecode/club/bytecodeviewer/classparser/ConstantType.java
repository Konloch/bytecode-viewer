package the.bytecode.club.bytecodeviewer.classparser;

public interface ConstantType {

    byte CONSTANT_Utf8 = 1;
    byte CONSTANT_Class = 7;
    byte CONSTANT_Fieldref = 9;
    byte CONSTANT_Methodref = 10;
    byte CONSTANT_InterfaceMethodref = 11;
    byte CONSTANT_String = 8;
    byte CONSTANT_Integer = 3;
    byte CONSTANT_Float = 4;
    byte CONSTANT_Long = 5;
    byte CONSTANT_Double = 6;
    byte CONSTANT_NameAndType = 12;
    byte CONSTANT_MethodHandle = 15;
    byte CONSTANT_MethodType = 16;
    byte CONSTANT_InvokeDynamic = 18;
}
