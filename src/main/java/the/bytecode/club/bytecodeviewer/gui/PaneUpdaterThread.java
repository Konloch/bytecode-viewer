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

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.util.MethodParser;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Matcher;

import static the.bytecode.club.bytecodeviewer.gui.TabbedPane.BLANK;

/**
 * Allows us to run a background thread
 *
 * @author Konloch
 * @author DreamSworK
 */

public abstract class PaneUpdaterThread extends Thread
{
    public ClassViewer viewer;
    public RSyntaxTextArea panelArea;
    public RTextScrollPane scrollPane;
    public JComboBox<Integer> methodsList;
    public int decompiler;
    public int paneId;

    public abstract void doShit();

    @Override
    public void run() {
        doShit();
        synchronizePane();
        //attachCtrlMouseWheelZoom(scrollPane, panelArea); //freezes the UI for some reason, probably cause BCV is doing dumb shit with the swing thread
    }

    public void attachCtrlMouseWheelZoom(RTextScrollPane scrollPane, RSyntaxTextArea panelArea)
    {
        if(scrollPane == null)
            return;

        scrollPane.addMouseWheelListener(new MouseWheelListener()
        {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                if(panelArea == null || panelArea.getText().isEmpty())
                    return;

                if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0)
                {
                    Font font = panelArea.getFont();
                    int size = font.getSize();
                    if(e.getWheelRotation() > 0)
                    { //Up
                        panelArea.setFont(new Font(font.getName(), font.getStyle(), --size >= 2 ? --size : 2));
                    }
                    else
                    { //Down
                        panelArea.setFont(new Font(font.getName(), font.getStyle(), ++size));
                    }
                }
                e.consume();
            }
        });
    }

    public final CaretListener caretListener = new CaretListener()
    {
        @Override
        public void caretUpdate(CaretEvent e)
        {
            MethodParser methods = viewer.methods.get(paneId);
            if (methods != null)
            {
                int methodLine = methods.findActiveMethod(panelArea.getCaretLineNumber());
                if (methodLine != -1)
                {
                    if (BytecodeViewer.viewer.showClassMethods.isSelected())
                    {
                        if (methodsList != null)
                        {
                            if (methodLine != (int) methodsList.getSelectedItem())
                            {
                                methodsList.setSelectedItem(methodLine);
                            }
                        }
                    }
                    if (BytecodeViewer.viewer.synchronizedViewing.isSelected())
                    {
                        int panes = 2;
                        if(viewer.panel3 != null)
                            panes = 3;

                        for (int i = 0; i < panes; i++)
                        {
                            if (i != paneId)
                            {
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
            if(viewer.panel3 != null)
                panes = 3;

            if (BytecodeViewer.viewer.synchronizedViewing.isSelected()) {
                if (panelArea.isShowing() && (panelArea.hasFocus() || panelArea.getMousePosition() != null)) {
                    int caretLine = panelArea.getCaretLineNumber();
                    int maxViewLine = ClassViewer.getMaxViewLine(panelArea);
                    int activeViewLine = ClassViewer.getViewLine(panelArea);
                    int activeLine = (activeViewLine == maxViewLine && caretLine > maxViewLine) ? caretLine : activeViewLine;
                    int activeLineDelta = -1;
                    MethodParser.Method activeMethod = null;
                    MethodParser activeMethods = viewer.methods.get(paneId);
                    if (activeMethods != null) {
                        int activeMethodLine = activeMethods.findActiveMethod(activeLine);
                        if (activeMethodLine != -1) {
                            activeLineDelta = activeLine - activeMethodLine;
                            activeMethod = activeMethods.getMethod(activeMethodLine);
                            ClassViewer.selectMethod(panelArea, activeMethodLine);
                        }
                    }
                    for (int i = 0; i < panes; i++) {
                        if (i != paneId)
                        {
                            int setLine = -1;

                            RSyntaxTextArea area = null;
                            switch(i)
                            {
                                case 0:
                                    area = viewer.t1.panelArea;
                                    break;
                                case 1:
                                    area = viewer.t2.panelArea;
                                    break;
                                case 2:
                                    area = viewer.t3.panelArea;
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

    class MethodsRenderer extends JLabel implements ListCellRenderer<Object>
    {
        public MethodsRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            MethodParser methods = viewer.methods.get(paneId);
            MethodParser.Method method = methods.getMethod((Integer) value);
            setText(method.toString());
            return this;
        }
    }

    public void synchronizePane()
    {
        JViewport viewport = scrollPane.getViewport();
        viewport.addChangeListener(viewportListener);
        panelArea.addCaretListener(caretListener);

        final MethodParser methods = viewer.methods.get(paneId);
        for (int i = 0; i < panelArea.getLineCount(); i++)
        {
            String lineText = ClassViewer.getLineText(panelArea, i);
            Matcher regexMatcher = MethodParser.regex.matcher(lineText);
            if (regexMatcher.find())
            {
                String methodName = regexMatcher.group("name");
                String methodParams = regexMatcher.group("params");
                methods.addMethod(i, methodName, methodParams);
            }
        }

        if (BytecodeViewer.viewer.showClassMethods.isSelected())
        {
            if (!methods.isEmpty()) {
                methodsList = new JComboBox<>();
                for (Integer line : methods.getMethodsLines()) {
                    methodsList.addItem(line);
                }
                methodsList.setRenderer(new PaneUpdaterThread.MethodsRenderer());
                methodsList.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int line = (int) methodsList.getSelectedItem();

                        RSyntaxTextArea area = null;
                        switch(paneId)
                        {
                            case 0:
                                area = viewer.t1.panelArea;
                                break;
                            case 1:
                                area = viewer.t2.panelArea;
                                break;
                            case 2:
                                area = viewer.t3.panelArea;
                                break;
                        }

                        if(area != null)
                            ClassViewer.selectMethod(area, line);
                    }
                });

                JPanel panel = new JPanel(new BorderLayout());
                panel.add(scrollPane.getColumnHeader().getComponent(0), BorderLayout.NORTH);
                panel.add(methodsList, BorderLayout.SOUTH);
                methodsList.setBackground(BLANK);
                scrollPane.getColumnHeader().removeAll();
                scrollPane.getColumnHeader().add(panel);
            }
        }
    }
}