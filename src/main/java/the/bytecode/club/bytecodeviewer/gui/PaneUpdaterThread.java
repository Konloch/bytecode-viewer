package the.bytecode.club.bytecodeviewer.gui;

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

import com.jhe.hexed.JHexEditor;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.Methods.*;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;

/**
 * Updates a pane
 * 
 * @author Konloch
 * @author DreamSworK
 */
public class PaneUpdaterThread extends Thread {

	private Decompiler decompiler;
	private int paneId;
	private JPanel target;
	private ClassViewer viewer;
	private RSyntaxTextArea panelArea;
	private JComboBox<Integer> methodsList;
    private JButton button;

	public PaneUpdaterThread(ClassViewer viewer, Decompiler decompiler, int paneId, JPanel target, JButton button) {
		this.decompiler = decompiler;
		this.paneId = paneId;
		this.target = target;
		this.viewer = viewer;
        this.button = button;
	}

    private final CaretListener caretListener = new CaretListener() {
        @Override
        public void caretUpdate(CaretEvent e) {
            Methods methods = viewer.methods.get(paneId);
            if (methods != null) {
                int methodLine = methods.findActiveMethod(panelArea.getCaretLineNumber());
                if (methodLine != -1) {
                    if (BytecodeViewer.viewer.showMethodsList.isSelected()) {
                        if (methodsList != null) {
                            if (methodLine != (int) methodsList.getSelectedItem()) {
                                methodsList.setSelectedItem(methodLine);
                            }
                        }
                    }
                    if (BytecodeViewer.viewer.synchronizeViewing.isSelected()) {
                        for (int i = 0; i < viewer.javas.size(); i++) {
                            if (i != paneId) {
                                ClassViewer.selectMethod(viewer, i, methods.getMethod(methodLine));
                            }
                        }
                    }
                }
            }
        }
    };

    private final ChangeListener viewportListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (BytecodeViewer.viewer.synchronizeViewing.isSelected()) {
                if (panelArea.isShowing() && (panelArea.hasFocus() || panelArea.getMousePosition() != null)) {
                    int caretLine = panelArea.getCaretLineNumber();
                    int maxViewLine = ClassViewer.getMaxViewLine(panelArea);
                    int activeViewLine = ClassViewer.getViewLine(panelArea);
                    int activeLine = (activeViewLine == maxViewLine && caretLine > maxViewLine) ? caretLine : activeViewLine;
                    int activeLineDelta = -1;
                    Method activeMethod = null;
                    Methods activeMethods = viewer.methods.get(paneId);
                    if (activeMethods != null) {
                        int activeMethodLine = activeMethods.findActiveMethod(activeLine);
                        if (activeMethodLine != -1) {
                            activeLineDelta = activeLine - activeMethodLine;
                            activeMethod = activeMethods.getMethod(activeMethodLine);
                            ClassViewer.selectMethod(panelArea, activeMethodLine);
                        }
                    }
                    for (int i = 0; i < viewer.javas.size(); i++) {
                        if (i != paneId) {
                            int setLine = -1;
                            RSyntaxTextArea area = viewer.javas.get(i);
                            if (area != null) {
                                if (activeMethod != null && activeLineDelta >= 0) {
                                    Methods methods = viewer.methods.get(i);
                                    if (methods != null) {
                                        int methodLine = methods.findMethod(activeMethod);
                                        if (methodLine != -1) {
                                            int viewLine = ClassViewer.getViewLine(area);
                                            if (activeLineDelta != viewLine - methodLine) {
                                                setLine = methodLine + activeLineDelta;
                                            }
                                        }
                                    }
                                }
                                else if (activeLine != ClassViewer.getViewLine(area)) {
                                    setLine = activeLine;
                                }
                                if (setLine >= 0) {
                                    ClassViewer.setViewLine(area, setLine);
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    class MethodsRenderer extends JLabel implements ListCellRenderer<Object> {
        public MethodsRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            Methods methods = viewer.methods.get(paneId);
            Method method = methods.getMethod((Integer) value);
            setText(method.toString());
            return this;
        }
    }

	public void run() {
		try {
			final byte[] b = BytecodeViewer.getClassBytes(viewer.container, viewer.cn.name + ".class");
            if (decompiler != Decompiler.HEXCODE) {
                panelArea = new RSyntaxTextArea();
                panelArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                panelArea.setCodeFoldingEnabled(true);
                panelArea.setAntiAliasingEnabled(true);
                final RTextScrollPane scrollPane = new RTextScrollPane(panelArea);
                JViewport viewport = scrollPane.getViewport();
                viewport.addChangeListener(viewportListener);
                panelArea.addCaretListener(caretListener);
                panelArea.setText(decompiler.decompileClassNode(viewer.cn, b));
                final Methods methods = viewer.methods.get(paneId);
                for (int i = 0; i < panelArea.getLineCount(); i++) {
                    String lineText = ClassViewer.getLineText(panelArea, i);
                    Matcher regexMatcher = Methods.regex.matcher(lineText);
                    if (regexMatcher.find()) {
                        String methodName = regexMatcher.group("name");
                        String methodParams = regexMatcher.group("params");
                        methods.addMethod(i, methodName, methodParams);
                    }
                }
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem synchronize = new JMenuItem("Synchronize");
                synchronize.addActionListener(new ClassViewer.SynchronizeActionListener(viewer, paneId));
                popupMenu.add(synchronize);
                popupMenu.addSeparator();
                for (Component component : panelArea.getPopupMenu().getComponents()) {
                    popupMenu.add(component);
                }
                panelArea.setPopupMenu(popupMenu);
                panelArea.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            int line = e.getY() > panelArea.getLineHeight() ? e.getY() / panelArea.getLineHeight() : 0;
                            viewer.activeLines.set(paneId, line);
                        }
                    }
                });
                panelArea.setCaretPosition(0);
                panelArea.setEditable(viewer.isPaneEditable(paneId));
                panelArea.addKeyListener(new KeyListener() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                            viewer.requestFocus(paneId);
                        }

                        BytecodeViewer.checkHotKey(e);
                    }

                    @Override
                    public void keyReleased(KeyEvent arg0) {
                    }

                    @Override
                    public void keyTyped(KeyEvent arg0) {
                    }
                });
                scrollPane.setColumnHeaderView(new JLabel(decompiler.getName() + " Decompiler - Editable: " + panelArea.isEditable()));
                panelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) BytecodeViewer.viewer.fontSpinner.getValue()));

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        target.add(scrollPane);
                    }
                });
                viewer.updatePane(paneId, panelArea, decompiler);

                if (BytecodeViewer.viewer.showMethodsList.isSelected()) {
                    if (!methods.isEmpty()) {
                        methodsList = new JComboBox<>();
                        for (Integer line : methods.getMethodsLines()) {
                            methodsList.addItem(line);
                        }
                        methodsList.setRenderer(new MethodsRenderer());
                        methodsList.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int line = (int) methodsList.getSelectedItem();
                                ClassViewer.selectMethod(viewer.javas.get(paneId), line);
                            }
                        });
                        JPanel searchPanel = viewer.searches.get(paneId);
                        searchPanel.add(methodsList, BorderLayout.SOUTH);
                    }
                }
            } else {
                final JHexEditor hex = new JHexEditor(b);
                hex.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int)BytecodeViewer.viewer.fontSpinner.getValue()));
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        target.add(hex);
                    }
                });
            }
		} catch(Exception e) {
			new ExceptionUI(e);
		} finally {
			viewer.resetDivider();
			BytecodeViewer.viewer.setIcon(false);
			if(button != null)
				button.setEnabled(true);
		}
	}
}