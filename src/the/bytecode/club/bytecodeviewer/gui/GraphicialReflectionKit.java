package the.bytecode.club.bytecodeviewer.gui;

import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import the.bytecode.club.bytecodeviewer.Resources;

/**
 * A graphical way to execute reflection.
 * 
 * @author Konloch
 *
 */

public class GraphicialReflectionKit extends JFrame {
	public GraphicialReflectionKit() {
		this.setIconImages(Resources.iconList);
		setSize(new Dimension(382, 356));
		setTitle("Graphicial Reflection Kit");

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Invoke Method", null, panel, null);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Get Field Value", null, panel_1, null);

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Cast Field", null, panel_2, null);
	}

	private static final long serialVersionUID = 6728356108271228236L;

}
