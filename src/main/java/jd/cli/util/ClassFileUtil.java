package jd.cli.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import jd.core.CoreConstants;
import jd.core.model.classfile.constant.Constant;
import jd.core.model.classfile.constant.ConstantClass;
import jd.core.model.classfile.constant.ConstantConstant;
import jd.core.model.classfile.constant.ConstantUtf8;
import jd.core.process.deserializer.ClassFormatException;
import jd.core.util.StringConstants;


public class ClassFileUtil {
    /*
     * Lecture rapide de la structure de la classe et extraction du nom du
     * repoertoire de base.
     */
    public static String ExtractDirectoryPath(String pathToClass) {
        DataInputStream dis = null;
        String directoryPath = null;

        try {
            dis = new DataInputStream(
                    new BufferedInputStream(
                            new FileInputStream(pathToClass)));

            int magic = dis.readInt();
            if (magic != CoreConstants.JAVA_MAGIC_NUMBER)
                throw new ClassFormatException("Invalid Java .class file");

            /* int minor_version = */
            dis.readUnsignedShort();
            /* int major_version = */
            dis.readUnsignedShort();

            Constant[] constants = DeserializeConstants(dis);

            /* int access_flags = */
            dis.readUnsignedShort();
            int this_class = dis.readUnsignedShort();

            Constant c = constants[this_class];
            if ((c == null) || (c.tag != ConstantConstant.CONSTANT_Class))
                throw new ClassFormatException("Invalid contant pool");

            c = constants[((ConstantClass) c).name_index];
            if ((c == null) || (c.tag != ConstantConstant.CONSTANT_Utf8))
                throw new ClassFormatException("Invalid contant pool");

            String internalClassName = ((ConstantUtf8) c).bytes;
            String pathSuffix = internalClassName.replace(
                    StringConstants.INTERNAL_PACKAGE_SEPARATOR, File.separatorChar) +
                    StringConstants.CLASS_FILE_SUFFIX;

            int index = pathToClass.indexOf(pathSuffix);

            if (index < 0)
                throw new ClassFormatException("Invalid internal class name");

            directoryPath = pathToClass.substring(0, index);
        } catch (FileNotFoundException e) {
            directoryPath = null;
            e.printStackTrace();
        } catch (IOException e) {
            directoryPath = null;
            e.printStackTrace();
        } finally {
            if (dis != null)
                try {
                    dis.close();
                } catch (IOException e) {
                }
        }

        return directoryPath;
    }

    public static String ExtractInternalPath(
            String directoryPath, String pathToClass) {
        if ((directoryPath == null) || (pathToClass == null) ||
                !pathToClass.startsWith(directoryPath))
            return null;

        String s = pathToClass.substring(directoryPath.length());

        return s.replace(File.separatorChar, StringConstants.INTERNAL_PACKAGE_SEPARATOR);
    }

    private static Constant[] DeserializeConstants(DataInputStream dis)
            throws IOException {
        int count = dis.readUnsignedShort();
        if (count == 0)
            return null;

        Constant[] constants = new Constant[count];

        for (int i = 1; i < count; i++) {
            byte tag = dis.readByte();

            switch (tag) {
                case ConstantConstant.CONSTANT_Class:
                    constants[i] = new ConstantClass(tag, dis.readUnsignedShort());
                    break;
                case ConstantConstant.CONSTANT_Utf8:
                    constants[i] = new ConstantUtf8(tag, dis.readUTF());
                    break;
                case ConstantConstant.CONSTANT_Long:
                case ConstantConstant.CONSTANT_Double:
                    dis.read();
                    dis.read();
                    dis.read();
                    dis.read();
                    i++;
                case ConstantConstant.CONSTANT_Fieldref:
                case ConstantConstant.CONSTANT_Methodref:
                case ConstantConstant.CONSTANT_InterfaceMethodref:
                case ConstantConstant.CONSTANT_NameAndType:
                case ConstantConstant.CONSTANT_Integer:
                case ConstantConstant.CONSTANT_Float:
                    dis.read();
                    dis.read();
                case ConstantConstant.CONSTANT_String:
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
