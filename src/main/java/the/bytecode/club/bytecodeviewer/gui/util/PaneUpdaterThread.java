package the.bytecode.club.bytecodeviewer.gui.util;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.util.Objects;
import java.util.regex.Matcher;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.components.MethodsRenderer;
import the.bytecode.club.bytecodeviewer.gui.components.SearchableRSyntaxTextArea;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.util.MethodParser;

import static the.bytecode.club.bytecodeviewer.gui.resourceviewer.TabbedPane.BLANK_COLOR;

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

/**
 * Allows us to run a background thread
 *
 * @author Konloch
 * @author DreamSworK
 */
public abstract class PaneUpdaterThread implements Runnable
{
    public ClassViewer viewer;
    public SearchableRSyntaxTextArea updateUpdaterTextArea;
    public JComboBox<Integer> methodsList;
    private Thread thread;
    public int paneIndex;
    public int decompilerViewIndex;
    
    public PaneUpdaterThread(int paneIndex, int decompilerViewIndex)
    {
        this.paneIndex = paneIndex;
        this.decompilerViewIndex = decompilerViewIndex;
    }
    
    public abstract void doShit();
    
    public void startNewThread()
    {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run()
    {
        if(decompilerViewIndex == 0)
            return;
        
        doShit();
        
        synchronizePane();
        
        attachCtrlMouseWheelZoom(updateUpdaterTextArea.getScrollPane(), updateUpdaterTextArea); //freezes the UI for some reason
        //probably cause BCV is doing dumb shit with the swing thread
    }

    public void attachCtrlMouseWheelZoom(RTextScrollPane scrollPane, RSyntaxTextArea panelArea) {
        if (scrollPane == null)
            return;

        scrollPane.addMouseWheelListener(e -> {
            if (panelArea == null || panelArea.getText().isEmpty())
                return;

            if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
                Font font = panelArea.getFont();
                int size = font.getSize();
                if (e.getWheelRotation() > 0) { //Up
                    panelArea.setFont(new Font(font.getName(), font.getStyle(), --size >= 2 ? --size : 2));
                } else { //Down
                    panelArea.setFont(new Font(font.getName(), font.getStyle(), ++size));
                }
            }
            e.consume();
        });
    }

    public final CaretListener caretListener = new CaretListener() {
        @Override
        public void caretUpdate(CaretEvent e) {
            MethodParser methods = viewer.methods.get(paneIndex);
            if (methods != null) {
                int methodLine = methods.findActiveMethod(updateUpdaterTextArea.getCaretLineNumber());
                if (methodLine != -1) {
                    if (BytecodeViewer.viewer.showClassMethods.isSelected()) {
                        if (methodsList != null) {
                            if (methodLine != (int) Objects.requireNonNull(methodsList.getSelectedItem())) {
                                methodsList.setSelectedItem(methodLine);
                            }
                        }
                    }
                    if (BytecodeViewer.viewer.synchronizedViewing.isSelected()) {
                        int panes = 2;
                        if (viewer.resourceViewPanel3.panel != null)
                            panes = 3;

                        for (int i = 0; i < panes; i++) {
                            if (i != paneIndex) {
                                ClassViewer.selectMethod(viewer, i, methods.getMethod(methodLine));
                            }
                        }
                    }
                }
            }
        }
    };

    public final ChangeListener viewportListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            int panes = 2;
            if (viewer.resourceViewPanel3.panel != null)
                panes = 3;

            if (BytecodeViewer.viewer.synchronizedViewing.isSelected()) {
                if (updateUpdaterTextArea.isShowing() && (updateUpdaterTextArea.hasFocus() || updateUpdaterTextArea.getMousePosition() != null)) {
                    int caretLine = updateUpdaterTextArea.getCaretLineNumber();
                    int maxViewLine = ClassViewer.getMaxViewLine(updateUpdaterTextArea);
                    int activeViewLine = ClassViewer.getViewLine(updateUpdaterTextArea);
                    int activeLine = (activeViewLine == maxViewLine && caretLine > maxViewLine) ? caretLine :
                            activeViewLine;
                    int activeLineDelta = -1;
                    MethodParser.Method activeMethod = null;
                    MethodParser activeMethods = viewer.methods.get(paneIndex);
                    if (activeMethods != null) {
                        int activeMethodLine = activeMethods.findActiveMethod(activeLine);
                        if (activeMethodLine != -1) {
                            activeLineDelta = activeLine - activeMethodLine;
                            activeMethod = activeMethods.getMethod(activeMethodLine);
                            ClassViewer.selectMethod(updateUpdaterTextArea, activeMethodLine);
                        }
                    }
                    for (int i = 0; i < panes; i++) {
                        if (i != paneIndex) {
                            int setLine = -1;

                            RSyntaxTextArea area = null;
                            switch (i) {
                            case 0:
                                area = viewer.resourceViewPanel1.updateThread.updateUpdaterTextArea;
                                break;
                            case 1:
                                area = viewer.resourceViewPanel2.updateThread.updateUpdaterTextArea;
                                break;
                            case 2:
                                area = viewer.resourceViewPanel3.updateThread.updateUpdaterTextArea;
                                break;
                            }

                            if (area != null) {
                                if (activeMethod != null && activeLineDelta >= 0) {
                                    MethodParser methods = viewer.methods.get(i);
                                    if (methods != null) {
                                        int methodLine = methods.findMethod(activeMethod);
                                        if (methodLine != -1) {
                                            int viewLine = ClassViewer.getViewLine(area);
                                            if (activeLineDelta != viewLine - methodLine) {
                                                setLine = methodLine + activeLineDelta;
                                            }
                                        }
                                    }
                                } else if (activeLine != ClassViewer.getViewLine(area)) {
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
    
    public void synchronizePane()
    {
        if(decompilerViewIndex == 5 || decompilerViewIndex < 0)
            return;
        
        JViewport viewport = updateUpdaterTextArea.getScrollPane().getViewport();
        viewport.addChangeListener(viewportListener);
        updateUpdaterTextArea.addCaretListener(caretListener);
        
        final MethodParser methods = viewer.methods.get(paneIndex);
        for (int i = 0; i < updateUpdaterTextArea.getLineCount(); i++) {
            String lineText = ClassViewer.getLineText(updateUpdaterTextArea, i);
            Matcher regexMatcher = MethodParser.regex.matcher(lineText);
            if (regexMatcher.find()) {
                String methodName = regexMatcher.group("name");
                String methodParams = regexMatcher.group("params");
                methods.addMethod(i, methodName, methodParams);
            }
        }

        if (BytecodeViewer.viewer.showClassMethods.isSelected()) {
            if (!methods.isEmpty()) {
                methodsList = new JComboBox<>();
                for (Integer line : methods.getMethodsLines()) {
                    methodsList.addItem(line);
                }
                methodsList.setRenderer(new MethodsRenderer(this));
                methodsList.addActionListener(e ->
                {
                    int line = (int) Objects.requireNonNull(methodsList.getSelectedItem());

                    RSyntaxTextArea area = null;
                    switch (paneIndex)
                    {
                        case 0:
                            area = viewer.resourceViewPanel1.updateThread.updateUpdaterTextArea;
                            break;
                        case 1:
                            area = viewer.resourceViewPanel2.updateThread.updateUpdaterTextArea;
                            break;
                        case 2:
                            area = viewer.resourceViewPanel3.updateThread.updateUpdaterTextArea;
                            break;
                    }

                    if (area != null)
                        ClassViewer.selectMethod(area, line);
                });

                JPanel panel = new JPanel(new BorderLayout());
                panel.add(updateUpdaterTextArea.getScrollPane().getColumnHeader().getComponent(0), BorderLayout.NORTH);
                panel.add(methodsList, BorderLayout.SOUTH);
                methodsList.setBackground(BLANK_COLOR);
                updateUpdaterTextArea.getScrollPane().getColumnHeader().removeAll();
                updateUpdaterTextArea.getScrollPane().getColumnHeader().add(panel);
            }
        }
    }
}