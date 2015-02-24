package the.bytecode.club.bytecodeviewer;

import java.io.File;
import java.util.Random;

/**
 * A collection of Misc Utils.
 * 
 * @author Konloch
 *
 */

public class MiscUtils {
	private static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final String AN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static Random rnd = new Random();

	/**
	 * Returns a random string without numbers
	 * @param len the length of the String
	 * @return the randomized string
	 */
	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	/**
	 * Returns a random string with numbers
	 * @param len the length of the String
	 * @return the randomized string
	 */
	public static String randomStringNum(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AN.charAt(rnd.nextInt(AN.length())));
		return sb.toString();
	}
	
	/**
	 * Checks the file system to ensure it's a unique name
	 * @param start directory it'll be in
	 * @param ext the file extension it'll use
	 * @return the unique name
	 */
	public static String getUniqueName(String start, String ext) {
		String s = null;
		boolean b = true;
		File f = null;
		String m = null;
		while (b) {
			m = MiscUtils.randomString(32);
			f = new File(start + m + ext);
			if (!f.exists()) {
				s = start + m;
				b = false;
			}
		}
		return s;
	}
	
	/**
	 * Checks the file system to ensure it's a unique number
	 * @param start directory it'll be in
	 * @param ext the file extension it'll use
	 * @return the unique number
	 */
	public static int getClassNumber(String start, String ext) {
		boolean b = true;
		int i = 0;
		while (b) {
			File tempF = new File(start + i + ext);
			if (!tempF.exists())
				b = false;
			else
				i++;
		}
		return i;
	}
}
