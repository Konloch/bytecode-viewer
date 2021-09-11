package the.bytecode.club.bytecodeviewer.gui.hexviewer;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.EditMode;
import org.exbin.bined.EditOperation;
import org.exbin.bined.SelectionRange;

/**
 * Binary editor status interface.
 *
 * @author hajdam
 */
@ParametersAreNonnullByDefault
public interface BinaryStatusApi {

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
     * @param mode edit mode
     * @param operation edit operation
     */
    void setEditMode(EditMode mode, EditOperation operation);

    /**
     * Sets current document size.
     *
     * @param documentSize document size
     * @param initialDocumentSize document size when file was opened
     */
    void setCurrentDocumentSize(long documentSize, long initialDocumentSize);
}
