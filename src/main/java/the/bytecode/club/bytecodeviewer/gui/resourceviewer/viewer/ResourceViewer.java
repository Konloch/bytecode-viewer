package the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer;

import javax.swing.JButton;
import javax.swing.JPanel;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.TabbedPane;
import the.bytecode.club.bytecodeviewer.resources.Resource;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
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

/**
 * Represents an opened tab
 *
 * @author Konloch
 */

public abstract class ResourceViewer extends JPanel
{
    public final Resource resource;
    public TabbedPane tabbedPane;
    
    protected ResourceViewer(Resource resource) {this.resource = resource;}
    
    /**
     * Returns the tab name
     */
    public String getTabName()
    {
        String tabName = resource.name;
        
        if (Configuration.simplifiedTabNames)
            tabName = MiscUtils.getChildFromPath(tabName);
        if (Configuration.displayParentInTab)
            tabName = resource.container.name + ">" + tabName;
        
        return tabName;
    }
    
    /**
     * Returns the resource bytes from the resource container
     */
    public byte[] getResourceBytes()
    {
        return resource.getResourceBytes();
    }
    
    
    public abstract void refresh(final JButton button);
    
    /**
     * Updates the tab's title
     */
    public void refreshTitle()
    {
        if(tabbedPane != null)
            tabbedPane.label.setText(getTabName());
    }

    private static final long serialVersionUID = -2965538493489119191L;
}
