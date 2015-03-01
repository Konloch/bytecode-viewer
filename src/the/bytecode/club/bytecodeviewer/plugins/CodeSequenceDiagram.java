package the.bytecode.club.bytecodeviewer.plugins;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.gui.ClassViewer;

/**
 * A simple code sequence diagram.
 * 
 * @author Konloch
 * 
 */

public class CodeSequenceDiagram extends Plugin {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(ArrayList<ClassNode> classNodeList) {
		if(BytecodeViewer.viewer.workPane.getCurrentViewer() == null || !(BytecodeViewer.viewer.workPane.getCurrentViewer() instanceof ClassViewer)) {
			BytecodeViewer.showMessage("First open a class file.");
			return;
		}
		ClassNode c = BytecodeViewer.viewer.workPane.getCurrentViewer().cn;
		JFrame frame = new JFrame("Code Sequence Diagram - " +c.name);
		frame.setIconImages(Resources.iconList);
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
		FontRenderContext frc = new FontRenderContext(affinetransform,true,true); 

		graph.getModel().beginUpdate();
		try
		{

			int testX = 10;
			int testY = 0;
			double magicNumber = 5.8;

			for(MethodNode m : (ArrayList<MethodNode>)c.methods) {
				String mIdentifier = c.name+"."+m.name+m.desc;
				Object node = graph.insertVertex(parent, null, mIdentifier, testX, testY, mIdentifier.length() * magicNumber, 30);
				Object attach = node;
				testX += (int) (font.getStringBounds(mIdentifier, frc).getWidth()) + 60;
				for (AbstractInsnNode i : m.instructions.toArray()) {
					if (i instanceof MethodInsnNode) {
						MethodInsnNode mi = (MethodInsnNode) i;
						String identifier = mi.owner+"."+mi.name+mi.desc;
						Object node2 = graph.insertVertex(parent, null, identifier, testX, testY, identifier.length() * 5, 30);
						testX += (int) (font.getStringBounds(identifier, frc).getWidth()) + 60;
						graph.insertEdge(parent, null, null, attach, node2);
						attach = node2;
					}
				}
				testY += 60;
				testX = 10;
			}
		} finally {
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		frame.getContentPane().add(graphComponent);
		frame.setVisible(true);
	}

}