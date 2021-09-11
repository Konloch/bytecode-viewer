package the.bytecode.club.bytecodeviewer.gui.hexviewer;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.PositionCodeType;

/**
 * Document size format for status.
 *
 * @author hajdam
 */
@ParametersAreNonnullByDefault
public class StatusDocumentSizeFormat {

    private PositionCodeType positionCodeType = PositionCodeType.DECIMAL;
    private boolean showRelative = true;

    public StatusDocumentSizeFormat() {

    }

    public StatusDocumentSizeFormat(PositionCodeType positionCodeType, boolean showRelative) {
        this.positionCodeType = positionCodeType;
        this.showRelative = showRelative;
    }

    @Nonnull
    public PositionCodeType getCodeType() {
        return positionCodeType;
    }

    public void setCodeType(PositionCodeType positionCodeType) {
        this.positionCodeType = Objects.requireNonNull(positionCodeType);
    }

    public boolean isShowRelative() {
        return showRelative;
    }

    public void setShowRelative(boolean showRelativeSize) {
        this.showRelative = showRelativeSize;
    }
}
