package the.bytecode.club.bytecodeviewer.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * @author Konloch
 * @since 7/6/2021
 */
public class ClassFileUtils
{
	/**
	 * Grab the byte array from the loaded Class object by getting the resource from the classloader
	 */
	public static byte[] getClassFileBytes(Class<?> clazz) throws IOException
	{
		try (InputStream is = clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class");
		     ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			int r;
			byte[] buffer = new byte[8192];
			while ((r = Objects.requireNonNull(is).read(buffer)) >= 0)
				baos.write(buffer, 0, r);
			return baos.toByteArray();
		}
	}
}
