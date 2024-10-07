package the.bytecode.club.bytecodeviewer.gui.components.actions;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.BytecodeViewPanel;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.util.BytecodeViewPanelUpdater;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.ClassFileContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassFieldLocation;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassMethodLocation;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassReferenceLocation;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.TokenUtil;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Element;
import java.awt.event.ActionEvent;
import java.util.HashMap;

/**
 * This action is triggered by a user typing (CTRL+B). This goes to a specific variables declaration whether it be in the opened class, or a class within the jar.
 * <p>
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

        container.fieldMembers.values().forEach(fields -> fields.forEach(field ->
        {
            if (field.line == line
                && field.columnStart - 1 <= column
                && field.columnEnd >= column)
            {
                Element root = textArea.getDocument().getDefaultRootElement();

                // Open the class that is associated with the field's owner.
                if (!field.owner.equals(container.getName()))
                {
                    open(textArea, false, true, false);
                    return;
                }

                ClassFieldLocation first = fields.get(0);
                int startOffset = root.getElement(first.line - 1).getStartOffset() + (first.columnStart - 1);
                textArea.setCaretPosition(startOffset);
            }
        }));

        container.methodParameterMembers.values().forEach(parameters -> parameters.forEach(parameter ->
        {
            if (parameter.line == line && parameter.columnStart - 1 <= column && parameter.columnEnd >= column)
            {
                Element root = textArea.getDocument().getDefaultRootElement();
                if (parameter.decRef.equalsIgnoreCase("declaration"))
                {
                    int startOffset = root.getElement(parameter.line - 1).getStartOffset() + (parameter.columnStart - 1);
                    textArea.setCaretPosition(startOffset);
                }
                else
                {
                    String method = parameter.method;
                    parameters.stream().filter(classParameterLocation -> classParameterLocation.method.equals(method)).forEach(classParameterLocation ->
                    {
                        if (classParameterLocation.decRef.equalsIgnoreCase("declaration"))
                        {
                            int startOffset = root.getElement(classParameterLocation.line - 1).getStartOffset() + (classParameterLocation.columnStart - 1);
                            textArea.setCaretPosition(startOffset);
                        }
                    });
                }
            }
        }));

        container.methodLocalMembers.values().forEach(localMembers -> localMembers.forEach(localMember ->
        {
            if (localMember.line == line && localMember.columnStart - 1 <= column && localMember.columnEnd >= column)
            {
                Element root = textArea.getDocument().getDefaultRootElement();

                if (localMember.decRef.equals("declaration"))
                {
                    int startOffset = root.getElement(localMember.line - 1).getStartOffset() + (localMember.columnStart - 1);
                    textArea.setCaretPosition(startOffset);
                }
                else
                {
                    String method = localMember.method;
                    localMembers.stream().filter(classLocalVariableLocation -> classLocalVariableLocation.method.equals(method)).forEach(classLocalVariableLocation ->
                    {
                        if (classLocalVariableLocation.decRef.equalsIgnoreCase("declaration"))
                        {
                            int startOffset = root.getElement(classLocalVariableLocation.line - 1).getStartOffset() + (classLocalVariableLocation.columnStart - 1);
                            textArea.setCaretPosition(startOffset);
                        }
                    });
                }
            }
        }));

        container.methodMembers.values().forEach(methods -> methods.forEach(method ->
        {
            if (method.line == line && method.columnStart - 1 <= column && method.columnEnd >= column)
            {
                Element root = textArea.getDocument().getDefaultRootElement();

                if (method.decRef.equalsIgnoreCase("declaration"))
                {
                    int startOffset = root.getElement(method.line - 1).getStartOffset() + (method.columnStart - 1);
                    textArea.setCaretPosition(startOffset);
                }
                else
                {
                    methods.stream().filter(classMethodLocation -> classMethodLocation.signature.equals(method.signature)).forEach(classMethodLocation ->
                    {
                        if (classMethodLocation.decRef.equalsIgnoreCase("declaration"))
                        {
                            int startOffset = root.getElement(classMethodLocation.line - 1).getStartOffset() + (classMethodLocation.columnStart - 1);
                            textArea.setCaretPosition(startOffset);
                        }
                    });

                    open(textArea, false, false, true);
                }
            }
        }));

        container.classReferences.values().forEach(classes -> classes.forEach(clazz ->
        {
            String name;
            if (clazz.line == line && clazz.columnStart - 1 <= column && clazz.columnEnd - 1 >= column)
            {
                name = clazz.owner;
                Element root = textArea.getDocument().getDefaultRootElement();

                if (clazz.type.equals("declaration"))
                {
                    int startOffset = root.getElement(clazz.line - 1).getStartOffset() + (clazz.columnStart - 1);
                    textArea.setCaretPosition(startOffset);
                }
                else
                {
                    classes.stream().filter(classReferenceLocation -> classReferenceLocation.owner.equals(name)).forEach(classReferenceLocation ->
                    {
                        if (classReferenceLocation.type.equals("declaration"))
                        {
                            int startOffset = root.getElement(classReferenceLocation.line - 1).getStartOffset() + (classReferenceLocation.columnStart - 1);
                            textArea.setCaretPosition(startOffset);
                        }
                    });

                    // Should not really do anything when the class is already open
                    open(textArea, true, false, false);
                }
            }
        }));
    }

    private ClassFileContainer openClass(String lexeme, boolean field, boolean method)
    {
        if (lexeme.equals(container.getName()))
            return null;

        ResourceContainer resourceContainer = BytecodeViewer.getFileContainer(container.getParentContainer());

        if (resourceContainer == null)
            return null;

        if (field)
        {
            String className = container.getClassForField(lexeme);
            BytecodeViewer.viewer.workPane.addClassResource(resourceContainer, className + ".class");
            ClassViewer activeResource = (ClassViewer) BytecodeViewer.viewer.workPane.getActiveResource();
            HashMap<String, ClassFileContainer> classFiles = BytecodeViewer.viewer.workPane.classFiles;
            return wait(classFiles, activeResource);
        }
        else if (method)
        {
            ClassMethodLocation classMethodLocation = container.getMethodLocationsFor(lexeme).get(0);
            ClassReferenceLocation classReferenceLocation = null;

            try
            {
                classReferenceLocation = container.getClassReferenceLocationsFor(classMethodLocation.owner).get(0);
            }
            catch (Exception ignored)
            {
            }

            if (classReferenceLocation == null)
                return null;

            String packagePath = classReferenceLocation.packagePath;

            if (packagePath.startsWith("java") || packagePath.startsWith("javax") || packagePath.startsWith("com.sun"))
                return null;

            String resourceName = classMethodLocation.owner;
            if (!packagePath.isEmpty())
                resourceName = packagePath + "/" + classMethodLocation.owner;

            if (resourceContainer.resourceClasses.containsKey(resourceName))
            {
                BytecodeViewer.viewer.workPane.addClassResource(resourceContainer, resourceName + ".class");
                ClassViewer activeResource = (ClassViewer) BytecodeViewer.viewer.workPane.getActiveResource();
                HashMap<String, ClassFileContainer> classFiles = BytecodeViewer.viewer.workPane.classFiles;
                return wait(classFiles, activeResource);
            }
        }
        else
        {
            ClassReferenceLocation classReferenceLocation = container.getClassReferenceLocationsFor(lexeme).get(0);
            String packagePath = classReferenceLocation.packagePath;

            if (packagePath.startsWith("java") || packagePath.startsWith("javax") || packagePath.startsWith("com.sun"))
                return null;

            String resourceName = lexeme;
            if (!packagePath.isEmpty())
            {
                resourceName = packagePath + "/" + lexeme;
            }

            if (resourceContainer.resourceClasses.containsKey(resourceName))
            {
                BytecodeViewer.viewer.workPane.addClassResource(resourceContainer, resourceName + ".class");
                ClassViewer activeResource = (ClassViewer) BytecodeViewer.viewer.workPane.getActiveResource();
                HashMap<String, ClassFileContainer> classFiles = BytecodeViewer.viewer.workPane.classFiles;
                return wait(classFiles, activeResource);
            }
        }

        return null;
    }

    private void open(RSyntaxTextArea textArea, boolean isClass, boolean isField, boolean isMethod)
    {
        Thread thread = new Thread(() ->
        {
            Token token = textArea.modelToToken(textArea.getCaretPosition());
            token = TokenUtil.getToken(textArea, token);
            String lexeme = token.getLexeme();
            ClassFileContainer classFileContainer;

            if (isClass)
            {
                classFileContainer = openClass(lexeme, false, false);

                if (classFileContainer == null)
                    return;

                classFileContainer.classReferences.forEach((className, classReference) ->
                {
                    if (className.equals(lexeme))
                    {
                        classReference.forEach(classReferenceLocation ->
                        {
                            if (classReferenceLocation.type.equals("declaration"))
                                moveCursor(classReferenceLocation.line, classReferenceLocation.columnStart);
                        });
                    }
                });
            }
            else if (isField)
            {
                classFileContainer = openClass(lexeme, true, false);
                if (classFileContainer == null)
                    return;

                classFileContainer.fieldMembers.forEach((fieldName, fields) ->
                {
                    if (fieldName.equals(lexeme))
                    {
                        fields.forEach(classFieldLocation ->
                        {
                            if (classFieldLocation.type.equals("declaration"))
                                moveCursor(classFieldLocation.line, classFieldLocation.columnStart);
                        });
                    }
                });
            }
            else if (isMethod)
            {
                classFileContainer = openClass(lexeme, false, true);

                if (classFileContainer == null)
                    return;

                classFileContainer.methodMembers.forEach((methodName, methods) ->
                {
                    if (methodName.equals(lexeme))
                    {
                        methods.forEach(method ->
                        {
                            if (method.decRef.equalsIgnoreCase("declaration"))
                                moveCursor(method.line, method.columnStart);
                        });
                    }
                });
            }
        }, "Open Class");

        thread.start();
    }

    private ClassFileContainer wait(HashMap<String, ClassFileContainer> classFiles, ClassViewer activeResource)
    {
        String containerName = activeResource.resource.workingName + "-" + this.container.getDecompiler();
        try
        {
            BytecodeViewer.updateBusyStatus(true);
            Thread.getAllStackTraces().forEach((name, stackTrace) ->
            {
                if (name.getName().equals("Pane Update"))
                {
                    try
                    {
                        name.join();
                    }
                    catch (InterruptedException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            BytecodeViewer.updateBusyStatus(false);
        }

        return classFiles.get(containerName);
    }

    private void moveCursor(int line, int columnStart)
    {
        for (int i = 0; i < 3; i++)
        {
            BytecodeViewPanel panel = ((ClassViewer) BytecodeViewer.viewer.workPane.getActiveResource()).getPanel(i);
            if (panel.decompiler.getDecompilerName().equals(this.container.getDecompiler()))
            {
                Element root = panel.textArea.getDocument().getDefaultRootElement();
                int startOffset = root.getElement(line - 1).getStartOffset() + (columnStart - 1);
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
