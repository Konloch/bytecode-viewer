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

package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import com.github.weisj.darklaf.components.CloseButton;
import com.github.weisj.darklaf.ui.tabbedpane.*;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.components.listeners.MouseClickedListener;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class TabComponent extends JPanel
{

    private JTabbedPane pane;

    public TabComponent(JTabbedPane pane)
    {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));

        if (pane == null)
            throw new NullPointerException("TabbedPane is null");

        this.pane = pane;

        setOpaque(false);
        JLabel label = new JLabel()
        {
            public String getText()
            {
                int i = pane.indexOfTabComponent(TabComponent.this);
                if (i != -1)
                    return pane.getTitleAt(i);

                return null;
            }
        };

        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        label.setOpaque(false);
        add(label);
        JButton button = new CloseButton();
        add(button);

        JPopupMenu rightClickMenu = new JPopupMenu();
        JMenuItem closeAllTabs = new JMenuItem(String.valueOf(TranslatedStrings.CLOSE_ALL_BUT_THIS));
        JMenuItem closeTab = new JMenuItem(String.valueOf(TranslatedStrings.CLOSE_TAB));

        rightClickMenu.add(closeAllTabs);
        rightClickMenu.add(closeTab);
        button.setComponentPopupMenu(rightClickMenu);

        addMouseListener(new TabMouseListener());
        addMouseMotionListener(new TabMouseListener());

        button.addMouseListener(new MouseClickedListener(e ->
        {
            if (e.getButton() == MouseEvent.BUTTON2     // middle-click
                || e.getButton() == MouseEvent.BUTTON1) // left-click
                closePane();
        }));

        closeTab.addActionListener(e ->
        {
            if (pane.indexOfTabComponent(TabComponent.this) != -1)
            {
                int i = pane.indexOfTabComponent(TabComponent.this);
                removeTab(i);
            }
        });

        closeAllTabs.addActionListener(e ->
        {
            while (true)
            {
                if (pane.getTabCount() <= 1)
                    return;

                if (pane.indexOfTabComponent(TabComponent.this) != 0)
                    removeTab(0);
                else
                    removeTab(1);
            }
        });

        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }

    public void closePane()
    {
        if (pane.indexOfTabComponent(TabComponent.this) != -1)
        {
            int i = pane.indexOfTabComponent(TabComponent.this);
            removeTab(i);
        }
    }

    private void removeTab(int index)
    {
        ResourceViewer resourceViewer = (ResourceViewer) BytecodeViewer.viewer.workPane.tabs.getComponentAt(index);
        BytecodeViewer.viewer.workPane.openedTabs.remove(resourceViewer.resource.workingName);
        pane.remove(index);
    }

    /**
     * Get the tab panel for mouse positions.
     *
     * @return the panel.
     */
    private ScrollableTabPanel getTabPanel()
    {
        DarkScrollableTabViewport viewport = viewport();

        if(viewport != null)
        {
            for (Component component : viewport.getComponents())
                if (component instanceof ScrollableTabPanel)
                    return (ScrollableTabPanel) component;
        }

        return null;
    }

    /**
     * Get the viewport from darklaf.
     *
     * @return the viewport.
     */
    private DarkScrollableTabViewport viewport()
    {
        for (Component component : pane.getComponents())
            if (component instanceof DarkScrollableTabViewport)
                return (DarkScrollableTabViewport) component;

        return null;
    }

    /**
     * Get the tabbed pane handler from darklaf.
     *
     * @return the handler.
     */
    private DarkScrollTabbedPaneHandler getHandler()
    {
        for (Component component : pane.getComponents())
        {
            if (component instanceof DarkScrollableTabViewport)
            {
                DarkScrollableTabViewport viewport = (DarkScrollableTabViewport) component;
                for (MouseListener mouseListener : viewport.getMouseListeners())
                    if (mouseListener instanceof DarkScrollTabbedPaneHandler)
                        return (DarkScrollTabbedPaneHandler) mouseListener;
            }
        }

        return null;
    }

    /**
     * Create our own listener that redirects events back to darklaf.
     */
    private class TabMouseListener extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            e = convert(e);
            if (e == null)
                return;

            Objects.requireNonNull(getHandler()).mouseClicked(e);
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            if (e.getButton() == MouseEvent.BUTTON2)
            {
                closePane();
                return;
            }

            e = convert(e);
            if (e == null)
                return;

            Objects.requireNonNull(getHandler()).mousePressed(e);
        }

        @Override
        public void mouseEntered(MouseEvent e)
        {
            e = convert(e);
            if (e == null)
                return;

            Objects.requireNonNull(getHandler()).mouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            e = convert(e);
            if (e == null)
                return;

            Objects.requireNonNull(getHandler()).mouseExited(e);
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            e = convert(e);
            if (e == null)
                return;

            Objects.requireNonNull(getHandler()).mouseReleased(e);
        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
            e = convert(e);
            if (e == null)
                return;

            Objects.requireNonNull(getHandler()).mouseDragged(e);
        }

        @Override
        public void mouseMoved(MouseEvent e)
        {
            e = convert(e);
            if (e == null)
                return;

            Objects.requireNonNull(getHandler()).mouseMoved(e);
        }

        private MouseEvent convert(MouseEvent e)
        {
            ScrollableTabPanel tabPanel = getTabPanel();
            if (tabPanel == null || tabPanel.getMousePosition() == null)
                return null;

            int x = tabPanel.getMousePosition().x;
            int y = tabPanel.getMousePosition().y;
            return new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiersEx(), x, y,
                e.getClickCount(), e.isPopupTrigger());
        }
    }

}
