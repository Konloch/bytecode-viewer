package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import the.bytecode.club.bytecodeviewer.gui.components.ButtonHoverAnimation;
import the.bytecode.club.bytecodeviewer.gui.components.MaxWidthJLabel;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;
import the.bytecode.club.bytecodeviewer.gui.util.DelayTabbedPaneThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

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
 * Component to be used as tabComponent; Contains a JLabel to show the text and a JButton to close the tab it belongs to
 *
 * @author Konloch
 * @author WaterWolf
 * @since 09/26/2011
 */
public class TabbedPane extends JPanel {

	public final JTabbedPane tabs;
	public final JLabel label;
	private DelayTabbedPaneThread probablyABadIdea;
	private long startedDragging = 0;
	public final String tabName;
	public final String fileContainerName;
	public final ResourceViewer resource;
	private static long lastMouseClick = System.currentTimeMillis();
	public final static MouseListener buttonHoverAnimation = new ButtonHoverAnimation();
	public static final Color BLANK_COLOR = new Color(0, 0, 0, 0);

	public TabbedPane(int tabIndex, String tabWorkingName, String fileContainerName, String name, final JTabbedPane existingTabs, ResourceViewer resource) {
		// unset default FlowLayout' gaps
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));

		this.tabName = name;
		this.fileContainerName = fileContainerName;
		this.resource = resource;

		if (existingTabs == null)
			throw new NullPointerException("TabbedPane is null");

		this.tabs = existingTabs;
		setOpaque(false);

		// make JLabel read titles from JTabbedPane
		label = new MaxWidthJLabel(tabName, 400, 20);

		this.add(label);
		// add more space between the label and the button
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		// add more space to the top of the component
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
	}

	private static final long serialVersionUID = -4774885688297538774L;
}
