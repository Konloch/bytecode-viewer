package the.bytecode.club.bytecodeviewer.gui.hexviewer;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JToolTip;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.EditMode;
import org.exbin.bined.EditOperation;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.SelectionRange;

/**
 * Binary editor status panel.
 *
 * @author hajdam
 */
@ParametersAreNonnullByDefault
public class BinaryStatusPanel extends javax.swing.JPanel implements BinaryStatusApi {

    public static int DEFAULT_OCTAL_SPACE_GROUP_SIZE = 4;
    public static int DEFAULT_DECIMAL_SPACE_GROUP_SIZE = 3;
    public static int DEFAULT_HEXADECIMAL_SPACE_GROUP_SIZE = 4;

    public static final String INSERT_EDIT_MODE_LABEL = "INS";
    public static final String OVERWRITE_EDIT_MODE_LABEL = "OVR";
    public static final String READONLY_EDIT_MODE_LABEL = "RO";
    public static final String INPLACE_EDIT_MODE_LABEL = "INP";

    public static final String OCTAL_CODE_TYPE_LABEL = "OCT";
    public static final String DECIMAL_CODE_TYPE_LABEL = "DEC";
    public static final String HEXADECIMAL_CODE_TYPE_LABEL = "HEX";

    private final StatusCursorPositionFormat cursorPositionFormat = new StatusCursorPositionFormat();
    private final StatusDocumentSizeFormat documentSizeFormat = new StatusDocumentSizeFormat();
    private final int octalSpaceGroupSize = DEFAULT_OCTAL_SPACE_GROUP_SIZE;
    private final int decimalSpaceGroupSize = DEFAULT_DECIMAL_SPACE_GROUP_SIZE;
    private final int hexadecimalSpaceGroupSize = DEFAULT_HEXADECIMAL_SPACE_GROUP_SIZE;

    private EditOperation editOperation;
    private CodeAreaCaretPosition caretPosition;
    private SelectionRange selectionRange;
    private long documentSize;
    private long initialDocumentSize;

    private javax.swing.JMenu cursorPositionCodeTypeMenu;
    private javax.swing.JLabel cursorPositionLabel;
    private javax.swing.ButtonGroup cursorPositionModeButtonGroup;
    private javax.swing.JCheckBoxMenuItem cursorPositionShowOffsetCheckBoxMenuItem;
    private javax.swing.JRadioButtonMenuItem decimalCursorPositionModeRadioButtonMenuItem;
    private javax.swing.JRadioButtonMenuItem decimalDocumentSizeModeRadioButtonMenuItem;
    private javax.swing.JMenu documentSizeCodeTypeMenu;
    private javax.swing.JMenuItem documentSizeCopyMenuItem;
    private javax.swing.JLabel documentSizeLabel;
    private javax.swing.ButtonGroup documentSizeModeButtonGroup;
    private javax.swing.JPopupMenu documentSizePopupMenu;
    private javax.swing.JCheckBoxMenuItem documentSizeShowRelativeCheckBoxMenuItem;
    private javax.swing.JLabel editModeLabel;
    private javax.swing.JLabel encodingLabel;
    private javax.swing.JRadioButtonMenuItem hexadecimalCursorPositionModeRadioButtonMenuItem;
    private javax.swing.JRadioButtonMenuItem hexadecimalDocumentSizeModeRadioButtonMenuItem;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JRadioButtonMenuItem octalCursorPositionModeRadioButtonMenuItem;
    private javax.swing.JRadioButtonMenuItem octalDocumentSizeModeRadioButtonMenuItem;
    private javax.swing.JMenuItem positionCopyMenuItem;
    private javax.swing.JMenuItem positionGoToMenuItem;
    private javax.swing.JPopupMenu positionPopupMenu;

    public BinaryStatusPanel() {
        initComponents();
    }

    public void updateStatus() {
        updateCaretPosition();
        updateCursorPositionToolTip();
        updateDocumentSize();
        updateDocumentSizeToolTip();

        switch (cursorPositionFormat.getCodeType()) {
            case OCTAL: {
                octalCursorPositionModeRadioButtonMenuItem.setSelected(true);
                break;
            }
            case DECIMAL: {
                decimalCursorPositionModeRadioButtonMenuItem.setSelected(true);
                break;
            }
            case HEXADECIMAL: {
                hexadecimalCursorPositionModeRadioButtonMenuItem.setSelected(true);
                break;
            }
            default:
                throw CodeAreaUtils.getInvalidTypeException(cursorPositionFormat.getCodeType());
        }
        cursorPositionShowOffsetCheckBoxMenuItem.setSelected(cursorPositionFormat.isShowOffset());

        switch (documentSizeFormat.getCodeType()) {
            case OCTAL: {
                octalDocumentSizeModeRadioButtonMenuItem.setSelected(true);
                break;
            }
            case DECIMAL: {
                decimalDocumentSizeModeRadioButtonMenuItem.setSelected(true);
                break;
            }
            case HEXADECIMAL: {
                hexadecimalDocumentSizeModeRadioButtonMenuItem.setSelected(true);
                break;
            }
            default:
                throw CodeAreaUtils.getInvalidTypeException(documentSizeFormat.getCodeType());
        }
        documentSizeShowRelativeCheckBoxMenuItem.setSelected(documentSizeFormat.isShowRelative());
    }

    private void initComponents() {

        positionPopupMenu = new javax.swing.JPopupMenu();
        cursorPositionCodeTypeMenu = new javax.swing.JMenu();
        octalCursorPositionModeRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        decimalCursorPositionModeRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        hexadecimalCursorPositionModeRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        cursorPositionShowOffsetCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        positionCopyMenuItem = new javax.swing.JMenuItem();
        positionGoToMenuItem = new javax.swing.JMenuItem();
        documentSizePopupMenu = new javax.swing.JPopupMenu();
        documentSizeCodeTypeMenu = new javax.swing.JMenu();
        octalDocumentSizeModeRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        decimalDocumentSizeModeRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        hexadecimalDocumentSizeModeRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        documentSizeShowRelativeCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        documentSizeCopyMenuItem = new javax.swing.JMenuItem();
        documentSizeModeButtonGroup = new javax.swing.ButtonGroup();
        cursorPositionModeButtonGroup = new javax.swing.ButtonGroup();
        documentSizeLabel = new javax.swing.JLabel() {
            @Override
            public JToolTip createToolTip() {
                updateDocumentSizeToolTip();
                return super.createToolTip();
            }
        };
        cursorPositionLabel = new javax.swing.JLabel() {
            @Override
            public JToolTip createToolTip() {
                updateCursorPositionToolTip();
                return super.createToolTip();
            }
        };
        editModeLabel = new javax.swing.JLabel();
        encodingLabel = new javax.swing.JLabel();

        positionPopupMenu.setName("positionPopupMenu");

        cursorPositionCodeTypeMenu.setText("Code Type");
        cursorPositionCodeTypeMenu.setName("cursorPositionCodeTypeMenu");

        cursorPositionModeButtonGroup.add(octalCursorPositionModeRadioButtonMenuItem);
        octalCursorPositionModeRadioButtonMenuItem.setText("Show as octal");
        octalCursorPositionModeRadioButtonMenuItem.setName("octalCursorPositionModeRadioButtonMenuItem");
        octalCursorPositionModeRadioButtonMenuItem.addActionListener(this::octalCursorPositionModeRadioButtonMenuItemActionPerformed);
        cursorPositionCodeTypeMenu.add(octalCursorPositionModeRadioButtonMenuItem);

        cursorPositionModeButtonGroup.add(decimalCursorPositionModeRadioButtonMenuItem);
        decimalCursorPositionModeRadioButtonMenuItem.setSelected(true);
        decimalCursorPositionModeRadioButtonMenuItem.setText("Show as decimal");
        decimalCursorPositionModeRadioButtonMenuItem.setName("decimalCursorPositionModeRadioButtonMenuItem");
        decimalCursorPositionModeRadioButtonMenuItem.addActionListener(this::decimalCursorPositionModeRadioButtonMenuItemActionPerformed);
        cursorPositionCodeTypeMenu.add(decimalCursorPositionModeRadioButtonMenuItem);

        cursorPositionModeButtonGroup.add(hexadecimalCursorPositionModeRadioButtonMenuItem);
        hexadecimalCursorPositionModeRadioButtonMenuItem.setText("Show as hexadecimal");
        hexadecimalCursorPositionModeRadioButtonMenuItem.setName("hexadecimalCursorPositionModeRadioButtonMenuItem");
        hexadecimalCursorPositionModeRadioButtonMenuItem.addActionListener(this::hexadecimalCursorPositionModeRadioButtonMenuItemActionPerformed);
        cursorPositionCodeTypeMenu.add(hexadecimalCursorPositionModeRadioButtonMenuItem);

        positionPopupMenu.add(cursorPositionCodeTypeMenu);

        cursorPositionShowOffsetCheckBoxMenuItem.setSelected(true);
        cursorPositionShowOffsetCheckBoxMenuItem.setText("Show offset");
        cursorPositionShowOffsetCheckBoxMenuItem.setName("cursorPositionShowOffsetCheckBoxMenuItem");
        cursorPositionShowOffsetCheckBoxMenuItem.addActionListener(this::cursorPositionShowOffsetCheckBoxMenuItemActionPerformed);
        positionPopupMenu.add(cursorPositionShowOffsetCheckBoxMenuItem);

        jSeparator2.setName("jSeparator2");
        positionPopupMenu.add(jSeparator2);

        positionCopyMenuItem.setText("Copy");
        positionCopyMenuItem.setName("positionCopyMenuItem");
        positionCopyMenuItem.addActionListener(this::positionCopyMenuItemActionPerformed);
        positionPopupMenu.add(positionCopyMenuItem);

        positionGoToMenuItem.setText("Go To...");
        positionGoToMenuItem.setEnabled(false);
        positionGoToMenuItem.setName("positionGoToMenuItem");
        positionGoToMenuItem.addActionListener(this::positionGoToMenuItemActionPerformed);
        positionPopupMenu.add(positionGoToMenuItem);

        documentSizePopupMenu.setName("documentSizePopupMenu");

        documentSizeCodeTypeMenu.setText("Code Type");
        documentSizeCodeTypeMenu.setName("documentSizeCodeTypeMenu");

        documentSizeModeButtonGroup.add(octalDocumentSizeModeRadioButtonMenuItem);
        octalDocumentSizeModeRadioButtonMenuItem.setText("Show as octal");
        octalDocumentSizeModeRadioButtonMenuItem.setName("octalDocumentSizeModeRadioButtonMenuItem");
        octalDocumentSizeModeRadioButtonMenuItem.addActionListener(this::octalDocumentSizeModeRadioButtonMenuItemActionPerformed);
        documentSizeCodeTypeMenu.add(octalDocumentSizeModeRadioButtonMenuItem);

        documentSizeModeButtonGroup.add(decimalDocumentSizeModeRadioButtonMenuItem);
        decimalDocumentSizeModeRadioButtonMenuItem.setText("Show as decimal");
        decimalDocumentSizeModeRadioButtonMenuItem.setName("decimalDocumentSizeModeRadioButtonMenuItem");
        decimalDocumentSizeModeRadioButtonMenuItem.addActionListener(this::decimalDocumentSizeModeRadioButtonMenuItemActionPerformed);
        documentSizeCodeTypeMenu.add(decimalDocumentSizeModeRadioButtonMenuItem);

        documentSizeModeButtonGroup.add(hexadecimalDocumentSizeModeRadioButtonMenuItem);
        hexadecimalDocumentSizeModeRadioButtonMenuItem.setText("Show as hexadecimal");
        hexadecimalDocumentSizeModeRadioButtonMenuItem.setName("hexadecimalDocumentSizeModeRadioButtonMenuItem");
        hexadecimalDocumentSizeModeRadioButtonMenuItem.addActionListener(this::hexadecimalDocumentSizeModeRadioButtonMenuItemActionPerformed);
        documentSizeCodeTypeMenu.add(hexadecimalDocumentSizeModeRadioButtonMenuItem);

        documentSizePopupMenu.add(documentSizeCodeTypeMenu);

        documentSizeShowRelativeCheckBoxMenuItem.setSelected(true);
        documentSizeShowRelativeCheckBoxMenuItem.setText("Show relative size");
        documentSizeShowRelativeCheckBoxMenuItem.setName("documentSizeShowRelativeCheckBoxMenuItem");
        documentSizeShowRelativeCheckBoxMenuItem.addActionListener(this::documentSizeShowRelativeCheckBoxMenuItemActionPerformed);
        documentSizePopupMenu.add(documentSizeShowRelativeCheckBoxMenuItem);

        jSeparator1.setName("jSeparator1");
        documentSizePopupMenu.add(jSeparator1);

        documentSizeCopyMenuItem.setText("Copy");
        documentSizeCopyMenuItem.setName("documentSizeCopyMenuItem");
        documentSizeCopyMenuItem.addActionListener(this::documentSizeCopyMenuItemActionPerformed);
        documentSizePopupMenu.add(documentSizeCopyMenuItem);

        setName("Form");

        documentSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        documentSizeLabel.setText("0 (0)");
        documentSizeLabel.setToolTipText("Document size");
        documentSizeLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        documentSizeLabel.setComponentPopupMenu(documentSizePopupMenu);
        documentSizeLabel.setName("documentSizeLabel");

        cursorPositionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cursorPositionLabel.setText("0:0");
        cursorPositionLabel.setToolTipText("Cursor position");
        cursorPositionLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cursorPositionLabel.setComponentPopupMenu(positionPopupMenu);
        cursorPositionLabel.setName("cursorPositionLabel");
        cursorPositionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cursorPositionLabelMouseClicked(evt);
            }
        });

        editModeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        editModeLabel.setText("OVR");
        editModeLabel.setToolTipText("Edit mode");
        editModeLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        editModeLabel.setName("editModeLabel");
        editModeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editModeLabelMouseClicked(evt);
            }
        });

        encodingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        encodingLabel.setText("UTF-8");
        encodingLabel.setToolTipText("Active encoding");
        encodingLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        encodingLabel.setName("encodingLabel");
        encodingLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                encodingLabelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                encodingLabelMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                encodingLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(195, Short.MAX_VALUE)
                .addComponent(encodingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(documentSizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(cursorPositionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(editModeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(editModeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(documentSizeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(cursorPositionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(encodingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void editModeLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editModeLabelMouseClicked
//        if (statusControlHandler != null && evt.getButton() == MouseEvent.BUTTON1) {
//            if (editOperation == EditOperation.INSERT) {
//                statusControlHandler.changeEditOperation(EditOperation.OVERWRITE);
//            } else if (editOperation == EditOperation.OVERWRITE) {
//                statusControlHandler.changeEditOperation(EditOperation.INSERT);
//            }
//        }
    }//GEN-LAST:event_editModeLabelMouseClicked

    private void cursorPositionLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cursorPositionLabelMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() > 1) {
            // statusControlHandler.changeCursorPosition();
        }
    }//GEN-LAST:event_cursorPositionLabelMouseClicked

    private void positionGoToMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_positionGoToMenuItemActionPerformed
        // statusControlHandler.changeCursorPosition();
    }//GEN-LAST:event_positionGoToMenuItemActionPerformed

    private void positionCopyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_positionCopyMenuItemActionPerformed
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(cursorPositionLabel.getText()), null);
        } catch (IllegalStateException ex) {
            // ignore issues with clipboard
        }
    }//GEN-LAST:event_positionCopyMenuItemActionPerformed

    private void documentSizeCopyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_documentSizeCopyMenuItemActionPerformed
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(documentSizeLabel.getText()), null);
        } catch (IllegalStateException ex) {
            // ignore issues with clipboard
        }
    }//GEN-LAST:event_documentSizeCopyMenuItemActionPerformed

    private void encodingLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_encodingLabelMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            // Not supported
        } else {
            handleEncodingPopup(evt);
        }
    }//GEN-LAST:event_encodingLabelMouseClicked

    private void encodingLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_encodingLabelMousePressed
        handleEncodingPopup(evt);
    }//GEN-LAST:event_encodingLabelMousePressed

    private void encodingLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_encodingLabelMouseReleased
        handleEncodingPopup(evt);
    }//GEN-LAST:event_encodingLabelMouseReleased

    private void cursorPositionShowOffsetCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cursorPositionShowOffsetCheckBoxMenuItemActionPerformed
        cursorPositionFormat.setShowOffset(cursorPositionShowOffsetCheckBoxMenuItem.isSelected());
        updateCaretPosition();
    }//GEN-LAST:event_cursorPositionShowOffsetCheckBoxMenuItemActionPerformed

    private void documentSizeShowRelativeCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_documentSizeShowRelativeCheckBoxMenuItemActionPerformed
        documentSizeFormat.setShowRelative(documentSizeShowRelativeCheckBoxMenuItem.isSelected());
        updateDocumentSize();
        updateDocumentSizeToolTip();
    }//GEN-LAST:event_documentSizeShowRelativeCheckBoxMenuItemActionPerformed

    private void octalCursorPositionModeRadioButtonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_octalCursorPositionModeRadioButtonMenuItemActionPerformed
        cursorPositionFormat.setCodeType(PositionCodeType.OCTAL);
        updateCaretPosition();
    }//GEN-LAST:event_octalCursorPositionModeRadioButtonMenuItemActionPerformed

    private void decimalCursorPositionModeRadioButtonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decimalCursorPositionModeRadioButtonMenuItemActionPerformed
        cursorPositionFormat.setCodeType(PositionCodeType.DECIMAL);
        updateCaretPosition();
    }//GEN-LAST:event_decimalCursorPositionModeRadioButtonMenuItemActionPerformed

    private void hexadecimalCursorPositionModeRadioButtonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hexadecimalCursorPositionModeRadioButtonMenuItemActionPerformed
        cursorPositionFormat.setCodeType(PositionCodeType.HEXADECIMAL);
        updateCaretPosition();
    }//GEN-LAST:event_hexadecimalCursorPositionModeRadioButtonMenuItemActionPerformed

    private void octalDocumentSizeModeRadioButtonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_octalDocumentSizeModeRadioButtonMenuItemActionPerformed
        documentSizeFormat.setCodeType(PositionCodeType.OCTAL);
        updateDocumentSize();
    }//GEN-LAST:event_octalDocumentSizeModeRadioButtonMenuItemActionPerformed

    private void decimalDocumentSizeModeRadioButtonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decimalDocumentSizeModeRadioButtonMenuItemActionPerformed
        documentSizeFormat.setCodeType(PositionCodeType.DECIMAL);
        updateDocumentSize();
    }//GEN-LAST:event_decimalDocumentSizeModeRadioButtonMenuItemActionPerformed

    private void hexadecimalDocumentSizeModeRadioButtonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hexadecimalDocumentSizeModeRadioButtonMenuItemActionPerformed
        documentSizeFormat.setCodeType(PositionCodeType.HEXADECIMAL);
        updateDocumentSize();
    }//GEN-LAST:event_hexadecimalDocumentSizeModeRadioButtonMenuItemActionPerformed

    private void handleEncodingPopup(java.awt.event.MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            // Not supported
        }
    }

    @Override
    public void setCursorPosition(CodeAreaCaretPosition caretPosition) {
        this.caretPosition = caretPosition;
        updateCaretPosition();
        updateCursorPositionToolTip();
    }

    @Override
    public void setSelectionRange(SelectionRange selectionRange) {
        this.selectionRange = selectionRange;
        updateCaretPosition();
        updateCursorPositionToolTip();
        updateDocumentSize();
        updateDocumentSizeToolTip();
    }

    @Override
    public void setCurrentDocumentSize(long documentSize, long initialDocumentSize) {
        this.documentSize = documentSize;
        this.initialDocumentSize = initialDocumentSize;
        updateDocumentSize();
        updateDocumentSizeToolTip();
    }

    @Nonnull
    public String getEncoding() {
        return encodingLabel.getText();
    }

    public void setEncoding(String encodingName) {
        encodingLabel.setText(encodingName + " ^");
    }

    @Override
    public void setEditMode(EditMode editMode, EditOperation editOperation) {
        this.editOperation = editOperation;
        switch (editMode) {
            case READ_ONLY: {
                editModeLabel.setText(READONLY_EDIT_MODE_LABEL);
                break;
            }
            case EXPANDING:
            case CAPPED: {
                switch (editOperation) {
                    case INSERT: {
                        editModeLabel.setText(INSERT_EDIT_MODE_LABEL);
                        break;
                    }
                    case OVERWRITE: {
                        editModeLabel.setText(OVERWRITE_EDIT_MODE_LABEL);
                        break;
                    }
                    default:
                        throw CodeAreaUtils.getInvalidTypeException(editOperation);
                }
                break;
            }
            case INPLACE: {
                editModeLabel.setText(INPLACE_EDIT_MODE_LABEL);
                break;
            }
            default:
                throw CodeAreaUtils.getInvalidTypeException(editMode);
        }
    }

    private void updateCaretPosition() {
        if (caretPosition == null) {
            cursorPositionLabel.setText("-");
        } else {
            StringBuilder labelBuilder = new StringBuilder();
            if (selectionRange != null && !selectionRange.isEmpty()) {
                long first = selectionRange.getFirst();
                long last = selectionRange.getLast();
                labelBuilder.append(numberToPosition(first, cursorPositionFormat.getCodeType()));
                labelBuilder.append(" to ");
                labelBuilder.append(numberToPosition(last, cursorPositionFormat.getCodeType()));
            } else {
                labelBuilder.append(numberToPosition(caretPosition.getDataPosition(), cursorPositionFormat.getCodeType()));
                if (cursorPositionFormat.isShowOffset()) {
                    labelBuilder.append(":");
                    labelBuilder.append(caretPosition.getCodeOffset());
                }
            }
            cursorPositionLabel.setText(labelBuilder.toString());
        }
    }

    private void updateCursorPositionToolTip() {
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        if (caretPosition == null) {
            builder.append("Cursor position");
        } else {
            if (selectionRange != null && !selectionRange.isEmpty()) {
                long first = selectionRange.getFirst();
                long last = selectionRange.getLast();
                builder.append("Selection from<br>");
                builder.append(OCTAL_CODE_TYPE_LABEL + ": ").append(numberToPosition(first, PositionCodeType.OCTAL)).append("<br>");
                builder.append(DECIMAL_CODE_TYPE_LABEL + ": ").append(numberToPosition(first, PositionCodeType.DECIMAL)).append("<br>");
                builder.append(HEXADECIMAL_CODE_TYPE_LABEL + ": ").append(numberToPosition(first, PositionCodeType.HEXADECIMAL)).append("<br>");
                builder.append("<br>");
                builder.append("Selection to<br>");
                builder.append(OCTAL_CODE_TYPE_LABEL + ": ").append(numberToPosition(last, PositionCodeType.OCTAL)).append("<br>");
                builder.append(DECIMAL_CODE_TYPE_LABEL + ": ").append(numberToPosition(last, PositionCodeType.DECIMAL)).append("<br>");
                builder.append(HEXADECIMAL_CODE_TYPE_LABEL + ": ").append(numberToPosition(first, PositionCodeType.HEXADECIMAL)).append("<br>");
            } else {
                long dataPosition = caretPosition.getDataPosition();
                builder.append("Cursor position<br>");
                builder.append(OCTAL_CODE_TYPE_LABEL + ": ").append(numberToPosition(dataPosition, PositionCodeType.OCTAL)).append("<br>");
                builder.append(DECIMAL_CODE_TYPE_LABEL + ": ").append(numberToPosition(dataPosition, PositionCodeType.DECIMAL)).append("<br>");
                builder.append(HEXADECIMAL_CODE_TYPE_LABEL + ": ").append(numberToPosition(dataPosition, PositionCodeType.HEXADECIMAL));
                builder.append("</html>");
            }
        }

        cursorPositionLabel.setToolTipText(builder.toString());
    }

    private void updateDocumentSize() {
        if (documentSize == -1) {
            documentSizeLabel.setText(documentSizeFormat.isShowRelative() ? "0 (0)" : "0");
        } else {
            StringBuilder labelBuilder = new StringBuilder();
            if (selectionRange != null && !selectionRange.isEmpty()) {
                labelBuilder.append(numberToPosition(selectionRange.getLength(), documentSizeFormat.getCodeType()));
                labelBuilder.append(" of ");
                labelBuilder.append(numberToPosition(documentSize, documentSizeFormat.getCodeType()));
            } else {
                labelBuilder.append(numberToPosition(documentSize, documentSizeFormat.getCodeType()));
                if (documentSizeFormat.isShowRelative()) {
                    long difference = documentSize - initialDocumentSize;
                    labelBuilder.append(difference > 0 ? " (+" : " (");
                    labelBuilder.append(numberToPosition(difference, documentSizeFormat.getCodeType()));
                    labelBuilder.append(")");

                }
            }

            documentSizeLabel.setText(labelBuilder.toString());
        }
    }

    private void updateDocumentSizeToolTip() {
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        if (selectionRange != null && !selectionRange.isEmpty()) {
            long length = selectionRange.getLength();
            builder.append("Selection length<br>");
            builder.append(OCTAL_CODE_TYPE_LABEL + ": ").append(numberToPosition(length, PositionCodeType.OCTAL)).append("<br>");
            builder.append(DECIMAL_CODE_TYPE_LABEL + ": ").append(numberToPosition(length, PositionCodeType.DECIMAL)).append("<br>");
            builder.append(HEXADECIMAL_CODE_TYPE_LABEL + ": ").append(numberToPosition(length, PositionCodeType.HEXADECIMAL)).append("<br>");
            builder.append("<br>");
        }

        builder.append("Document size<br>");
        builder.append(OCTAL_CODE_TYPE_LABEL + ": ").append(numberToPosition(documentSize, PositionCodeType.OCTAL)).append("<br>");
        builder.append(DECIMAL_CODE_TYPE_LABEL + ": ").append(numberToPosition(documentSize, PositionCodeType.DECIMAL)).append("<br>");
        builder.append(HEXADECIMAL_CODE_TYPE_LABEL + ": ").append(numberToPosition(documentSize, PositionCodeType.HEXADECIMAL));
        builder.append("</html>");
        documentSizeLabel.setToolTipText(builder.toString());
    }

    @Nonnull
    private String numberToPosition(long value, PositionCodeType codeType) {
        if (value == 0) {
            return "0";
        }

        int spaceGroupSize;
        switch (codeType) {
            case OCTAL: {
                spaceGroupSize = octalSpaceGroupSize;
                break;
            }
            case DECIMAL: {
                spaceGroupSize = decimalSpaceGroupSize;
                break;
            }
            case HEXADECIMAL: {
                spaceGroupSize = hexadecimalSpaceGroupSize;
                break;
            }
            default:
                throw CodeAreaUtils.getInvalidTypeException(codeType);
        }

        long remainder = value > 0 ? value : -value;
        StringBuilder builder = new StringBuilder();
        int base = codeType.getBase();
        int groupSize = spaceGroupSize == 0 ? -1 : spaceGroupSize;
        while (remainder > 0) {
            if (groupSize >= 0) {
                if (groupSize == 0) {
                    builder.insert(0, ' ');
                    groupSize = spaceGroupSize - 1;
                } else {
                    groupSize--;
                }
            }

            int digit = (int) (remainder % base);
            remainder /= base;
            builder.insert(0, CodeAreaUtils.UPPER_HEX_CODES[digit]);
        }

        if (value < 0) {
            builder.insert(0, "-");
        }
        return builder.toString();
    }
}
