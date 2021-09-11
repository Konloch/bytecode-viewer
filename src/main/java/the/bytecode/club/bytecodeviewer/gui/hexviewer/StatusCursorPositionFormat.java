package the.bytecode.club.bytecodeviewer.gui.hexviewer;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.PositionCodeType;

/**
 * Cursor position format for status.
 *
 * @author hajdam
 */
@ParametersAreNonnullByDefault
public class StatusCursorPositionFormat {

    private PositionCodeType positionCodeType = PositionCodeType.DECIMAL;
    private boolean showOffset = true;

    public StatusCursorPositionFormat() {
    }

    public StatusCursorPositionFormat(PositionCodeType positionCodeType, boolean showOffset) {
        this.positionCodeType = positionCodeType;
        this.showOffset = showOffset;
    }

    @Nonnull
    public PositionCodeType getCodeType() {
        return positionCodeType;
    }

    public void setCodeType(PositionCodeType positionCodeType) {
        this.positionCodeType = Objects.requireNonNull(positionCodeType);
    }

    public boolean isShowOffset() {
        return showOffset;
    }

    public void setShowOffset(boolean showOffset) {
        this.showOffset = showOffset;
    }
}
