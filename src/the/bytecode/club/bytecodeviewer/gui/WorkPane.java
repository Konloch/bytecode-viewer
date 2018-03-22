package the.bytecode.club.bytecodeviewer.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.FileChangeNotifier;

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
 * The pane that contains all of the classes as tabs.
 * 
 * @author Konloch
 * @author WaterWolf
 * 
 */

public class WorkPane extends VisibleComponent implements ActionListener {

	private static final long serialVersionUID = 6542337997679487946L;

	FileChangeNotifier fcn;
	public JTabbedPane tabs;

	JPanel buttonPanel;
	JButton refreshClass;

	HashMap<String, Integer> workingOn = new HashMap<String, Integer>();

	public static int SyntaxFontHeight = 12;

	public WorkPane(final FileChangeNotifier fcn) {
		super("WorkPanel");
		setTitle("Work Space");

		this.tabs = new JTabbedPane();
		this.fcn = fcn;

		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(tabs, BorderLayout.CENTER);

		buttonPanel = new JPanel(new FlowLayout());

		refreshClass = new JButton("Refresh");
		refreshClass.addActionListener(this);

		buttonPanel.add(refreshClass);

		buttonPanel.setVisible(false);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		tabs.addContainerListener(new ContainerListener() {

			@Override
			public void componentAdded(final ContainerEvent e) {
			}

			@Override
			public void componentRemoved(final ContainerEvent e) {
				final Component c = e.getChild();
				if (c instanceof ClassViewer) {
					workingOn.remove(((ClassViewer) c).name);
				}
				if (c instanceof FileViewer) {
					workingOn.remove(((FileViewer) c).name);
				}
			}

		});
		tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent arg0) {
				buttonPanel.setVisible(tabs.getSelectedIndex() != -1);
			}
		});

		this.setVisible(true);

	}

	int tabCount = 0;

	public void addWorkingFile(final String name, final ClassNode cn) {
		if (!workingOn.containsKey(name)) {
			final JPanel tabComp = new ClassViewer(name, cn);
			tabs.add(tabComp);
			final int tabCount = tabs.indexOfComponent(tabComp);
			workingOn.put(name, tabCount);
			tabs.setTabComponentAt(tabCount, new TabbedPane(name,tabs));
			tabs.setSelectedIndex(tabCount);
		} else {
			tabs.setSelectedIndex(workingOn.get(name));
		}
	}
	
	public void addFile(final String name, byte[] contents) {
		if(contents == null) //a directory
			return;
		
		if (!workingOn.containsKey(name)) {
			final Component tabComp = new FileViewer(name, contents);
			tabs.add(tabComp);
			final int tabCount = tabs.indexOfComponent(tabComp);
			workingOn.put(name, tabCount);
			tabs.setTabComponentAt(tabCount, new TabbedPane(name,tabs));
			tabs.setSelectedIndex(tabCount);
		} else {
			tabs.setSelectedIndex(workingOn.get(name));
		}
	}

	@Override
	public void openClassFile(final String name, final ClassNode cn) {
		addWorkingFile(name, cn);
	}

	@Override
	public void openFile(final String name, byte[] content) {
		addFile(name, content);
	}

	public Viewer getCurrentViewer() {
		return (Viewer) tabs.getSelectedComponent();
	}

	public java.awt.Component[] getLoadedViewers() {
		return (java.awt.Component[])tabs.getComponents();
	}
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {
		Thread t = new Thread() {
			public void run() {
				if(BytecodeViewer.viewer.autoCompileOnRefresh.isSelected())
					try {
						if(!BytecodeViewer.compile(false))
							return;
					} catch(java.lang.NullPointerException e) {
						
					}
				final JButton src = (JButton) arg0.getSource();
				if (src == refreshClass) {
					final Component tabComp = tabs.getSelectedComponent();
					if (tabComp != null) {
						if(tabComp instanceof ClassViewer) {
							src.setEnabled(false);
							BytecodeViewer.viewer.setIcon(true);
							((ClassViewer) tabComp).startPaneUpdater(src);
							BytecodeViewer.viewer.setIcon(false);
						} else if(tabComp instanceof FileViewer) {
							src.setEnabled(false);
							BytecodeViewer.viewer.setIcon(true);
							((FileViewer) tabComp).refresh(src);
							BytecodeViewer.viewer.setIcon(false);
						}
					}
				}
			}
		};
		t.start();
	}

	public void resetWorkspace() {
		tabs.removeAll();
		tabs.updateUI();
	}
}
