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

package the.bytecode.club.bytecodeviewer.gui.tabpopup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Register PopupEvent Handler on TabbedPane
 *
 * @author su
 */
public class JTabbedPanePopupEventHandler
{
    protected final JTabbedPane tabbedPane;
    private ITabPopupEventListener tabPopupEventListener;

    public JTabbedPanePopupEventHandler(JTabbedPane tabbedPane)
    {
        super();
        this.tabbedPane = tabbedPane;
        this.registerMouseEventListener();
    }

    private void registerMouseEventListener()
    {
        this.tabbedPane.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                tryTriggerTabPopupEvent(e);
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                tryTriggerTabPopupEvent(e);
            }
        });
    }

    public void registerPopupEventListener(ITabPopupEventListener tabPopupEventListener)
    {
        this.tabPopupEventListener = tabPopupEventListener;
    }

    protected void tryTriggerTabPopupEvent(MouseEvent e)
    {
        if (e.isPopupTrigger())
        {
            int index = tabbedPane.indexAtLocation(e.getX(), e.getY());

            if (index != -1)
            {
                Component popupOnTab = tabbedPane.getComponentAt(index);

                if (this.tabPopupEventListener != null)
                    this.tabPopupEventListener.onTabPopupEvent(tabbedPane, index, new TabPopupEvent(e, popupOnTab));
            }
        }
    }

    public JTabbedPane getTabbedPane()
    {
        return tabbedPane;
    }

}
