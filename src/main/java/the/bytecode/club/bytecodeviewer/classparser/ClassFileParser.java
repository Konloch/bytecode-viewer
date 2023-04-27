package the.bytecode.club.bytecodeviewer.classparser;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class ClassFileParser {

    private final byte[] classBytes;
    private final ConstantParser cpParser;

    public ClassFileParser(byte[] classBytes){
        this.classBytes = classBytes;
        cpParser = new ConstantParser(classBytes, parseConstantPoolCount());
    }

    public String parseMagicValue(){
        try(DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(classBytes))) {
            int magicValue = dataInputStream.readInt();
            return "0x" + Integer.toHexString(magicValue).toUpperCase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "none";
    }

    public ClassVersion parseVersionClass(){
        try(DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(classBytes))) {
            dataInputStream.skipBytes(4);
            int minor = dataInputStream.readUnsignedShort();
            int major = dataInputStream.readUnsignedShort();
            return ClassVersion.check(major, minor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ClassVersion.UNKNOWN;
    }

    public Date parseClassModificationDate() {
        try (DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(classBytes))) {
            dataInputStream.skipBytes(8);
            long modificationTime = dataInputStream.readInt() * 1000L;
            return new Date(modificationTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int parseConstantPoolCount(){
        try(DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(classBytes))) {
            dataInputStream.skipBytes(8);
            return dataInputStream.readUnsignedShort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getHash(String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(classBytes);
            byte[] digest = md.digest();
            return convertToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "null";
    }

    private String convertToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }

    public ConstantParser getConstantPool(){
        return cpParser;
    }

}
