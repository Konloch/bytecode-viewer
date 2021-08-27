package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.resources.IconResources;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

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
 * A simple code sequence diagram.
 *
 * @author Konloch
 */

public class CodeSequenceDiagram extends Plugin
{
    public static void open()
    {
        PluginManager.runPlugin(new CodeSequenceDiagram());
    }
    
    @Override
    public void execute(List<ClassNode> classNodeList)
    {
        if (!BytecodeViewer.isActiveResourceClass())
        {
            BytecodeViewer.showMessage(TranslatedStrings.FIRST_VIEW_A_CLASS.toString());
            return;
        }
        
        ClassNode c = BytecodeViewer.getCurrentlyOpenedClassNode();
        JFrame frame = new JFrame("Code Sequence Diagram - " + c.name);

        frame.setIconImages(IconResources.iconList);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 320);
        mxGraph graph = new mxGraph();
        graph.setVertexLabelsMovable(false);
        graph.setGridEnabled(true);
        graph.setEnabled(false);
        graph.setCellsEditable(false);
        graph.setCellsSelectable(false);
        graph.setCellsMovable(false);
        graph.setCellsLocked(true);
        Object parent = graph.getDefaultParent();
        Font font = UIManager.getDefaults().getFont("TabbedPane.font");
        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform, true, true);

        graph.getModel().beginUpdate();
        try
        {
            int testX = 10;
            int testY = 0;
            double magicNumber = 5.8;

            for (MethodNode m : c.methods)
            {
                String mIdentifier = c.name + "." + m.name + m.desc;
                Object attach = graph.insertVertex(parent, null, mIdentifier, testX, testY,
                        mIdentifier.length() * magicNumber, 30);
                testX += (int) (font.getStringBounds(mIdentifier, frc).getWidth()) + 60;
                for (AbstractInsnNode i : m.instructions.toArray())
                {
                    if (i instanceof MethodInsnNode)
                    {
                        MethodInsnNode mi = (MethodInsnNode) i;
                        String identifier = mi.owner + "." + mi.name + mi.desc;
                        Object node2 = graph.insertVertex(parent, null, identifier, testX, testY, identifier.length() * 5, 30);
                        testX += (int) (font.getStringBounds(identifier, frc).getWidth()) + 60;
                        graph.insertEdge(parent, null, null, attach, node2);
                        attach = node2;
                    }
                }
                
                testY += 60;
                testX = 10;
            }
        }
        finally
        {
            graph.getModel().endUpdate();
        }

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        frame.getContentPane().add(graphComponent);
        frame.setVisible(true);
    }
}
