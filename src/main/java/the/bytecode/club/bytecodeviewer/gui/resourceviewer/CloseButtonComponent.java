package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import com.android.tools.r8.internal.Cl;
import com.github.weisj.darklaf.components.CloseButton;

import javax.swing.*;
import java.awt.*;

public class CloseButtonComponent extends JPanel {

	private final JTabbedPane pane;

	public CloseButtonComponent(final JTabbedPane pane) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		if (pane == null) {
			throw new NullPointerException("TabbedPane is null");
		}

		this.pane = pane;
		setOpaque(false);
		JLabel label = new JLabel() {
			public String getText() {
				int i = pane.indexOfTabComponent(CloseButtonComponent.this);
				if (i != -1) {
					return pane.getTitleAt(i);
				}

				return null;
			}
		};

		add(label);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		JButton button = new CloseButton();
		add(button);
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
	}

}
