package the.bytecode.club.bytecodeviewer.gui.util;

import the.bytecode.club.bytecodeviewer.gui.resourceviewer.TabbedPane;

import java.awt.Color;
import javax.swing.SwingUtilities;

/**
 * @author Konloch
 */
public class DelayTabbedPaneThread extends Thread
{
    public boolean stopped = false;
    private final TabbedPane pane;

    public DelayTabbedPaneThread(TabbedPane pane) {
        this.pane = pane;
    }

    @Override
    public void run() {
        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!stopped) {
            SwingUtilities.invokeLater(() -> {
                if (stopped)
                    return;

                pane.label.setOpaque(true);
                pane.label.setBackground(Color.MAGENTA);
                pane.label.updateUI();
            });
        }
    }
}
