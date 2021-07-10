package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bootloader.InitialBootScreen;
import the.bytecode.club.bytecodeviewer.Configuration;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;

import static the.bytecode.club.bytecodeviewer.Constants.*;

/**
 * @author Konloch
 * @since 7/7/2021
 */
public class HTMLPane extends JEditorPane
{
	private HTMLPane()
	{
		setEditorKit(new HTMLEditorKit());
		setEditable(false);
	}
	
	public static HTMLPane fromResource(String resourcePath) throws IOException
	{
		return fromString(convertStreamToString(InitialBootScreen.class.getClassLoader().getResourceAsStream(resourcePath)));
	}
	
	public static HTMLPane fromString(String text)
	{
		HTMLPane pane = new HTMLPane();
		
		text = text.replace("{fatJar}", String.valueOf(FAT_JAR));
		text = text.replace("{java}", Configuration.java);
		text = text.replace("{javac}", Configuration.javac);
		text = text.replace("{bcvDir}", BCVDir.getAbsolutePath());
		text = text.replace("{python}", Configuration.python);
		text = text.replace("{python3}", Configuration.python3);
		text = text.replace("{rt}", Configuration.rt);
		text = text.replace("{lib}", Configuration.library);
		text = text.replace("{krakatauVersion}", krakatauVersion);
		text = text.replace("{krakatauDir}", krakatauWorkingDirectory);
		text = text.replace("{enjarifyVersion}", enjarifyVersion);
		text = text.replace("{enjarifyDir}", enjarifyWorkingDirectory);
		
		pane.setText(text);
		pane.setCaretPosition(0);
		
		return pane;
	}
	
	public static String convertStreamToString(java.io.InputStream is) throws IOException
	{
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		String string = s.hasNext() ? s.next() : "";
		is.close();
		s.close();
		return string;
	}
}