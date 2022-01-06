package the.bytecode.club.bytecodeviewer.gui.util;

import java.awt.BorderLayout;
import java.util.Objects;
import java.util.regex.Matcher;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.objectweb.asm.ClassWriter;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.compilers.Compiler;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.components.MethodsRenderer;
import the.bytecode.club.bytecodeviewer.gui.components.SearchableRSyntaxTextArea;
import the.bytecode.club.bytecodeviewer.gui.hexviewer.HexViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.BytecodeViewPanel;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.util.MethodParser;

import static the.bytecode.club.bytecodeviewer.gui.resourceviewer.TabbedPane.BLANK_COLOR;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.EDITABLE;

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
 * Updates the Bytecode View Panel in a background thread
 *
 * @author Konloch
 * @author WaterWolf
 * @author DreamSworK
 * @since 09/26/2011
 */

public class BytecodeViewPanelUpdater implements Runnable
{
    public final ClassViewer viewer;
    public final BytecodeViewPanel bytecodeViewPanel;
    private final JButton button;
    private final byte[] classBytes;
    
    
    public SearchableRSyntaxTextArea updateUpdaterTextArea;
    public JComboBox<Integer> methodsList;
    public boolean isPanelEditable;
    public boolean waitingFor;
    private Thread thread;
    
    public BytecodeViewPanelUpdater(BytecodeViewPanel bytecodeViewPanel, ClassViewer cv, byte[] classBytes, boolean isPanelEditable, JButton button)
    {
        this.viewer = cv;
        this.bytecodeViewPanel = bytecodeViewPanel;
        this.classBytes = classBytes;
        this.isPanelEditable = isPanelEditable;
        this.button = button;
        waitingFor = true;
    }
    
    public void processDisplay()
    {
        try
        {
            BytecodeViewer.updateBusyStatus(true);
        
            if (bytecodeViewPanel.decompiler != Decompiler.NONE)
            {
                //hex viewer
                if (bytecodeViewPanel.decompiler == Decompiler.HEXCODE_VIEWER)
                {
                    final ClassWriter cw = new ClassWriter(0);
                    viewer.resource.getResourceClassNode().accept(cw);
                
                    SwingUtilities.invokeLater(() ->
                    {
                        final HexViewer hex = new HexViewer(cw.toByteArray());
                        bytecodeViewPanel.add(hex);
                    });
                }
                else
                {
                    final Decompiler decompiler = bytecodeViewPanel.decompiler;
                
                    //perform decompiling inside of this thread
                    final String decompiledSource = decompiler.getDecompiler().decompileClassNode(viewer.resource.getResourceClassNode(), classBytes);
                
                    //set the swing components on the swing thread
                    SwingUtilities.invokeLater(() ->
                    {
                        buildTextArea(decompiler, decompiledSource);
                        waitingFor = false;
                    });
                
                    //hold this thread until the swing thread has finished attaching the components
                    while (waitingFor)
                    {
                        try {
                            Thread.sleep(1);
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException | NullPointerException e)
        {
            //ignore
        }
        catch (Exception e)
        {
            BytecodeViewer.handleException(e);
        }
        finally
        {
            viewer.resetDivider();
            BytecodeViewer.updateBusyStatus(false);
            SwingUtilities.invokeLater(() ->
            {
                if (button != null)
                    button.setEnabled(true);
            });
        }
    }
    
    public void startNewThread()
    {
        thread = new Thread(this, "Pane Update");
        thread.start();
    }

    @Override
    public void run()
    {
        if(bytecodeViewPanel.decompiler == Decompiler.NONE)
            return;
        
        processDisplay();
    
        if(bytecodeViewPanel.decompiler == Decompiler.HEXCODE_VIEWER)
            return;
        
        //nullcheck broken pane
        if(updateUpdaterTextArea == null || updateUpdaterTextArea.getScrollPane() == null
                || updateUpdaterTextArea.getScrollPane().getViewport() == null)
        {
            //build an error message
            SwingUtilities.invokeLater(() ->
                    buildTextArea(bytecodeViewPanel.decompiler, "Critical BCV Error"));
            return;
        }
        
        //this still freezes the swing UI
        synchronizePane();
    }

    public final CaretListener caretListener = new CaretListener()
    {
        @Override
        public void caretUpdate(CaretEvent e)
        {
            MethodParser methods = viewer.methods.get(bytecodeViewPanel.panelIndex);
            if (methods != null)
            {
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
                        if (viewer.bytecodeViewPanel3 != null)
                            panes = 3;

                        for (int i = 0; i < panes; i++) {
                            if (i != bytecodeViewPanel.panelIndex) {
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
            if (viewer.bytecodeViewPanel3 != null)
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
                    MethodParser activeMethods = viewer.methods.get(bytecodeViewPanel.panelIndex);
                    if (activeMethods != null) {
                        int activeMethodLine = activeMethods.findActiveMethod(activeLine);
                        if (activeMethodLine != -1) {
                            activeLineDelta = activeLine - activeMethodLine;
                            activeMethod = activeMethods.getMethod(activeMethodLine);
                            ClassViewer.selectMethod(updateUpdaterTextArea, activeMethodLine);
                        }
                    }
                    for (int i = 0; i < panes; i++) {
                        if (i != bytecodeViewPanel.panelIndex) {
                            int setLine = -1;

                            RSyntaxTextArea area = null;
                            switch (i) {
                            case 0:
                                area = viewer.bytecodeViewPanel1.updateThread.updateUpdaterTextArea;
                                break;
                            case 1:
                                area = viewer.bytecodeViewPanel2.updateThread.updateUpdaterTextArea;
                                break;
                            case 2:
                                area = viewer.bytecodeViewPanel3.updateThread.updateUpdaterTextArea;
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
        if(bytecodeViewPanel.decompiler == Decompiler.HEXCODE_VIEWER
                || bytecodeViewPanel.decompiler == Decompiler.NONE)
            return;
        
        SwingUtilities.invokeLater(()->
        {
            JViewport viewport = updateUpdaterTextArea.getScrollPane().getViewport();
            viewport.addChangeListener(viewportListener);
            updateUpdaterTextArea.addCaretListener(caretListener);
        });
        
        final MethodParser methods = viewer.methods.get(bytecodeViewPanel.panelIndex);
        for (int i = 0; i < updateUpdaterTextArea.getLineCount(); i++)
        {
            String lineText = updateUpdaterTextArea.getLineText(i);
            Matcher regexMatcher = MethodParser.regex.matcher(lineText);
            if (regexMatcher.find())
            {
                String methodName = regexMatcher.group("name");
                String methodParams = regexMatcher.group("params");
                methods.addMethod(i, methodName, methodParams);
            }
        }

        //TODO fix this
        if (BytecodeViewer.viewer.showClassMethods.isSelected())
        {
            if (!methods.isEmpty())
            {
                methodsList = new JComboBox<>();
                
                for (Integer line : methods.getMethodsLines())
                    methodsList.addItem(line);
                
                methodsList.setRenderer(new MethodsRenderer(this));
                methodsList.addActionListener(e ->
                {
                    int line = (int) Objects.requireNonNull(methodsList.getSelectedItem());

                    RSyntaxTextArea area = null;
                    switch (bytecodeViewPanel.panelIndex)
                    {
                        case 0:
                            area = viewer.bytecodeViewPanel1.updateThread.updateUpdaterTextArea;
                            break;
                        case 1:
                            area = viewer.bytecodeViewPanel2.updateThread.updateUpdaterTextArea;
                            break;
                        case 2:
                            area = viewer.bytecodeViewPanel3.updateThread.updateUpdaterTextArea;
                            break;
                    }

                    if (area != null)
                        ClassViewer.selectMethod(area, line);
                });

                JPanel panel = new JPanel(new BorderLayout());
                panel.add(updateUpdaterTextArea.getScrollPane().getColumnHeader().getComponent(0), BorderLayout.NORTH);
                panel.add(methodsList, BorderLayout.SOUTH);
                methodsList.setBackground(BLANK_COLOR);
    
                SwingUtilities.invokeLater(()->
                {
                    updateUpdaterTextArea.getScrollPane().getColumnHeader().removeAll();
                    updateUpdaterTextArea.getScrollPane().getColumnHeader().add(panel);
                });
            }
        }
    }
    
    public void buildTextArea(Decompiler decompiler, String decompiledSource)
    {
        updateUpdaterTextArea = new SearchableRSyntaxTextArea();
        
        Configuration.rstaTheme.apply(updateUpdaterTextArea);
        bytecodeViewPanel.add(updateUpdaterTextArea.getScrollPane());
        bytecodeViewPanel.add(updateUpdaterTextArea.getTitleHeader(), BorderLayout.NORTH);
        
        bytecodeViewPanel.textArea = updateUpdaterTextArea;
        bytecodeViewPanel.textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        bytecodeViewPanel.textArea.setCodeFoldingEnabled(true);
        bytecodeViewPanel.textArea.setAntiAliasingEnabled(true);
        bytecodeViewPanel.textArea.setText(decompiledSource);
        bytecodeViewPanel.textArea.setCaretPosition(0);
        bytecodeViewPanel.textArea.setEditable(isPanelEditable);
        
        if(isPanelEditable && decompiler == Decompiler.SMALI_DISASSEMBLER)
            bytecodeViewPanel.compiler = Compiler.SMALI_ASSEMBLER;
        else if(isPanelEditable && decompiler == Decompiler.KRAKATAU_DISASSEMBLER)
            bytecodeViewPanel.compiler = Compiler.KRAKATAU_ASSEMBLER;
        
        String editable = isPanelEditable ? " - " + EDITABLE : "";
        bytecodeViewPanel.textArea.getTitleHeader().setText(decompiler.getDecompilerName() + editable);
    }
}
