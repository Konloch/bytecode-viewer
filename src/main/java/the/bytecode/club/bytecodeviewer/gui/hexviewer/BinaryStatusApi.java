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

import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.EditMode;
import org.exbin.bined.EditOperation;
import org.exbin.bined.SelectionRange;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Binary editor status interface.
 *
 * @author hajdam
 */
@ParametersAreNonnullByDefault
public interface BinaryStatusApi
{

    /**
     * Reports cursor position.
     *
     * @param cursorPosition cursor position
     */
    void setCursorPosition(CodeAreaCaretPosition cursorPosition);

    /**
     * Sets current selection.
     *
     * @param selectionRange current selection
     */
    void setSelectionRange(SelectionRange selectionRange);

    /**
     * Reports currently active edit mode.
     *
     * @param mode      edit mode
     * @param operation edit operation
     */
    void setEditMode(EditMode mode, EditOperation operation);

    /**
     * Sets current document size.
     *
     * @param documentSize        document size
     * @param initialDocumentSize document size when file was opened
     */
    void setCurrentDocumentSize(long documentSize, long initialDocumentSize);
}
