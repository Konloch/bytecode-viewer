package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.FileViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJButton;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedVisibleComponent;
import the.bytecode.club.uikit.tabpopup.closer.JTabbedPanePopupMenuTabsCloser;
import the.bytecode.club.uikit.tabpopup.closer.PopupMenuTabsCloseConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Set;

import static the.bytecode.club.bytecodeviewer.Constants.BLOCK_TAB_MENU;

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
 * This pane contains all the resources, as tabs.
 *
 * @author Konloch
 * @author WaterWolf
 * @since 09/26/2011
 */

public class Workspace extends TranslatedVisibleComponent {

	public JTabbedPane tabs;
	public final JPanel buttonPanel;
	public final JButton refreshClass;
	public final Set<String> openedTabs = new HashSet<>();

	public Workspace() {
		super("Workspace", TranslatedComponents.WORK_SPACE);

		this.tabs = new DraggableTabbedPane();

		// configure popup menu of close tabs
		JTabbedPanePopupMenuTabsCloser popupMenuTabsCloser = new JTabbedPanePopupMenuTabsCloser(this.tabs);
		PopupMenuTabsCloseConfiguration.Builder builder = new PopupMenuTabsCloseConfiguration.Builder();
		popupMenuTabsCloser.configureCloseItems(builder.buildFull());

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabs, BorderLayout.CENTER);

		buttonPanel = new JPanel(new FlowLayout());

		refreshClass = new TranslatedJButton("Refresh", TranslatedComponents.REFRESH);
		refreshClass.addActionListener((event) ->
		{
			refreshClass.setEnabled(false);
			Thread t = new Thread(() -> new WorkspaceRefresh(event).run(), "Refresh");
			t.start();
		});

		buttonPanel.add(refreshClass);
		buttonPanel.setVisible(false);

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		tabs.addContainerListener(new TabRemovalEvent());
		tabs.addChangeListener(arg0 -> buttonPanel.setVisible(tabs.getSelectedIndex() != -1));

		this.setVisible(true);
	}

	//load class resources
	public void addClassResource(final ResourceContainer container, final String name) {
		addResource(container, name, new ClassViewer(container, name));
	}

	//Load file resources
	public void addFileResource(final ResourceContainer container, final String name) {
		addResource(container, name, new FileViewer(container, name));
	}

	private void addResource(final ResourceContainer container, final String name, final ResourceViewer resourceView) {
		// Warn user and prevent 'nothing' from opening if no Decompiler is selected
		if (BytecodeViewer.viewer.viewPane1.getSelectedDecompiler() == Decompiler.NONE &&
				BytecodeViewer.viewer.viewPane2.getSelectedDecompiler() == Decompiler.NONE &&
				BytecodeViewer.viewer.viewPane3.getSelectedDecompiler() == Decompiler.NONE) {
			BytecodeViewer.showMessage(TranslatedStrings.SUGGESTED_FIX_NO_DECOMPILER_WARNING.toString());
			return;
		}

		//unlock the refresh button
		BytecodeViewer.viewer.workPane.refreshClass.setEnabled(true);

		final String workingName = container.getWorkingName(name);

		//create a new tab if the resource isn't opened currently
		if (!openedTabs.contains(workingName)) {
			addResourceToTab(resourceView, workingName, container.name, name);
		} else //if the resource is already opened select this tab as the active one
		{
			//TODO openedTabs could be changed to a HashMap<String, Integer> for faster lookups

			//search through each tab
			for (int i = 0; i < tabs.getTabCount(); i++) {
				//find the matching resource and open it
				ResourceViewer tab = (ResourceViewer) tabs.getComponentAt(i);
				if (tab.resource.workingName.equals(workingName)) {
					tabs.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	public void addResourceToTab(ResourceViewer resourceView, String workingName, String containerName, String name) {
		//start processing the resource to be viewed
		if (resourceView instanceof ClassViewer)
			resourceView.refresh(null);

		//add the resource view to the tabs
		tabs.add(resourceView);

		//get the resource view index
		final int tabIndex = tabs.indexOfComponent(resourceView);

		//create a new tabbed pane
		resourceView.tabbedPane = new TabbedPane(tabIndex, workingName, containerName, name, tabs, resourceView);
		resourceView.resource.workingName = workingName;

		//set the tabs index
		tabs.setTabComponentAt(tabIndex, new CloseButtonComponent(tabs));

		//open the tab that was just added
		tabs.setSelectedIndex(tabIndex);

		//set resource as opened in a tab
		openedTabs.add(workingName);

		//refresh the tab title
		resourceView.refreshTitle();
	}

	public ResourceViewer getActiveResource() {
		return (ResourceViewer) tabs.getSelectedComponent();
	}

	public Component[] getLoadedViewers() {
		return tabs.getComponents();
	}

	public void resetWorkspace() {
		tabs.removeAll();
		tabs.updateUI();
	}

	private static final long serialVersionUID = 6542337997679487946L;
}
