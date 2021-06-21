package the.bytecode.club.bytecodeviewer.util;

import the.bytecode.club.bytecodeviewer.gui.MainViewerGUI;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Konloch
 * @since 6/21/2021
 */
public class WindowStateChangeAdapter extends WindowAdapter
{
	private final MainViewerGUI mainViewerGUI;
	
	public WindowStateChangeAdapter(MainViewerGUI mainViewerGUI) {this.mainViewerGUI = mainViewerGUI;}
	
	@Override
	public void windowStateChanged(WindowEvent evt)
	{
		int oldState = evt.getOldState();
		int newState = evt.getNewState();

        /*if ((oldState & Frame.ICONIFIED) == 0 && (newState & Frame.ICONIFIED) != 0) {
            System.out.println("Frame was iconized");
        } else if ((oldState & Frame.ICONIFIED) != 0 && (newState & Frame.ICONIFIED) == 0) {
            System.out.println("Frame was deiconized");
        }*/
		
		if ((oldState & Frame.MAXIMIZED_BOTH) == 0 && (newState & Frame.MAXIMIZED_BOTH) != 0)
		{
			mainViewerGUI.isMaximized = true;
		}
		else if ((oldState & Frame.MAXIMIZED_BOTH) != 0 && (newState & Frame.MAXIMIZED_BOTH) == 0)
		{
			mainViewerGUI.isMaximized = false;
		}
	}
}
