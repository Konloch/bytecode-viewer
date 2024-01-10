package the.bytecode.club.bytecodeviewer.gui.hexviewer;

import org.exbin.auxiliary.binary_data.ByteArrayData;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.CodeType;
import org.exbin.bined.EditMode;
import org.exbin.bined.RowWrappingMode;
import org.exbin.bined.highlight.swing.HighlightNonAsciiCodeAreaPainter;
import org.exbin.bined.swing.basic.CodeArea;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Binary/hexadecimal viewer based on BinEd library.
 *
 * @author hajdam
 */
public class HexViewer extends JPanel {

    private final CodeArea codeArea;
    private final JToolBar toolBar;
    private final BinaryStatusPanel statusPanel;
    private final ValuesPanel valuesPanel;
    private JPanel codeAreaPanel;
    private JScrollPane valuesPanelScrollBar;
    private boolean valuesPanelVisible = false;

    private final AbstractAction cycleCodeTypesAction;
    private JButton cycleCodeTypeButton;
    private BinaryStatusApi binaryStatus;
    private final AbstractAction goToAction;

    public HexViewer(byte[] contentData) {
        super(new BorderLayout());
        codeArea = new CodeArea();
        codeArea.setFocusTraversalKeysEnabled(false);
        codeArea.setPainter(new HighlightNonAsciiCodeAreaPainter(codeArea));
        toolBar = new JToolBar();
        statusPanel = new BinaryStatusPanel() {
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(0, super.getMinimumSize().height);
            }
        };
        valuesPanel = new ValuesPanel();
        codeArea.setContentData(new ByteArrayData(contentData));
        codeArea.setEditMode(EditMode.READ_ONLY);

        cycleCodeTypesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int codeTypePos = codeArea.getCodeType().ordinal();
                CodeType[] values = CodeType.values();
                CodeType next = codeTypePos + 1 >= values.length ? values[0] : values[codeTypePos + 1];
                codeArea.setCodeType(next);
                updateCycleButtonState();
            }
        };

        goToAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final GoToBinaryPanel goToPanel = new GoToBinaryPanel();
                goToPanel.setCursorPosition(codeArea.getCaret().getCaretPosition().getDataPosition());
                goToPanel.setMaxPosition(codeArea.getDataSize());
                final JDialog dialog = new JDialog((JFrame) SwingUtilities.getRoot(HexViewer.this), Dialog.ModalityType.APPLICATION_MODAL);
                OkCancelPanel okCancelPanel = new OkCancelPanel() {
                    @Override
                    protected void okAction() {
                        goToPanel.acceptInput();
                        codeArea.setCaretPosition(goToPanel.getTargetPosition());
                        codeArea.revealCursor();
                        dialog.setVisible(false);
                        dialog.dispose();
                        codeArea.requestFocus();
                    }

                    @Override
                    protected void cancelAction() {
                        dialog.setVisible(false);
                        dialog.dispose();
                    }
                };

                final String ESC_CANCEL = "esc-cancel";
                dialog.getRootPane().getActionMap().put(ESC_CANCEL, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okCancelPanel.cancelAction();
                    }
                });
                dialog.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ESC_CANCEL);
                okCancelPanel.setOkButtonText("Go To");
                dialog.setTitle("Go To Position");
                dialog.add(goToPanel, BorderLayout.CENTER);
                dialog.add(okCancelPanel, BorderLayout.SOUTH);
                dialog.pack();
                dialog.setLocationByPlatform(true);
                dialog.setVisible(true);
            }
        };

        init();
    }

    private void init() {
        cycleCodeTypesAction.putValue(Action.SHORT_DESCRIPTION, "Cycle through code types");

        cycleCodeTypeButton = new JButton();
        cycleCodeTypeButton.setAction(cycleCodeTypesAction);
        updateCycleButtonState();
        toolBar.add(cycleCodeTypeButton);
        JToggleButton lineWrappingToggleButton = new JToggleButton();
        lineWrappingToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/the/bytecode/club/bytecodeviewer/gui/hexviewer/resources/bined-linewrap.png")));
        lineWrappingToggleButton.setToolTipText("Toggle line wrapping");
        lineWrappingToggleButton.addActionListener(evt -> {
            if (codeArea.getRowWrapping() == RowWrappingMode.WRAPPING) {
                codeArea.setMaxBytesPerRow(16);
                codeArea.setRowWrapping(RowWrappingMode.NO_WRAPPING);
            } else {
                codeArea.setMaxBytesPerRow(0);
                codeArea.setRowWrapping(RowWrappingMode.WRAPPING);
            }
        });
        toolBar.add(lineWrappingToggleButton);

        add(toolBar, BorderLayout.NORTH);

        codeAreaPanel = new JPanel(new BorderLayout());
        codeAreaPanel.add(codeArea, BorderLayout.CENTER);
        codeArea.setComponentPopupMenu(new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                removeAll();
                final JPopupMenu menu = createPopupMenu();
                menu.show(invoker, x, y);
            }
        });

        valuesPanelScrollBar = new JScrollPane();
        valuesPanel.setCodeArea(codeArea);
        valuesPanel.updateValues();
        valuesPanelScrollBar.setViewportView(valuesPanel);
        valuesPanelScrollBar.setMinimumSize(new Dimension(10, valuesPanel.getMinimumSize().height));
        setShowValuesPanel(true);
        add(codeAreaPanel, BorderLayout.CENTER);

        registerBinaryStatus(statusPanel);
        add(statusPanel, BorderLayout.SOUTH);

        final String GO_TO_ACTION = "goToAction";
        codeArea.getActionMap().put(GO_TO_ACTION, goToAction);
        codeArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_G, HexViewer.getMetaMask()), GO_TO_ACTION);
        invalidate();
    }

    private void setShowValuesPanel(boolean show) {
        if (valuesPanelVisible != show) {
            if (show) {
                codeAreaPanel.add(valuesPanelScrollBar, BorderLayout.SOUTH);
                codeAreaPanel.revalidate();
                codeAreaPanel.repaint();
                valuesPanelVisible = true;
                valuesPanel.enableUpdate();
            } else {
                valuesPanel.disableUpdate();
                codeAreaPanel.remove(valuesPanelScrollBar);
                codeAreaPanel.revalidate();
                codeAreaPanel.repaint();
                valuesPanelVisible = false;
            }
        }
    }

    public void registerBinaryStatus(BinaryStatusApi binaryStatusApi) {
        this.binaryStatus = binaryStatusApi;
        codeArea.addCaretMovedListener((CodeAreaCaretPosition caretPosition) -> binaryStatus.setCursorPosition(caretPosition));
        codeArea.addSelectionChangedListener(() -> binaryStatus.setSelectionRange(codeArea.getSelection()));
        codeArea.addDataChangedListener(() -> binaryStatus.setCurrentDocumentSize(codeArea.getDataSize(), codeArea.getDataSize()));
        binaryStatus.setCurrentDocumentSize(codeArea.getDataSize(), codeArea.getDataSize());

        codeArea.addEditModeChangedListener(binaryStatus::setEditMode);
        binaryStatus.setEditMode(codeArea.getEditMode(), codeArea.getActiveOperation());
    }

    /**
     * Returns platform specific down mask filter.
     *
     * @return down mask for meta keys
     */
    public static int getMetaMask() {
        try {
            switch (java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) {
                case java.awt.Event.META_MASK:
                    return KeyEvent.META_DOWN_MASK;
                case java.awt.Event.SHIFT_MASK:
                    return KeyEvent.SHIFT_DOWN_MASK;
                case java.awt.Event.ALT_MASK:
                    return KeyEvent.ALT_DOWN_MASK;
                default:
                    return KeyEvent.CTRL_DOWN_MASK;
            }
        } catch (java.awt.HeadlessException ex) {
            return KeyEvent.CTRL_DOWN_MASK;
        }
    }

    @Nonnull
    private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenu viewMenu = new JMenu("View");
        JMenu codeTypeMenu = new JMenu("Code Type");
        ButtonGroup codeTypeButtonGroup = new ButtonGroup();
        JRadioButtonMenuItem binaryCodeTypeMenuItem = new JRadioButtonMenuItem(new AbstractAction("Binary") {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setCodeType(CodeType.BINARY);
                updateCycleButtonState();
                menu.setVisible(false);
            }
        });
        codeTypeButtonGroup.add(binaryCodeTypeMenuItem);
        JRadioButtonMenuItem octalCodeTypeMenuItem = new JRadioButtonMenuItem(new AbstractAction("Octal") {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setCodeType(CodeType.OCTAL);
                updateCycleButtonState();
                menu.setVisible(false);
            }
        });
        codeTypeButtonGroup.add(octalCodeTypeMenuItem);
        JRadioButtonMenuItem decimalCodeTypeMenuItem = new JRadioButtonMenuItem(new AbstractAction("Decimal") {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setCodeType(CodeType.DECIMAL);
                updateCycleButtonState();
                menu.setVisible(false);
            }
        });
        codeTypeButtonGroup.add(decimalCodeTypeMenuItem);
        JRadioButtonMenuItem hexadecimalCodeTypeMenuItem = new JRadioButtonMenuItem(new AbstractAction("Hexadecimal") {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setCodeType(CodeType.HEXADECIMAL);
                updateCycleButtonState();
                menu.setVisible(false);
            }
        });
        codeTypeButtonGroup.add(hexadecimalCodeTypeMenuItem);
        codeTypeMenu.add(binaryCodeTypeMenuItem);
        codeTypeMenu.add(octalCodeTypeMenuItem);
        codeTypeMenu.add(decimalCodeTypeMenuItem);
        codeTypeMenu.add(hexadecimalCodeTypeMenuItem);
        switch (codeArea.getCodeType()) {
            case BINARY: {
                binaryCodeTypeMenuItem.setSelected(true);
                break;
            }
            case OCTAL: {
                octalCodeTypeMenuItem.setSelected(true);
                break;
            }
            case DECIMAL: {
                decimalCodeTypeMenuItem.setSelected(true);
                break;
            }
            case HEXADECIMAL: {
                hexadecimalCodeTypeMenuItem.setSelected(true);
                break;
            }
        }

        viewMenu.add(codeTypeMenu);
        JCheckBoxMenuItem showValuesPanelMenuItem = new JCheckBoxMenuItem("Show values panel");
        showValuesPanelMenuItem.setSelected(valuesPanelVisible);
        showValuesPanelMenuItem.addActionListener((event) -> {
            setShowValuesPanel(showValuesPanelMenuItem.isSelected());
            menu.setVisible(false);
        });
        viewMenu.add(showValuesPanelMenuItem);
        JCheckBoxMenuItem codeColorizationMenuItem = new JCheckBoxMenuItem("Code Colorization");
        codeColorizationMenuItem.setSelected(((HighlightNonAsciiCodeAreaPainter) codeArea.getPainter()).isNonAsciiHighlightingEnabled());
        codeColorizationMenuItem.addActionListener((event) -> {
            ((HighlightNonAsciiCodeAreaPainter) codeArea.getPainter()).setNonAsciiHighlightingEnabled(codeColorizationMenuItem.isSelected());
            menu.setVisible(false);
        });
        viewMenu.add(codeColorizationMenuItem);
        menu.add(viewMenu);

        final JMenuItem copyMenuItem = new JMenuItem("Copy");
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, HexViewer.getMetaMask()));
        copyMenuItem.setEnabled(codeArea.hasSelection());
        copyMenuItem.addActionListener((ActionEvent e) -> codeArea.copy());
        menu.add(copyMenuItem);

        final JMenuItem selectAllMenuItem = new JMenuItem("Select All");
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, HexViewer.getMetaMask()));
        selectAllMenuItem.addActionListener((ActionEvent e) -> codeArea.selectAll());
        menu.add(selectAllMenuItem);
        menu.addSeparator();

        final JMenuItem goToMenuItem = new JMenuItem("Go To...");
        goToMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, HexViewer.getMetaMask()));
        goToMenuItem.addActionListener(goToAction);
        menu.add(goToMenuItem);

        return menu;
    }

    private void updateCycleButtonState() {
        CodeType codeType = codeArea.getCodeType();
        cycleCodeTypeButton.setText(codeType.name().substring(0, 3));
    }
}
