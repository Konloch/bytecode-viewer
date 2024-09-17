package the.bytecode.club.bytecodeviewer.gui.components.actions;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.BytecodeViewPanel;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.util.BytecodeViewPanelUpdater;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.ClassFileContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassFieldLocation;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Element;
import java.awt.event.ActionEvent;
import java.util.HashMap;

/**
 * Created by Bl3nd.
 * Date: 9/7/2024
 */
public class GoToAction extends AbstractAction
{
    private final ClassFileContainer container;

    public GoToAction(ClassFileContainer classFileContainer)
    {
        this.container = classFileContainer;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        RSyntaxTextArea textArea = (RSyntaxTextArea) e.getSource();
        int line = textArea.getCaretLineNumber() + 1;
        int column = textArea.getCaretOffsetFromLineStart();

        container.fieldMembers.values().forEach(fields -> fields.forEach(field -> {
            if (field.line == line && field.columnStart - 1 <= column && field.columnEnd >= column)
            {
                Element root = textArea.getDocument().getDefaultRootElement();
                // Open the class that is associated with the field's owner.
                if (!field.owner.equals(container.getName()))
                {
                    openFieldClass(field, textArea);
                    return;
                }

                ClassFieldLocation first = fields.get(0);
                int startOffset = root.getElement(first.line - 1).getStartOffset() + (first.columnStart - 1);
                textArea.setCaretPosition(startOffset);
            }
        }));

        container.methodParameterMembers.values().forEach(parameters -> parameters.forEach(parameter -> {
            if (parameter.line == line && parameter.columnStart - 1 <= column && parameter.columnEnd >= column)
            {
                Element root = textArea.getDocument().getDefaultRootElement();
                if (parameter.decRef.equalsIgnoreCase("declaration"))
                {
                    int startOffset = root.getElement(parameter.line - 1).getStartOffset() + (parameter.columnStart - 1);
                    textArea.setCaretPosition(startOffset);
                } else
                {
                    String method = parameter.method;
                    parameters.stream().filter(classParameterLocation -> classParameterLocation.method.equals(method)).forEach(classParameterLocation -> {
                        if (classParameterLocation.decRef.equalsIgnoreCase("declaration"))
                        {
                            int startOffset = root.getElement(classParameterLocation.line - 1).getStartOffset() + (classParameterLocation.columnStart - 1);
                            textArea.setCaretPosition(startOffset);
                        }
                    });
                }
            }
        }));

        container.methodLocalMembers.values().forEach(localMembers -> localMembers.forEach(localMember -> {
            if (localMember.line == line && localMember.columnStart - 1 <= column && localMember.columnEnd >= column)
            {
                Element root = textArea.getDocument().getDefaultRootElement();
                if (localMember.decRef.equals("declaration"))
                {
                    int startOffset = root.getElement(localMember.line - 1).getStartOffset() + (localMember.columnStart - 1);
                    textArea.setCaretPosition(startOffset);
                } else
                {
                    String method = localMember.method;
                    localMembers.stream().filter(classLocalVariableLocation -> classLocalVariableLocation.method.equals(method)).forEach(classLocalVariableLocation -> {
                        if (classLocalVariableLocation.decRef.equalsIgnoreCase("declaration"))
                        {
                            int startOffset = root.getElement(classLocalVariableLocation.line - 1).getStartOffset() + (classLocalVariableLocation.columnStart - 1);
                            textArea.setCaretPosition(startOffset);
                        }
                    });
                }
            }
        }));
    }

    private void openFieldClass(ClassFieldLocation field, RSyntaxTextArea textArea)
    {
        String token = textArea.modelToToken(textArea.getCaretPosition()).getLexeme();
        ResourceContainer resourceContainer = BytecodeViewer.getFileContainer(container.getParentContainer());
        if (resourceContainer != null)
        {
            String s = container.getImport(field.owner);
            BytecodeViewer.viewer.workPane.addClassResource(resourceContainer, s + ".class");
            ClassViewer activeResource = (ClassViewer) BytecodeViewer.viewer.workPane.getActiveResource();
            HashMap<String, ClassFileContainer> classFiles = BytecodeViewer.viewer.workPane.classFiles;
            Thread thread = new Thread(() -> {
                try
                {
                    BytecodeViewer.updateBusyStatus(true);
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                } finally
                {
                    BytecodeViewer.updateBusyStatus(false);
                }

                String s2 = activeResource.resource.workingName + "-" + this.container.getDecompiler();
                ClassFileContainer classFileContainer = classFiles.get(s2);
                classFileContainer.fieldMembers.forEach((field1, field2) -> {
                    if (field1.equals(token))
                    {
                        field2.forEach(classFieldLocation -> {
                            if (classFieldLocation.type.equals("declaration"))
                            {
                                for (int i = 0; i < 3; i++)
                                {
                                    BytecodeViewPanel panel = activeResource.getPanel(i);
                                    if (panel.textArea != null)
                                    {
                                        if (panel.decompiler.getDecompilerName().equals(this.container.getDecompiler()))
                                        {
                                            Element root = panel.textArea.getDocument().getDefaultRootElement();
                                            int startOffset = root.getElement(classFieldLocation.line - 1).getStartOffset() + (classFieldLocation.columnStart - 1);
                                            panel.textArea.setCaretPosition(startOffset);
                                            for (CaretListener caretListener : panel.textArea.getCaretListeners())
                                            {
                                                if (caretListener instanceof BytecodeViewPanelUpdater.MarkerCaretListener)
                                                {
                                                    BytecodeViewPanelUpdater.MarkerCaretListener markerCaretListener = (BytecodeViewPanelUpdater.MarkerCaretListener) caretListener;
                                                    markerCaretListener.caretUpdate(new CaretEvent(panel.textArea)
                                                    {
                                                        @Override
                                                        public int getDot()
                                                        {
                                                            return panel.textArea.getCaret().getDot();
                                                        }

                                                        @Override
                                                        public int getMark()
                                                        {
                                                            return 0;
                                                        }
                                                    });
                                                }
                                            }

                                            panel.textArea.requestFocusInWindow();

                                            break;
                                        }
                                    }
                                }
                            }
                        });
                    }
                });
            });
            thread.start();
        }
    }
}
