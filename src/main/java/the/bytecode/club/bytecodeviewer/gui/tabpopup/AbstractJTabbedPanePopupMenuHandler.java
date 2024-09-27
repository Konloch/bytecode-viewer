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

/**
 * Show PopupMenu on Tabs
 *
 * @author su
 */
public abstract class AbstractJTabbedPanePopupMenuHandler extends JTabbedPanePopupEventHandler implements ITabPopupEventListener
{

    public AbstractJTabbedPanePopupMenuHandler(JTabbedPane tabbedPane)
    {
        super(tabbedPane);

        registerPopupEventListener(this);
    }

    @Override
    public void onTabPopupEvent(JTabbedPane tabbedPane, int index, TabPopupEvent e)
    {
        JPopupMenu popupMenu = toBuildTabPopupMenu(tabbedPane, e.getPopupOnTab());

        popupTabMenuWithEvent(popupMenu, e);
    }

    public abstract JPopupMenu toBuildTabPopupMenu(JTabbedPane tabbedPane, Component popupOnTab);


    public static void popupTabMenuWithEvent(JPopupMenu popupMenu, TabPopupEvent e)
    {
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

}
