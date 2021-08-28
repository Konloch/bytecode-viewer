package the.bytecode.club.bytecodeviewer.gui.resourcelist;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import the.bytecode.club.bytecodeviewer.gui.util.StringMetricsUtil;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

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
 * @author Konloch
 * @since 6/22/2021
 */
public class ResourceTree extends JTree
{
	private static final long serialVersionUID = -2355167326094772096L;
    DefaultMutableTreeNode treeRoot;
	
	public ResourceTree(final DefaultMutableTreeNode treeRoot)
	{
        super(treeRoot);
        this.treeRoot = treeRoot;
    }
	
	StringMetricsUtil m = null;
	
	@Override
    public void paint(final Graphics graphics)
	{
        try
        {
            Graphics2D g = (Graphics2D) graphics;
            super.paint(g);
            if (m == null)
            {
                m = new StringMetricsUtil(g);
            }
            if (treeRoot.getChildCount() < 1)
            {
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setColor(new Color(0, 0, 0, 100));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.white);
                String s = TranslatedStrings.DRAG_CLASS_JAR.toString();
                g.drawString(s,
                        ((int) ((getWidth() / 2) - (m.getWidth(s) / 2))),
                        getHeight() / 2);
            }
        }
        catch (InternalError | NullPointerException | ClassCastException ignored)
        {
        }
    }
}
