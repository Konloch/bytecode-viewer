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

import com.github.weisj.darklaf.components.RotatableIconAnimator;
import com.github.weisj.darklaf.properties.icons.RotatableIcon;
import the.bytecode.club.bytecodeviewer.resources.IconResources;

import java.awt.event.HierarchyEvent;

/**
 * @author Konloch
 * @since 7/4/2021
 */
public class WaitBusyIcon extends JMenuItemIcon
{
    private final RotatableIconAnimator animator;

    public WaitBusyIcon()
    {
        super(new RotatableIcon(IconResources.busyIcon));

        animator = new RotatableIconAnimator(8, (RotatableIcon) getIcon(), this);

        addHierarchyListener(e ->
        {
            if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0)
            {
                if (getParent() == null)
                    animator.stop();
                else
                    animator.start();
            }
        });
    }
}
