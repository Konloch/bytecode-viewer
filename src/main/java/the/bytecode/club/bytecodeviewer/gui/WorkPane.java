package the.bytecode.club.bytecodeviewer.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.util.FileChangeNotifier;
import the.bytecode.club.bytecodeviewer.util.FileContainer;

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


		JPopupMenu pop_up = new JPopupMenu()
		{
			@Override
			public void setVisible(boolean b) {
				super.setVisible(b);
			}
		};
		JMenuItem closealltab = new JMenuItem("Close All But This");
		JMenuItem closetab = new JMenuItem("Close Tab");
		closetab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/*String name = e.getActionCommand().split(": ")[1];
				final int i = pane.indexOfTab(name);
				if (i != -1)
					pane.remove(i);*/
			}
		});
		closealltab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = e.getActionCommand().split(": ")[1];
				System.out.println("debug-3: "+name);
				boolean removedAll = false;
				while (!removedAll) {
					int thisID = tabs.indexOfTab(name);
					if (tabs.getTabCount() <= 1) {
						removedAll = true;
						return;
					}
					if (thisID != 0)
						tabs.remove(0);
					else
						tabs.remove(1);
				}
			}
		});
		tabs.addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent e) {}
			@Override public void mouseEntered(MouseEvent arg0) {
			}
			@Override public void mouseExited(MouseEvent arg0) {
			}
			@Override public void mousePressed(MouseEvent e) {
				if(e.getButton() == 3)
				{
					if(BytecodeViewer.BLOCK_TAB_MENU)
						return;

					Rectangle bounds = new Rectangle(1, 1, e.getX(), e.getY());
					Point point = tabs.getMousePosition();
					System.out.println("debug-1: " +point);
					for(int i = 0; i < BytecodeViewer.viewer.workPane.tabs.getTabCount(); i++)
					{
						Component c = BytecodeViewer.viewer.workPane.tabs.getTabComponentAt(i);
						if(c != null && bounds.intersects(c.getBounds()))
						{
							pop_up.setVisible(true);
							closealltab.setText("Close All But This: " + ((TabbedPane)c).tabName);
							closetab.setText("Close Tab: " + ((TabbedPane)c).tabName);
							//do something with this shit
							//BytecodeViewer.viewer.workPane.tabs.setSelectedIndex(i);
						}
						else
						{
							pop_up.setVisible(false);
						}
					}

					System.out.println("debug-2: " +e.getX()+", "+e.getY());
				}
			}
			@Override public void mouseReleased(MouseEvent e) {
			}
		});

		pop_up.add(closealltab);
		pop_up.add(closetab);


		if(!BytecodeViewer.BLOCK_TAB_MENU)
			tabs.setComponentPopupMenu(pop_up);

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
					String containerName = ((ClassViewer) c).container.name+">";
					String fileName = ((ClassViewer) c).name;

					if(fileName.startsWith(containerName))
					{
						workingOn.remove(fileName);
					}
					else
					{
						workingOn.remove(containerName+fileName);
					}
				}
				if (c instanceof FileViewer)
				{
					String containerName = ((FileViewer) c).container.name+">";
					String fileName = ((FileViewer) c).name;

					if(fileName.startsWith(containerName))
					{
						workingOn.remove(fileName);
					}
					else
					{
						workingOn.remove(containerName+fileName);
					}
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

	public void addWorkingFile(final FileContainer container, String name, final ClassNode cn) {
		String workingName = container.name+">"+name;
		String containerName = name;

		if(BytecodeViewer.displayParentInTab)
			containerName = container.name+">"+name;

		if (!workingOn.containsKey(workingName)) {
			final JPanel tabComp = new ClassViewer(container, containerName, cn);
			tabs.add(tabComp);
			final int tabCount = tabs.indexOfComponent(tabComp);
			workingOn.put(workingName, tabCount);
			TabbedPane tabbedPane = new TabbedPane(container.name, name,tabs);
			((ClassViewer) tabComp).tabbedPane = tabbedPane;
			tabs.setTabComponentAt(tabCount, tabbedPane);
			tabs.setSelectedIndex(tabCount);
		} else {
			tabs.setSelectedIndex(workingOn.get(workingName));
		}
	}
	
	public void addFile(final FileContainer container, String name, byte[] contents) {
		String workingName = container.name+">"+name;

		if(BytecodeViewer.displayParentInTab)
			name = container.name+">"+name;

		if(contents == null) //a directory
			return;
		
		if (!workingOn.containsKey(workingName)) {
			final Component tabComp = new FileViewer(container, name, contents);
			tabs.add(tabComp);
			final int tabCount = tabs.indexOfComponent(tabComp);
			workingOn.put(workingName, tabCount);

			TabbedPane tabbedPane = new TabbedPane(null, name,tabs);
			((FileViewer) tabComp).tabbedPane = tabbedPane;
			tabs.setTabComponentAt(tabCount, tabbedPane);
			tabs.setSelectedIndex(tabCount);
		} else {
			try
			{
				tabs.setSelectedIndex(workingOn.get(workingName));
			}
			catch(java.lang.IndexOutOfBoundsException e)
			{
				//workingOn.remove(workingName);
				e.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void openClassFile(final FileContainer container, final String name, final ClassNode cn) {
		addWorkingFile(container, name, cn);
	}

	@Override
	public void openFile(final FileContainer container, final String name, byte[] content) {
		addFile(container, name, content);
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
