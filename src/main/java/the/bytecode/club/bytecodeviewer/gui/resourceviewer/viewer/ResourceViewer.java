package the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer;

import javax.swing.JPanel;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.TabbedPane;
import the.bytecode.club.bytecodeviewer.util.FileContainer;
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
 * A ResourceViewer
 *
 * @author Konloch
 */

public abstract class ResourceViewer extends JPanel
{
    public ClassNode cn;
    public String name;
    public String workingName;
    public FileContainer container;
    public TabbedPane tabbedPane;
    
    public abstract void refreshTitle();
    
    public String getTabName()
    {
        String tabName = name;
        
        if (Configuration.simplifiedTabNames)
            tabName = MiscUtils.getChildFromPath(tabName);
        if (Configuration.displayParentInTab)
            tabName = container.name + ">" + tabName;
        
        return tabName;
    }

    private static final long serialVersionUID = -2965538493489119191L;
}