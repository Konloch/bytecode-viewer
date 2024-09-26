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

package the.bytecode.club.bytecodeviewer.gui.hexviewer;

import org.exbin.bined.PositionCodeType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

/**
 * Cursor position format for status.
 *
 * @author hajdam
 */
@ParametersAreNonnullByDefault
public class StatusCursorPositionFormat
{

    private PositionCodeType positionCodeType = PositionCodeType.DECIMAL;
    private boolean showOffset = true;

    public StatusCursorPositionFormat()
    {
    }

    public StatusCursorPositionFormat(PositionCodeType positionCodeType, boolean showOffset)
    {
        this.positionCodeType = positionCodeType;
        this.showOffset = showOffset;
    }

    @Nonnull
    public PositionCodeType getCodeType()
    {
        return positionCodeType;
    }

    public void setCodeType(PositionCodeType positionCodeType)
    {
        this.positionCodeType = Objects.requireNonNull(positionCodeType);
    }

    public boolean isShowOffset()
    {
        return showOffset;
    }

    public void setShowOffset(boolean showOffset)
    {
        this.showOffset = showOffset;
    }
}
