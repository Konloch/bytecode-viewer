package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

import com.github.weisj.darklaf.components.CloseButton;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.components.ButtonHoverAnimation;
import the.bytecode.club.bytecodeviewer.gui.components.MaxWidthJLabel;
import the.bytecode.club.bytecodeviewer.gui.components.listeners.MouseClickedListener;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;
import the.bytecode.club.bytecodeviewer.gui.util.DelayTabbedPaneThread;
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

	public TabbedPane(int tabIndex, String tabWorkingName, String fileContainerName, String name, final DraggableTabbedPane existingTabs, ResourceViewer resource) {
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

    public void onMousePressed(MouseEvent e)
    {
        BytecodeViewer.viewer.workPane.tabs.dispatchEvent(e);
    
        if(e.getButton() == 1)
        {
            startedDragging = System.currentTimeMillis();
            //dragging = true;
            if (probablyABadIdea != null)
                probablyABadIdea.stopped = true;
            
            probablyABadIdea = new DelayTabbedPaneThread(TabbedPane.this);
            probablyABadIdea.start();
            repaint();
            Rectangle bounds = new Rectangle(e.getX(), e.getY(), e.getX() + this.getX(), e.getY());
            for(int i = 0; i < BytecodeViewer.viewer.workPane.tabs.getTabCount(); i++)
            {
                Component c = BytecodeViewer.viewer.workPane.tabs.getTabComponentAt(i);
                if(c != null && bounds.intersects(c.getBounds()))
                    BytecodeViewer.viewer.workPane.tabs.setSelectedIndex(i);
            }
        }
    }

	private static final long serialVersionUID = -4774885688297538774L;

	/*public int getTabIndex() {
		return tabs.indexOfTabComponent(this);
	}*/
}
