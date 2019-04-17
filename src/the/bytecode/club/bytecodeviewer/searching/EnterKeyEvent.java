package the.bytecode.club.bytecodeviewer.searching;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class EnterKeyEvent implements KeyListener
{
    public static final EnterKeyEvent SINGLETON = new EnterKeyEvent();

    @Override public void keyTyped(KeyEvent e) { }

    @Override public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            BytecodeViewer.viewer.s.search();
        }
    }

    @Override public void keyReleased(KeyEvent e) { }

}
