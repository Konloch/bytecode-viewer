package the.bytecode.club.bytecodeviewer;

import java.io.File;
import java.util.Random;

public class MiscUtils {
	private static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final String AN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static Random rnd = new Random();

	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	public static String randomStringNum(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AN.charAt(rnd.nextInt(AN.length())));
		return sb.toString();
	}
	


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
