package the.bytecode.club.bytecodeviewer.gui.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;

import static javax.swing.JOptionPane.CLOSED_OPTION;
import static javax.swing.JOptionPane.DEFAULT_OPTION;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.UNINITIALIZED_VALUE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.getRootFrame;

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
 * Extends the JOptionPane
 *
 * @author Konloch
 * @author James Gosling
 * @author Scott Violet
 * @since 7/4/2021
 */

public class ExtendedJOptionPane
{
	public static void showMessageDialog(Component parentComponent,
	                                     Object message) throws HeadlessException
	{
		showMessageDialog(parentComponent, message, UIManager.getString(
				"OptionPane.messageDialogTitle", parentComponent.getLocale()),
				INFORMATION_MESSAGE);
	}
	
	public static void showMessageDialog(Component parentComponent,
	                                     Object message, String title, int messageType)
			throws HeadlessException
	{
		showMessageDialog(parentComponent, message, title, messageType, null);
	}
	
	public static void showMessageDialog(Component parentComponent,
	                                     Object message, String title, int messageType, Icon icon)
			throws HeadlessException
	{
		showOptionDialog(parentComponent, message, title, DEFAULT_OPTION,
				messageType, icon, null, null);
	}
	
	public static String showInputDialog(Object message)
			throws HeadlessException {
		return showInputDialog(null, message);
	}
	
	public static String showInputDialog(Object message, Object initialSelectionValue) {
		return showInputDialog(null, message, initialSelectionValue);
	}
	
	public static String showInputDialog(Component parentComponent,
	                                     Object message) throws HeadlessException {
		return showInputDialog(parentComponent, message, UIManager.getString(
				"OptionPane.inputDialogTitle", parentComponent.getLocale()), QUESTION_MESSAGE);
	}
	
	public static String showInputDialog(Component parentComponent, Object message,
	                                     Object initialSelectionValue) {
		return (String)showInputDialog(parentComponent, message,
				UIManager.getString("OptionPane.inputDialogTitle",
						parentComponent.getLocale()), QUESTION_MESSAGE, null, null,
				initialSelectionValue);
	}
	
	public static String showInputDialog(Component parentComponent,
	                                     Object message, String title, int messageType)
			throws HeadlessException {
		return (String)showInputDialog(parentComponent, message, title,
				messageType, null, null, null);
	}
	
	public static int showOptionDialog(Component parentComponent,
	                                   Object message, String title, int optionType, int messageType,
	                                   Icon icon, Object[] options, Object initialValue)
			throws HeadlessException
	{
		JOptionPane pane = new JOptionPane(message, messageType,
				optionType, icon,
				options, initialValue);
		
		pane.setInitialValue(initialValue);
		pane.setComponentOrientation(((parentComponent == null) ?
				getRootFrame() : parentComponent).getComponentOrientation());
		
		int style = styleFromMessageType(messageType);
		JDialog dialog = createNewJDialog(parentComponent, pane, title, style, (d)->
                pane.selectInitialValue());
		
		pane.selectInitialValue();
		
		Object selectedValue = pane.getValue();
		
		if(selectedValue == null)
			return CLOSED_OPTION;
		
		if(options == null)
		{
			if(selectedValue instanceof Integer)
				return (Integer) selectedValue;
			return CLOSED_OPTION;
		}
		
		for(int counter = 0, maxCounter = options.length;
		    counter < maxCounter; counter++)
		{
			if(options[counter].equals(selectedValue))
				return counter;
		}
		
		return CLOSED_OPTION;
	}
	
	public static Object showInputDialog(Component parentComponent,
	                                     Object message, String title, int messageType, Icon icon,
	                                     Object[] selectionValues, Object initialSelectionValue)
			throws HeadlessException {
		JOptionPane    pane = new JOptionPane(message, messageType,
				OK_CANCEL_OPTION, icon,
				null, null);
		
		pane.setWantsInput(true);
		pane.setSelectionValues(selectionValues);
		pane.setInitialSelectionValue(initialSelectionValue);
		pane.setComponentOrientation(((parentComponent == null) ?
				getRootFrame() : parentComponent).getComponentOrientation());
		
		int style = styleFromMessageType(messageType);
		JDialog dialog = createNewJDialog(parentComponent, pane, title, style, (d)->
                pane.selectInitialValue());
		
		pane.selectInitialValue();
		
		Object value = pane.getInputValue();
		
		if (value == UNINITIALIZED_VALUE)
			return null;
		
		return value;
	}
	
	public static void showJPanelDialog(Component parentComponent, JScrollPane panel, int minimumHeight, OnCreate onCreate)
			throws HeadlessException
	{
		//create a new option pane with a empty text and just 'ok'
		JOptionPane pane = new JOptionPane("");
		pane.add(panel, 0);

		JDialog dialog = createNewJDialog(parentComponent, pane, panel.getName(), ERROR_MESSAGE, (d)->
		{
			int newHeight = Math.min(minimumHeight, d.getHeight());
			d.setMinimumSize(new Dimension(d.getWidth(), newHeight));
			d.setSize(new Dimension(d.getWidth(), newHeight));
			
			if(onCreate != null)
				onCreate.onCreate(d);
		});
	}
	
	private static JDialog createNewJDialog(Component parentComponent, JOptionPane pane, String title, int style, OnCreate onCreate)
	{
		JDialog dialog = pane.createDialog(parentComponent, title);
		if (JDialog.isDefaultLookAndFeelDecorated()) {
			boolean supportsWindowDecorations =
					UIManager.getLookAndFeel().getSupportsWindowDecorations();
			if (supportsWindowDecorations) {
				dialog.setUndecorated(true);
				pane.getRootPane().setWindowDecorationStyle(style);
			}
		}

		onCreate.onCreate(dialog);
		
		//check if the dialog is in a poor location, attempt to correct
		if (dialog.getLocation().getY() == 0 || dialog.getLocation().getY() == 1)
			dialog.setLocationRelativeTo(null); //TODO check if BytecodeViewer.viewer is better on multi monitor for this edgecase
		else
			dialog.setLocationRelativeTo(BytecodeViewer.viewer);
		
		dialog.setVisible(true);
		dialog.dispose();
		
		return dialog;
	}
	
	private static int styleFromMessageType(int messageType)
	{
		switch (messageType)
		{
			case ERROR_MESSAGE:
				return JRootPane.ERROR_DIALOG;
			case QUESTION_MESSAGE:
				return JRootPane.QUESTION_DIALOG;
			case WARNING_MESSAGE:
				return JRootPane.WARNING_DIALOG;
			case INFORMATION_MESSAGE:
				return JRootPane.INFORMATION_DIALOG;
			case PLAIN_MESSAGE:
			default:
				return JRootPane.PLAIN_DIALOG;
		}
	}
	
	interface OnCreate
	{
		void onCreate(JDialog dialog);
	}
}
