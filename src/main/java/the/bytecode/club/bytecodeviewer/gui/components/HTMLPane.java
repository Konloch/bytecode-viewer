package the.bytecode.club.bytecodeviewer.gui.components;

import the.bytecode.club.bytecodeviewer.bootloader.InitialBootScreen;
import the.bytecode.club.bytecodeviewer.Configuration;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;

import static the.bytecode.club.bytecodeviewer.Constants.*;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

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
		text = text.replace("{python}", Configuration.python2+" " + (Configuration.python2Extra ? "-2" : ""));
		text = text.replace("{python3}", Configuration.python3 + " " + (Configuration.python3Extra ? "-3" : ""));
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