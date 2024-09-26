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

package the.bytecode.club.bytecodeviewer.gui.util;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * @author http://stackoverflow.com/a/18450804
 */
public class StringMetricsUtil
{
    Font font;
    FontRenderContext context;

    public StringMetricsUtil(Graphics2D g2)
    {
        font = g2.getFont();
        context = g2.getFontRenderContext();
    }

    public Rectangle2D getBounds(String message)
    {
        return font.getStringBounds(message, context);
    }

    public double getWidth(String message)
    {
        Rectangle2D bounds = getBounds(message);
        return bounds.getWidth();
    }

    public double getHeight(String message)
    {
        Rectangle2D bounds = getBounds(message);
        return bounds.getHeight();
    }

}
