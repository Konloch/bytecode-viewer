package the.bytecode.club.bytecodeviewer.decompilers.jdgui;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.jd.core.v1.model.classfile.constant.Constant;
import org.jd.core.v1.model.classfile.constant.ConstantClass;
import org.jd.core.v1.model.classfile.constant.ConstantUtf8;
import org.jd.core.v1.service.deserializer.classfile.ClassFileFormatException;
import org.jd.core.v1.service.deserializer.classfile.ClassFileReader;


public class JDGUIClassFileUtil
{
    public static final char INTERNAL_PACKAGE_SEPARATOR = '/';
    public static final String CLASS_FILE_SUFFIX = ".class";

    /*
     * Lecture rapide de la structure de la classe et extraction du nom du
     * repoertoire de base.
     */
    public static String ExtractDirectoryPath(String pathToClass) {
        String directoryPath;

        try (FileInputStream fis = new FileInputStream(pathToClass);
             BufferedInputStream bis = new BufferedInputStream(fis);
             DataInputStream dis = new DataInputStream(bis)) {
            int magic = dis.readInt();
            if (magic != ClassFileReader.JAVA_MAGIC_NUMBER) {
                throw new ClassFileFormatException("Invalid Java .class file");
            }

            /* int minor_version = */
            dis.readUnsignedShort();
            /* int major_version = */
            dis.readUnsignedShort();

            Constant[] constants = DeserializeConstants(dis);

            /* int access_flags = */
            dis.readUnsignedShort();
            int this_class = dis.readUnsignedShort();

            if (this_class > constants.length) {
                throw new ClassFileFormatException("Unknown Java structure");
            }
            Constant c = constants[this_class];
            if ((c == null) || (c.getTag() != Constant.CONSTANT_Class)) {
                throw new ClassFileFormatException("Invalid constant pool");
            }

            c = constants[((ConstantClass) c).getNameIndex()];
            if ((c == null) || (c.getTag() != Constant.CONSTANT_Utf8)) {
                throw new ClassFileFormatException("Invalid constant pool");
            }

            String internalClassName = ((ConstantUtf8) c).getValue();
            String pathSuffix = internalClassName.replace(
                    INTERNAL_PACKAGE_SEPARATOR, File.separatorChar) +
                    CLASS_FILE_SUFFIX;

            int index = pathToClass.indexOf(pathSuffix);

            if (index < 0) {
                throw new ClassFileFormatException("Invalid internal class name");
            }

            directoryPath = pathToClass.substring(0, index);
        } catch (IOException e) {
            directoryPath = null;
            e.printStackTrace();
        }

        return directoryPath;
    }

    public static String ExtractInternalPath(
            String directoryPath, String pathToClass) {
        if ((directoryPath == null) || (pathToClass == null) ||
                !pathToClass.startsWith(directoryPath))
            return null;

        String s = pathToClass.substring(directoryPath.length());

        return s.replace(File.separatorChar, INTERNAL_PACKAGE_SEPARATOR);
    }

    private static Constant[] DeserializeConstants(DataInputStream dis)
            throws IOException {
        int count = dis.readUnsignedShort();
        Constant[] constants = new Constant[count];

        for (int i = 1; i < count; i++) {
            byte tag = dis.readByte();

            switch (tag) {
            case Constant.CONSTANT_Class:
                constants[i] = new ConstantClass(dis.readUnsignedShort());
                break;
            case Constant.CONSTANT_Utf8:
                constants[i] = new ConstantUtf8(dis.readUTF());
                break;
            case Constant.CONSTANT_Long:
            case Constant.CONSTANT_Double:
                dis.read();
                dis.read();
                dis.read();
                dis.read();
                i++;
            case Constant.CONSTANT_FieldRef:
            case Constant.CONSTANT_MethodRef:
            case Constant.CONSTANT_InterfaceMethodRef:
            case Constant.CONSTANT_InvokeDynamic:
            case Constant.CONSTANT_NameAndType:
            case Constant.CONSTANT_Integer:
            case Constant.CONSTANT_Float:
                dis.read();
            case Constant.CONSTANT_MethodHandle:
                dis.read();
            case Constant.CONSTANT_String:
            case Constant.CONSTANT_MethodType:
                dis.read();
                dis.read();
                break;
            default:
                //throw new ClassFormatException("Invalid constant pool entry");
                return constants;
            }
        }

        return constants;
    }
}
