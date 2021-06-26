package the.bytecode.club.bytecodeviewer.gui.components;

import javax.swing.JInternalFrame;

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
 * Used to represent all the panes inside of Bytecode Viewer.
 *
 * @author Konloch
 * @author WaterWolf
 */

public abstract class VisibleComponent extends JInternalFrame
{
    private static final long serialVersionUID = -6453413772343643526L;

    public VisibleComponent(final String title)
    {
        super(title, false, false, false, false);
        this.setFrameIcon(null);
    }
}