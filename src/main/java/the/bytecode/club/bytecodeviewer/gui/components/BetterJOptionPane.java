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
		
		//reflection to cheat our way around the
		// private createDialog(Component parentComponent, String title, int style)
		JDialog dialog = null;
		try
		{
			Method createDialog = pane.getClass().getDeclaredMethod("createDialog", Component.class, String.class, int.class);
			createDialog.setAccessible(true);
			dialog = (JDialog) createDialog.invoke(pane, parentComponent, title, style);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		pane.selectInitialValue();
		
		//check if the dialogue is in a poor location, attempt to correct
		if(dialog.getLocation().getY() == 0)
			dialog.setLocationRelativeTo(null);
		
		dialog.show();
		dialog.dispose();
		
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
}