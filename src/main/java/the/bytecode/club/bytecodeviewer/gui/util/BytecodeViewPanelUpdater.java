/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
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

package the.bytecode.club.bytecodeviewer.gui.util;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.SmartHighlightPainter;
import org.objectweb.asm.ClassWriter;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.compilers.Compiler;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.components.MethodsRenderer;
import the.bytecode.club.bytecodeviewer.gui.components.MyErrorStripe;
import the.bytecode.club.bytecodeviewer.gui.components.RSyntaxTextAreaHighlighterEx;
import the.bytecode.club.bytecodeviewer.gui.components.SearchableRSyntaxTextArea;
import the.bytecode.club.bytecodeviewer.gui.components.actions.GoToAction;
import the.bytecode.club.bytecodeviewer.gui.hexviewer.HexViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.BytecodeViewPanel;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.ClassFileContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.*;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.TokenUtil;
import the.bytecode.club.bytecodeviewer.util.MethodParser;
import the.bytecode.club.bytecodeviewer.util.SleepUtil;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.regex.Matcher;

import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.EDITABLE;

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

    public MarkerCaretListener markerCaretListener;
    private MyErrorStripe errorStripe;
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
                    String decompilerName = decompiler.getDecompilerName();
                    final String workingDecompilerName = viewer.resource.workingName + "-" + decompilerName;

                    //perform decompiling inside of this thread
                    final String decompiledSource = decompiler.getDecompiler().decompileClassNode(viewer.resource.getResourceClassNode(), classBytes);

                    ClassFileContainer container = new ClassFileContainer(
                        viewer.resource.workingName,
                        decompilerName,
                        decompiledSource,
                        viewer.resource.container
                    );

                    if (!BytecodeViewer.viewer.workPane.classFiles.containsKey(workingDecompilerName))
                    {
                        boolean parsed = container.parse();
                        BytecodeViewer.viewer.workPane.classFiles.put(workingDecompilerName, container);
                        container.hasBeenParsed = parsed;
                    }

                    //set the swing components on the swing thread
                    SwingUtilities.invokeLater(() ->
                    {
                        buildTextArea(decompiler, decompiledSource);
                        waitingFor = false;
                    });

                    //hold this thread until the swing thread has finished attaching the components
                    while (waitingFor)
                    {
                        SleepUtil.sleep(1);
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException | NullPointerException e)
        {
            e.printStackTrace();
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
        if (bytecodeViewPanel.decompiler == Decompiler.NONE)
            return;

        processDisplay();

        if (bytecodeViewPanel.decompiler == Decompiler.HEXCODE_VIEWER)
            return;

        //nullcheck broken pane
        if (updateUpdaterTextArea == null
            || updateUpdaterTextArea.getScrollPane() == null
            || updateUpdaterTextArea.getScrollPane().getViewport() == null)
        {
            //build an error message
            SwingUtilities.invokeLater(() -> buildTextArea(bytecodeViewPanel.decompiler, "Critical BCV Error"));
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

                if (methodLine != -1)
                {
                    if (BytecodeViewer.viewer.showClassMethods.isSelected())
                    {
                        if (methodsList != null)
                        {
                            if (methodLine != (int) Objects.requireNonNull(methodsList.getSelectedItem()))
                            {
                                methodsList.setSelectedItem(methodLine);
                            }
                        }
                    }
                    if (BytecodeViewer.viewer.synchronizedViewing.isSelected())
                    {
                        int panes = 2;
                        if (viewer.bytecodeViewPanel3 != null)
                            panes = 3;

                        for (int i = 0; i < panes; i++)
                        {
                            if (i != bytecodeViewPanel.panelIndex)
                            {
                                ClassViewer.selectMethod(viewer, i, methods.getMethod(methodLine));
                            }
                        }
                    }
                }
            }
        }
    };

    public final ChangeListener viewportListener = new ChangeListener()
    {
        @Override
        public void stateChanged(ChangeEvent e)
        {
            int panes = 2;
            if (viewer.bytecodeViewPanel3 != null)
                panes = 3;

            if (BytecodeViewer.viewer.synchronizedViewing.isSelected())
            {
                if (updateUpdaterTextArea.isShowing()
                    && (updateUpdaterTextArea.hasFocus() || updateUpdaterTextArea.getMousePosition() != null))
                {
                    int caretLine = updateUpdaterTextArea.getCaretLineNumber();
                    int maxViewLine = ClassViewer.getMaxViewLine(updateUpdaterTextArea);
                    int activeViewLine = ClassViewer.getViewLine(updateUpdaterTextArea);
                    int activeLine = (activeViewLine == maxViewLine && caretLine > maxViewLine) ? caretLine : activeViewLine;
                    int activeLineDelta = -1;

                    MethodParser.Method activeMethod = null;
                    MethodParser activeMethods = viewer.methods.get(bytecodeViewPanel.panelIndex);

                    if (activeMethods != null)
                    {
                        int activeMethodLine = activeMethods.findActiveMethod(activeLine);

                        if (activeMethodLine != -1)
                        {
                            activeLineDelta = activeLine - activeMethodLine;
                            activeMethod = activeMethods.getMethod(activeMethodLine);
                            ClassViewer.selectMethod(updateUpdaterTextArea, activeMethodLine);
                        }
                    }

                    for (int i = 0; i < panes; i++)
                    {
                        if (i != bytecodeViewPanel.panelIndex)
                        {
                            int setLine = -1;

                            RSyntaxTextArea area = null;
                            switch (i)
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
                            {
                                if (activeMethod != null && activeLineDelta >= 0)
                                {
                                    MethodParser methods = viewer.methods.get(i);
                                    if (methods != null)
                                    {
                                        int methodLine = methods.findMethod(activeMethod);
                                        if (methodLine != -1)
                                        {
                                            int viewLine = ClassViewer.getViewLine(area);

                                            if (activeLineDelta != viewLine - methodLine)
                                                setLine = methodLine + activeLineDelta;
                                        }
                                    }
                                }
                                else if (activeLine != ClassViewer.getViewLine(area))
                                {
                                    setLine = activeLine;
                                }

                                if (setLine >= 0)
                                    ClassViewer.setViewLine(area, setLine);
                            }
                        }
                    }
                }
            }
        }
    };

    public void synchronizePane()
    {
        if (bytecodeViewPanel.decompiler == Decompiler.HEXCODE_VIEWER || bytecodeViewPanel.decompiler == Decompiler.NONE)
            return;

        SwingUtilities.invokeLater(() ->
        {
            JViewport viewport = updateUpdaterTextArea.getScrollPane().getViewport();
            viewport.addChangeListener(viewportListener);
            updateUpdaterTextArea.addCaretListener(caretListener);
        });

        final MethodParser methods = viewer.methods.get(bytecodeViewPanel.panelIndex);

        for (int i = 0; i < updateUpdaterTextArea.getLineCount(); i++)
        {
            String lineText = updateUpdaterTextArea.getLineText(i);
            Matcher regexMatcher = MethodParser.REGEX.matcher(lineText);

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
                methodsList.setBackground(new Color(0, 0, 0, 0));

                SwingUtilities.invokeLater(() ->
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
        bytecodeViewPanel.add(updateUpdaterTextArea.getTextAreaSearchPanel(), BorderLayout.NORTH);
        bytecodeViewPanel.add(updateUpdaterTextArea.getScrollPane());

        bytecodeViewPanel.textArea = updateUpdaterTextArea;

        bytecodeViewPanel.textArea.setMarkOccurrencesColor(Color.ORANGE);
        bytecodeViewPanel.textArea.setHighlighter(new RSyntaxTextAreaHighlighterEx());

        if (bytecodeViewPanel.decompiler != Decompiler.BYTECODE_DISASSEMBLER)
        {
            bytecodeViewPanel.textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        }
        else
        {
            AbstractTokenMakerFactory tokenMakerFactory = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
            tokenMakerFactory.putMapping("text/javaBytecode", "the.bytecode.club.bytecodeviewer.decompilers.bytecode.JavaBytecodeTokenMaker");
            bytecodeViewPanel.textArea.setSyntaxEditingStyle("text/javaBytecode");
        }

        bytecodeViewPanel.textArea.setCodeFoldingEnabled(true);
        bytecodeViewPanel.textArea.setText(decompiledSource);
        bytecodeViewPanel.textArea.setCaretPosition(0);
        bytecodeViewPanel.textArea.setEditable(isPanelEditable);

        if (isPanelEditable && decompiler == Decompiler.SMALI_DISASSEMBLER)
            bytecodeViewPanel.compiler = Compiler.SMALI_ASSEMBLER;
        else if (isPanelEditable && decompiler == Decompiler.KRAKATAU_DISASSEMBLER)
            bytecodeViewPanel.compiler = Compiler.KRAKATAU_ASSEMBLER;

        String editable = isPanelEditable ? " - " + EDITABLE : "";
        bytecodeViewPanel.textArea.getTextAreaSearchPanel().getTitleHeader().setText(decompiler.getDecompilerName() + editable);

        errorStripe = new MyErrorStripe(bytecodeViewPanel.textArea);
        bytecodeViewPanel.add(errorStripe, BorderLayout.LINE_END);

        bytecodeViewPanel.revalidate();
        bytecodeViewPanel.repaint();

        String classContainerName = viewer.resource.workingName + "-" + decompiler.getDecompilerName();
        ClassFileContainer classFileContainer = BytecodeViewer.viewer.workPane.classFiles.get(classContainerName);
        bytecodeViewPanel.textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK), "goToAction");
        bytecodeViewPanel.textArea.getActionMap().put("goToAction", new GoToAction(classFileContainer));

        bytecodeViewPanel.textArea.addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseMoved(MouseEvent e)
            {
                if (classFileContainer != null && classFileContainer.hasBeenParsed)
                {
                    if (e.isControlDown())
                    {
                        RSyntaxTextArea textArea = (RSyntaxTextArea) e.getSource();
                        Token token = textArea.viewToToken(e.getPoint());

                        if (token != null)
                        {
                            String lexeme = token.getLexeme();
                            if (classFileContainer.fieldMembers.containsKey(lexeme)
                                || classFileContainer.methodMembers.containsKey(lexeme)
                                || classFileContainer.methodLocalMembers.containsKey(lexeme)
                                || classFileContainer.methodParameterMembers.containsKey(lexeme)
                                || classFileContainer.classReferences.containsKey(lexeme))
                            {
                                textArea.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            }
                        }
                    }
                    else
                    {
                        if (bytecodeViewPanel.textArea.getCursor().getType() != Cursor.TEXT_CURSOR)
                            bytecodeViewPanel.textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
                    }
                }
            }
        });

        bytecodeViewPanel.textArea.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (classFileContainer != null
                    && classFileContainer.hasBeenParsed)
                {
                    if (e.isControlDown())
                    {
                        RSyntaxTextArea textArea = (RSyntaxTextArea) e.getSource();
                        textArea.getActionMap().get("goToAction").actionPerformed(new ActionEvent(textArea, ActionEvent.ACTION_PERFORMED, "goToAction"));
                    }
                }
            }
        });

        markerCaretListener = new MarkerCaretListener(classContainerName);
        bytecodeViewPanel.textArea.addCaretListener(markerCaretListener);
    }

    private void markOccurrences(RSyntaxTextArea textArea, ClassFileContainer classFileContainer, MyErrorStripe errorStripe)
    {
        //prevent NPE
        if (classFileContainer == null)
            return;

        RSyntaxTextAreaHighlighterEx highlighterEx = (RSyntaxTextAreaHighlighterEx) textArea.getHighlighter();
        Token token = textArea.modelToToken(textArea.getCaretPosition());

        if (token == null)
        {
            token = textArea.modelToToken(textArea.getCaretPosition() - 1);

            if (token == null)
            {
                highlighterEx.clearMarkOccurrencesHighlights();
                errorStripe.refreshMarkers();
                return;
            }
        }

        token = TokenUtil.getToken(textArea, token);
        if (token == null)
        {
            highlighterEx.clearMarkOccurrencesHighlights();
            return;
        }

        int line = textArea.getCaretLineNumber() + 1;
        int column = textArea.getCaretOffsetFromLineStart();
        Token finalToken = token;

		/*
		Fields
		 */
        markField(textArea, classFileContainer, line, column, finalToken, highlighterEx);

        /*
        Methods
         */
        markMethod(textArea, classFileContainer, line, column, finalToken, highlighterEx);

		/*
		Method parameters
		 */
        markMethodParameter(textArea, classFileContainer, line, column, finalToken, highlighterEx);

		/*
		Method local variables
		 */
        markMethodLocalVariable(textArea, classFileContainer, line, column, finalToken, highlighterEx);

        /*
        Class references
         */
        markClasses(textArea, classFileContainer, line, column, finalToken, highlighterEx);

        errorStripe.refreshMarkers();
    }

    private void markField(RSyntaxTextArea textArea, ClassFileContainer classFileContainer,
                           int line, int column, Token finalToken, RSyntaxTextAreaHighlighterEx highlighterEx)
    {
        classFileContainer.fieldMembers.values().forEach(fields -> fields.forEach(field ->
        {
            if (field.line == line && field.columnStart - 1 <= column && field.columnEnd >= column)
            {
                try
                {
                    Element root = textArea.getDocument().getDefaultRootElement();

                    for (ClassFieldLocation location : classFileContainer.getFieldLocationsFor(finalToken.getLexeme()))
                    {
                        int startOffset = root.getElement(location.line - 1).getStartOffset() + (location.columnStart - 1);
                        int endOffset = root.getElement(location.line - 1).getStartOffset() + (location.columnEnd - 1);
                        highlighterEx.addMarkedOccurrenceHighlight(startOffset, endOffset, new SmartHighlightPainter());
                    }
                }
                catch (BadLocationException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        }));
    }

    private void markMethod(RSyntaxTextArea textArea, ClassFileContainer classFileContainer,
                            int line, int column, Token finalToken, RSyntaxTextAreaHighlighterEx highlighterEx)
    {
        classFileContainer.methodMembers.values().forEach(methods -> methods.forEach(method ->
        {
            String owner;
            String parameters;

            if (method.line == line && method.columnStart - 1 <= column
                && method.columnEnd >= column)
            {
                owner = method.owner;
                parameters = method.methodParameterTypes;
                Element root = textArea.getDocument().getDefaultRootElement();

                for (ClassMethodLocation location : classFileContainer.getMethodLocationsFor(finalToken.getLexeme()))
                {
                    try
                    {
                        if (Objects.equals(owner, location.owner)
                            && Objects.equals(parameters, location.methodParameterTypes))
                        {
                            int startOffset = root.getElement(location.line - 1).getStartOffset() + (location.columnStart - 1);
                            int endOffset = root.getElement(location.line - 1).getStartOffset() + (location.columnEnd - 1);
                            highlighterEx.addMarkedOccurrenceHighlight(startOffset, endOffset, new SmartHighlightPainter());
                        }
                    }
                    catch (BadLocationException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        }));
    }

    /**
     * Search through the text area and mark all occurrences that match the selected token.
     *
     * @param textArea           the text area
     * @param classFileContainer the container
     * @param line               the caret line
     * @param column             the caret column
     * @param finalToken         the token
     * @param highlighterEx      the highlighter
     */
    private void markMethodParameter(RSyntaxTextArea textArea, ClassFileContainer classFileContainer,
                                     int line, int column, Token finalToken, RSyntaxTextAreaHighlighterEx highlighterEx)
    {
        classFileContainer.methodParameterMembers.values().forEach(parameters -> parameters.forEach(parameter ->
        {
            String method;
            if (parameter.line == line && parameter.columnStart - 1 <= column && parameter.columnEnd >= column)
            {
                method = parameter.method;
                try
                {
                    Element root = textArea.getDocument().getDefaultRootElement();

                    for (ClassParameterLocation location : classFileContainer.getParameterLocationsFor(finalToken.getLexeme()))
                    {
                        if (Objects.equals(method, location.method))
                        {
                            int startOffset = root.getElement(location.line - 1).getStartOffset() + (location.columnStart - 1);
                            int endOffset = root.getElement(location.line - 1).getStartOffset() + (location.columnEnd - 1);
                            highlighterEx.addMarkedOccurrenceHighlight(startOffset, endOffset, new SmartHighlightPainter());
                        }
                    }
                }
                catch (BadLocationException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        }));
    }

    /**
     * Search through the text area and mark all occurrences that match the selected token.
     *
     * @param textArea           the text area
     * @param classFileContainer the container
     * @param line               the caret line
     * @param column             the caret column
     * @param finalToken         the token
     * @param highlighterEx      the highlighter
     */
    private void markMethodLocalVariable(RSyntaxTextArea textArea, ClassFileContainer classFileContainer,
                                         int line, int column, Token finalToken, RSyntaxTextAreaHighlighterEx highlighterEx)
    {
        classFileContainer.methodLocalMembers.values().forEach(localVariables -> localVariables.forEach(localVariable ->
        {
            String method;
            if (localVariable.line == line
                && localVariable.columnStart - 1 <= column
                && localVariable.columnEnd >= column)
            {
                method = localVariable.method;
                try
                {
                    Element root = textArea.getDocument().getDefaultRootElement();

                    for (ClassLocalVariableLocation location : classFileContainer.getLocalLocationsFor(finalToken.getLexeme()))
                    {
                        if (Objects.equals(method, location.method))
                        {
                            int startOffset = root.getElement(location.line - 1).getStartOffset() + (location.columnStart - 1);
                            int endOffset = root.getElement(location.line - 1).getStartOffset() + (location.columnEnd - 1);
                            highlighterEx.addMarkedOccurrenceHighlight(startOffset, endOffset, new SmartHighlightPainter());
                        }
                    }
                }
                catch (BadLocationException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        }));
    }

    private void markClasses(RSyntaxTextArea textArea, ClassFileContainer classFileContainer,
                             int line, int column, Token finalToken, RSyntaxTextAreaHighlighterEx highlighterEx)
    {
        classFileContainer.classReferences.values().forEach(classes -> classes.forEach(clazz ->
        {
            if (clazz.line == line && clazz.columnStart - 1 <= column && clazz.columnEnd - 1 >= column)
            {
                try
                {
                    Element root = textArea.getDocument().getDefaultRootElement();

                    for (ClassReferenceLocation location : classFileContainer.getClassReferenceLocationsFor(finalToken.getLexeme()))
                    {
                        int startOffset = root.getElement(location.line - 1).getStartOffset() + (location.columnStart - 1);
                        int endOffset = root.getElement(location.line - 1).getStartOffset() + (location.columnEnd - 1);
                        highlighterEx.addMarkedOccurrenceHighlight(startOffset, endOffset, new SmartHighlightPainter());
                    }
                }
                catch (Exception ignored)
                {
                }
            }
        }));
    }

    public class MarkerCaretListener implements CaretListener
    {
        private final String classContainerName;

        public MarkerCaretListener(String classContainerName)
        {
            this.classContainerName = classContainerName;
        }

        @Override
        public void caretUpdate(CaretEvent e)
        {
            SearchableRSyntaxTextArea textArea = (SearchableRSyntaxTextArea) e.getSource();
            RSyntaxTextAreaHighlighterEx highlighterEx = (RSyntaxTextAreaHighlighterEx) bytecodeViewPanel.textArea.getHighlighter();
            highlighterEx.clearMarkOccurrencesHighlights();
            markOccurrences(textArea, BytecodeViewer.viewer.workPane.classFiles.get(classContainerName), errorStripe);
        }
    }
}
