package the.bytecode.club.bytecodeviewer.gui;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

import javax.swing.*;
import java.awt.*;

/**
 * @author Konloch
 */
public class DelayTabbedPaneThread extends Thread
{
    public boolean stopped = false;
    private TabbedPane pane;

    public DelayTabbedPaneThread(TabbedPane pane)
    {
        this.pane = pane;
    }

    @Override
    public void run()
    {
        try
        {
            sleep(200);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if(!stopped)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    if(stopped)
                        return;

                    pane.label.setOpaque(true);
                    pane.label.setBackground(Color.MAGENTA);
                    pane.label.updateUI();
            }
            });
        }
    }
}
