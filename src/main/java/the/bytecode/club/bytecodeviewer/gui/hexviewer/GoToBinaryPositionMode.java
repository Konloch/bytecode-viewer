package the.bytecode.club.bytecodeviewer.gui.hexviewer;

/**
 * Mode for calculation of the go-to position in binary document.
 */
public enum GoToBinaryPositionMode {
    /**
     * Count from start of the document.
     */
    FROM_START,
    /**
     * Count from end of the document.
     */
    FROM_END,
    /**
     * Count from current position of the cursor in the document.
     */
    FROM_CURSOR
}
