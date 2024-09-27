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

package the.bytecode.club.bytecodeviewer.gui.tabpopup.closer;

import the.bytecode.club.bytecodeviewer.gui.tabpopup.AbstractJTabbedPanePopupMenuHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * PopupMenu & Items implementation of Close Tabs
 *
 * @author su
 */
public class JTabbedPanePopupMenuTabsCloser extends AbstractJTabbedPanePopupMenuHandler
{
    protected JTabbedPaneCloser tabbedPaneCloser;
    private PopupMenuTabsCloseConfiguration closeConfiguration;

    public JTabbedPanePopupMenuTabsCloser(JTabbedPane tabbedPane)
    {
        super(tabbedPane);
        this.tabbedPaneCloser = new JTabbedPaneCloser(tabbedPane);
    }

    public void configureCloseItems(PopupMenuTabsCloseConfiguration configuration)
    {
        this.closeConfiguration = configuration;
    }

    public PopupMenuTabsCloseConfiguration getCloseConfiguration()
    {
        return closeConfiguration;
    }

    @Override
    public JPopupMenu toBuildTabPopupMenu(JTabbedPane tabbedPane, Component popupOnTab)
    {
        JPopupMenu popUpMenu = new JPopupMenu();

        if (closeConfiguration.isClose())
            addItemCloseTab(popUpMenu, popupOnTab);

        if (closeConfiguration.isCloseOthers())
            addItemCloseOtherTabs(popUpMenu, popupOnTab);

        if (closeConfiguration.isCloseAll())
            addItemCloseAllTabs(popUpMenu);

        if (closeConfiguration.isCloseLefts())
            addItemCloseLeftTabs(popUpMenu, popupOnTab);

        if (closeConfiguration.isCloseRights())
            addItemCloseRightTabs(popUpMenu, popupOnTab);

        return popUpMenu;
    }

    protected void addItemCloseTab(JPopupMenu popUpMenu, Component popupOnTab)
    {
        addMenuItem(popUpMenu, "Close", e -> tabbedPaneCloser.removeComponent(popupOnTab));
    }

    protected void addItemCloseOtherTabs(JPopupMenu popUpMenu, Component popupOnTab)
    {
        addMenuItem(popUpMenu, "Close Others", e -> tabbedPaneCloser.removeOtherComponents(popupOnTab));
    }

    protected void addItemCloseAllTabs(JPopupMenu popUpMenu)
    {
        addMenuItem(popUpMenu, "Close All", e -> tabbedPaneCloser.removeAllComponents());
    }

    protected void addItemCloseLeftTabs(JPopupMenu popUpMenu, Component popupOnTab)
    {
        addMenuItem(popUpMenu, "Close Lefts", e -> tabbedPaneCloser.removeLeftComponents(popupOnTab));
    }

    protected void addItemCloseRightTabs(JPopupMenu popUpMenu, Component popupOnTab)
    {
        addMenuItem(popUpMenu, "Close Rights", e -> tabbedPaneCloser.removeRightComponents(popupOnTab));
    }

    protected void addMenuItem(JPopupMenu popUpMenu, String item, ActionListener listener)
    {
        JMenuItem menuItem = new JMenuItem(item);
        popUpMenu.add(menuItem);
        menuItem.addActionListener(listener);
    }
}
