package the.bytecode.club.bytecodeviewer.gui.components;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

import static javax.swing.JOptionPane.*;

/**
 * All this does is fix the bug with parentComponents being minimized.
 * The bug is the JOptionPane location ends up 0,0 instead of centered.
 * The fix is to center the frame manually before showing.
 *
 * @author Konloch
 * @author James Gosling
 * @author Scott Violet
 * @since 7/4/2021
 */

public class BetterJOptionPane
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
		JDialog dialog = createNewJDialogue(parentComponent, pane, title, style, (d)->
		{
			pane.selectInitialValue();
		});
		
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
		JDialog dialog = createNewJDialogue(parentComponent, pane, title, style, (d)->
		{
			pane.selectInitialValue();
		});
		
		pane.selectInitialValue();
		
		Object value = pane.getInputValue();
		
		if (value == UNINITIALIZED_VALUE)
			return null;
		
		return value;
	}
	
	public static void showJPanelDialogue(Component parentComponent, JScrollPane panel, int minimumHeight)
			throws HeadlessException
	{
		//create a new option pane with a empty text and just 'ok'
		JOptionPane pane = new JOptionPane("");
		pane.add(panel, 0);
		
		JDialog dialog = createNewJDialogue(parentComponent, pane, panel.getName(), 0, (d)->
		{
			int newHeight = Math.min(minimumHeight, d.getHeight());
			d.setMinimumSize(new Dimension(d.getWidth(), newHeight));
			d.setSize(new Dimension(d.getWidth(), newHeight));
		});
	}
	
	private static JDialog createNewJDialogue(Component parentComponent, JOptionPane pane, String title, int style, OnCreate onCreate)
	{
		JDialog dialog = null;
		
		//reflection to cheat our way around the
		// private createDialog(Component parentComponent, String title, int style)
		try
		{
			Method createDialog = pane.getClass().getDeclaredMethod("createDialog", Component.class, String.class, int.class);
			createDialog.setAccessible(true);
			dialog = (JDialog) createDialog.invoke(pane, parentComponent, title, style);
			
			onCreate.onCreate(dialog);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//check if the dialogue is in a poor location, attempt to correct
		if(dialog.getLocation().getY() == 0 || dialog.getLocation().getY() == 1)
			dialog.setLocationRelativeTo(null);
		
		dialog.show();
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