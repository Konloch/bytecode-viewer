package the.bytecode.club.bytecodeviewer.gui.components.actions;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.ClassFileContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.ClassFieldLocation;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.TokenUtil;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.event.ActionEvent;

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
                // Open the class that is associated with the field's owner.
                if (!field.owner.equals(container.getName()))
                {
                    ResourceContainer resourceContainer = BytecodeViewer.getFileContainer(container.getParentContainer());
                    if (resourceContainer != null)
                    {
                        String s = container.getImport(field.owner);
                        BytecodeViewer.viewer.workPane.addClassResource(resourceContainer, s + ".class");
                    }

                    return;
                }

                Element root = textArea.getDocument().getDefaultRootElement();
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
}
