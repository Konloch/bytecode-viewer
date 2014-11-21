package the.bytecode.club.bytecodeviewer.obfuscators;

import java.util.ArrayList;
import java.util.Random;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

public abstract class JavaObfuscator extends Thread {

	@Override
	public void run() {
		BytecodeViewer.viewer.setIcon(true);
		BytecodeViewer.runningObfuscation = true;
		obfuscate();
		BytecodeViewer.runningObfuscation = false;
		BytecodeViewer.viewer.setIcon(false);
	}
	
	public int getStringLength() {
		if(BytecodeViewer.viewer.obfuscatorGroup.isSelected(BytecodeViewer.viewer.strongObf.getModel())) {
			return MAX_STRING_LENGTH;
		} else { //if(BytecodeViewer.viewer.obfuscatorGroup.isSelected(BytecodeViewer.viewer.lightObf.getModel())) {
			return MIN_STRING_LENGTH;
		}
	}
	
	public static int MAX_STRING_LENGTH = 250;
	public static int MIN_STRING_LENGTH = 20;
	private ArrayList<String> names = new ArrayList<String>();
	private static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final String AN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static Random rnd = new Random();
	private static String randomString(int len) {
		   StringBuilder sb = new StringBuilder(len);
		   for( int i = 0; i < len; i++ ) 
		      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
		   return sb.toString();
	}
	private static String randomStringNum(int len) {
		   StringBuilder sb = new StringBuilder(len);
		   for( int i = 0; i < len; i++ ) 
		      sb.append( AN.charAt( rnd.nextInt(AN.length()) ) );
		   return sb.toString();
	}
	
	protected String generateUniqueName(int length) {
		boolean found = false;
		String name = "";
		while(!found) {
			String nameTry = randomString(1) + randomStringNum(length-1);
			if(!names.contains(nameTry)) {
				names.add(nameTry);
				name = nameTry;
				found = true;
			}
		}
		return name;
	}

	public abstract void obfuscate();
}
