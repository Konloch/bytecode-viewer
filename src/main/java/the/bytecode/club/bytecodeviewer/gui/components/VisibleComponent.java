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

import com.github.weisj.darklaf.iconset.AllIcons;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.theme.LAFTheme;
import the.bytecode.club.bytecodeviewer.resources.IconResources;

import javax.swing.*;

/**
 * Used to represent all the panes inside of Bytecode Viewer.
 *
 * @author Konloch
 * @author WaterWolf
 * @since 09/26/2011
 */

public abstract class VisibleComponent extends JInternalFrame
{
    public VisibleComponent(String title)
    {
        super(title, false, false, false, false);
        this.setDefaultIcon();
    }

    @Override
    public void updateUI()
    {
        if (Configuration.lafTheme != LAFTheme.SYSTEM)
            setBorder(BorderFactory.createEmptyBorder());
        else
            setBorder(null);
        super.updateUI();
    }

    public void setDefaultIcon()
    {
        try
        {
            if (Configuration.showDarkLAFComponentIcons)
                setFrameIcon(AllIcons.Window.Frame.get(16, 16));
            else
                setFrameIcon(IconResources.jarIcon);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static final long serialVersionUID = -6453413772343643526L;
}
