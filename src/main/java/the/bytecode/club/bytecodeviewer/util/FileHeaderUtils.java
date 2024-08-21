package the.bytecode.club.bytecodeviewer.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Konloch
 * @since 8/21/2024
 */
public class FileHeaderUtils
{
	public static final int JAVA_CLASS_FILE_HEADER = 0xCAFEBABE;
	
	public static boolean doesFileHeaderMatch(byte[] bytes, int fileHeader)
	{
		int bytesHeader = ((bytes[0] & 0xFF) << 24) |
				((bytes[1] & 0xFF) << 16) |
				((bytes[2] & 0xFF) << 8)  |
				((bytes[3] & 0xFF));
		
		return bytesHeader == fileHeader;
	}
	
	public static String getFileHeaderAsString(byte[] bytes)
	{
		if(bytes == null || bytes.length < 4)
			return StringUtils.EMPTY;
		
		return String.format("%02X%02X%02X%02X",
				bytes[0], bytes[1], bytes[2],bytes[3]);
	}
}
