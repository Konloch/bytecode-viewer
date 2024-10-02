/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
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

package the.bytecode.club.bytecodeviewer.gui.components;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.GlobalHotKeys;
import the.bytecode.club.bytecodeviewer.gui.components.listeners.PressKeyListener;
import the.bytecode.club.bytecodeviewer.gui.theme.LAFTheme;
import the.bytecode.club.bytecodeviewer.util.JTextAreaUtils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;

/**
 * Searching on an RSyntaxTextArea using swing highlighting
 *
 * @author Konloch
 * @since 6/25/2021
 */
public class SearchableRSyntaxTextArea extends RSyntaxTextArea
{

    private RTextScrollPane scrollPane = new RTextScrollPane(this);
    private final TextAreaSearchPanel textAreaSearchPanel;
    private final Color darkScrollBackground = new Color(0x3c3f41);
    private final Color darkScrollForeground = new Color(0x575859);
    private final Color blackScrollBackground = new Color(0x232323);
    private final Color blackScrollForeground = new Color(0x575859);
    private Runnable onCtrlS;

    public SearchableRSyntaxTextArea()
    {
        if (Configuration.lafTheme == LAFTheme.HIGH_CONTRAST_DARK)
        {
            //this fixes the white border on the jScrollBar panes
            scrollPane.getHorizontalScrollBar().setBackground(blackScrollBackground);
            scrollPane.getHorizontalScrollBar().setForeground(blackScrollForeground);
            scrollPane.getVerticalScrollBar().setBackground(blackScrollBackground);
            scrollPane.getVerticalScrollBar().setForeground(blackScrollForeground);
        }
        else if (Configuration.lafTheme.isDark())
        {
            //this fixes the white border on the jScrollBar panes
            scrollPane.getHorizontalScrollBar().setBackground(darkScrollBackground);
            scrollPane.getHorizontalScrollBar().setForeground(darkScrollForeground);
            scrollPane.getVerticalScrollBar().setBackground(darkScrollBackground);
            scrollPane.getVerticalScrollBar().setForeground(darkScrollForeground);
        }

        this.textAreaSearchPanel = new TextAreaSearchPanel(this);

        setAntiAliasingEnabled(true);

        addKeyListener(new PressKeyListener(keyEvent ->
        {
            if ((keyEvent.getKeyCode() == KeyEvent.VK_F)
                && ((keyEvent.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0))
                this.textAreaSearchPanel.getSearchInput().requestFocusInWindow();

            if (onCtrlS != null && (keyEvent.getKeyCode() == KeyEvent.VK_S)
                && ((keyEvent.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0))
            {
                onCtrlS.run();
                return;
            }

            GlobalHotKeys.keyPressed(keyEvent);
        }));

        setCursor(new Cursor(Cursor.TEXT_CURSOR));
        getCaret().setBlinkRate(0);
        getCaret().setVisible(true);
        addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                getCaret().setVisible(true);
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                getCaret().setVisible(true);
            }
        });

        final Font newFont = getFont().deriveFont((float) BytecodeViewer.viewer.getFontSize());

        //set number-bar font
        setFont(newFont);

        SwingUtilities.invokeLater(() ->
        {
            //attach CTRL + Mouse Wheel Zoom
            attachCtrlMouseWheelZoom();

            //set text font
            setFont(newFont);
        });

    }

    public void search(String search, boolean forwardSearchDirection, boolean caseSensitiveSearch)
    {
        JTextAreaUtils.search(this, search, forwardSearchDirection, caseSensitiveSearch);
    }

    public void highlight(String pattern, boolean caseSensitiveSearch)
    {
        JTextAreaUtils.highlight(this, pattern, caseSensitiveSearch);
    }

    public void attachCtrlMouseWheelZoom()
    {
        scrollPane.addMouseWheelListener(e ->
        {
            if (getText().isEmpty())
                return;
            if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0)
            {
                Font font = getFont();
                int size = font.getSize();
                if (e.getWheelRotation() > 0)
                    setFont(new Font(font.getName(), font.getStyle(), --size >= 2 ? --size : 2));
                else
                    setFont(new Font(font.getName(), font.getStyle(), ++size));

                e.consume();
            }
        });

        scrollPane = new RTextScrollPane()
        {
            @Override
            protected void processMouseWheelEvent(MouseWheelEvent event)
            {
                if (!isWheelScrollingEnabled())
                {
                    if (getParent() != null)
                    {
                        getParent().dispatchEvent(SwingUtilities.convertMouseEvent(this, event, getParent()));
                        return;
                    }
                }

                super.processMouseWheelEvent(event);
            }
        };

        scrollPane.setWheelScrollingEnabled(false);
    }

    public String getLineText(int line)
    {
        try
        {
            if (line < getLineCount())
            {
                int start = getLineStartOffset(line);
                int end = getLineEndOffset(line);
                return getText(start, end - start).trim();
            }
        }
        catch (BadLocationException ignored)
        {
        }
        return "";
    }

    public void setOnCtrlS(Runnable onCtrlS)
    {
        this.onCtrlS = onCtrlS;
    }

    public RTextScrollPane getScrollPane()
    {
        return scrollPane;
    }

    public TextAreaSearchPanel getTextAreaSearchPanel()
    {
        return textAreaSearchPanel;
    }

    public Runnable getOnCtrlS()
    {
        return onCtrlS;
    }
}
