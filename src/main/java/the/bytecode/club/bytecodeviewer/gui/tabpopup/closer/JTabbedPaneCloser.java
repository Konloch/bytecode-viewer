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

import the.bytecode.club.bytecodeviewer.gui.tabpopup.ITabZeroComponentEventListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Provide function of closing tabs
 *
 * @author su
 */
public class JTabbedPaneCloser
{
    private JTabbedPane tabbedPane;
    private ITabZeroComponentEventListener tabZeroComponentEventListener;

    public JTabbedPaneCloser(JTabbedPane tabbedPane)
    {
        super();
        this.tabbedPane = tabbedPane;
    }

    public JTabbedPaneCloser(JTabbedPane tabbedPane, ITabZeroComponentEventListener tabZeroComponentEventListener)
    {
        this(tabbedPane);
        this.tabZeroComponentEventListener = tabZeroComponentEventListener;
    }

    public void removeComponent(Component component)
    {
        this.tabbedPane.remove(component);
        tryTriggerTabZeroComponentEvent();
    }

    public void removeOtherComponents(Component component)
    {
        removeOtherComponents(component, false);
    }

    protected void removeOtherComponents(Component component, boolean equalStop)
    {
        int i = this.tabbedPane.getTabCount();

        while (i-- > 0)
        {
            Component c = this.tabbedPane.getComponentAt(i);

            if (c != component)
                this.tabbedPane.remove(i);
            else if (equalStop)
                break;
        }

        tryTriggerTabZeroComponentEvent();
    }

    public void removeLeftComponents(Component component)
    {
        int count = this.tabbedPane.getTabCount();
        int i = 0;
        List<Component> removeTabs = new ArrayList<>();

        do
        {
            Component c = this.tabbedPane.getComponentAt(i);

            if (c != component)
                removeTabs.add(c);
            else
                break;
        } while (i++ < count);

        for (Component c : removeTabs)
        {
            this.tabbedPane.remove(c);
        }

        tryTriggerTabZeroComponentEvent();
    }

    public void removeRightComponents(Component component)
    {
        removeOtherComponents(component, true);
    }

    public void removeAllComponents()
    {
        this.tabbedPane.removeAll();
        tryTriggerTabZeroComponentEvent();
    }

    private void tryTriggerTabZeroComponentEvent()
    {
        if (this.tabbedPane.getTabCount() == 0 && tabZeroComponentEventListener != null)
            tabZeroComponentEventListener.onTabZeroComponent(this.tabbedPane);
    }
}
