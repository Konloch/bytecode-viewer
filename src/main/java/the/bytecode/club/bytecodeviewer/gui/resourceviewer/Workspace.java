package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
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
 * The pane that contains all of the resources as tabs.
 *
 * @author Konloch
 * @author WaterWolf
 * @since 09/26/2011
 */

public class Workspace extends TranslatedVisibleComponent
{
    public final JTabbedPane tabs;
    public final JPanel buttonPanel;
    public final JButton refreshClass;
    public final Set<String> openedTabs = new HashSet<>();

    public Workspace()
    {
        super("Workspace", TranslatedComponents.WORK_SPACE);

        this.tabs = new JTabbedPane();
        
        JPopupMenu popUp = new JPopupMenu();
        JMenuItem closeAllTabs = new JMenuItem("Close All But This");
        JMenuItem closeTab = new JMenuItem("Close Tab");
        closeTab.addActionListener(e ->
        {
            TabExitButton tabExitButton = (TabExitButton) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
            final int index = tabExitButton.getTabIndex();
            
            if (index != -1)
                tabs.remove(index);
        });
        
        closeAllTabs.addActionListener(e ->
        {
            TabExitButton tabExitButton = (TabExitButton) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
            final int index = tabExitButton.getTabIndex();
    
            while (true)
            {
                if (tabs.getTabCount() <= 1)
                    return;
        
                if (index != 0)
                    tabs.remove(0);
                else
                    tabs.remove(1);
            }
        });
        
        tabs.addMouseListener(new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            public void mouseEntered(MouseEvent arg0) { }
            @Override
            public void mouseExited(MouseEvent arg0) { }

            @Override
            public void mousePressed(MouseEvent e)
            {
                if (BLOCK_TAB_MENU)
                    return;
                
                if (e.getButton() == 3)
                {
                    Rectangle bounds = new Rectangle(1, 1, e.getX(), e.getY());
                    
                    for (int i = 0; i < BytecodeViewer.viewer.workPane.tabs.getTabCount(); i++)
                    {
                        Component c = BytecodeViewer.viewer.workPane.tabs.getTabComponentAt(i);
                        if (c != null && bounds.intersects(c.getBounds()))
                        {
                            popUp.setVisible(true);
                            closeAllTabs.setText(TranslatedStrings.CLOSE_TAB + ": " + ((TabbedPane) c).tabName);
                            closeTab.setText(TranslatedStrings.CLOSE_TAB + ": " + ((TabbedPane) c).tabName);
                        }
                        else
                        {
                            popUp.setVisible(false);
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) { }
        });

        popUp.add(closeAllTabs);
        popUp.add(closeTab);
        
        if (!BLOCK_TAB_MENU)
            tabs.setComponentPopupMenu(popUp);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabs, BorderLayout.CENTER);

        buttonPanel = new JPanel(new FlowLayout());

        refreshClass = new TranslatedJButton("Refresh", TranslatedComponents.REFRESH);
        refreshClass.addActionListener((event)->
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
    public void addClassResource(final ResourceContainer container, final String name)
    {
        addResource(container, name, new ClassViewer(container, name));
    }

    //Load file resources
    public void addFileResource(final ResourceContainer container, final String name)
    {
        addResource(container, name, new FileViewer(container, name));
    }
    
    private void addResource(final ResourceContainer container, final String name, final ResourceViewer resourceView)
    {
        // Warn user and prevent 'nothing' from opening if no Decompiler is selected
        if(BytecodeViewer.viewer.viewPane1.getSelectedDecompiler() == Decompiler.NONE &&
            BytecodeViewer.viewer.viewPane2.getSelectedDecompiler() == Decompiler.NONE &&
            BytecodeViewer.viewer.viewPane3.getSelectedDecompiler() == Decompiler.NONE)
        {
            BytecodeViewer.showMessage(TranslatedStrings.SUGGESTED_FIX_NO_DECOMPILER_WARNING.toString());
            return;
        }
        
        //unlock the refresh button
        BytecodeViewer.viewer.workPane.refreshClass.setEnabled(true);
        
        final String workingName = container.getWorkingName(name);
        
        //create a new tab if the resource isn't opened currently
        if (!openedTabs.contains(workingName))
        {
            addResourceToTab(resourceView, workingName, container.name, name);
        }
        else //if the resource is already opened select this tab as the active one
        {
            //TODO openedTabs could be changed to a HashMap<String, Integer> for faster lookups
            
            //search through each tab
            for(int i = 0; i < tabs.getTabCount(); i++)
            {
                //find the matching resource and open it
                ResourceViewer tab = ((TabbedPane)tabs.getTabComponentAt(i)).resource;
                if(tab.resource.workingName.equals(workingName))
                {
                    tabs.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    public void addResourceToTab(ResourceViewer resourceView, String workingName, String containerName, String name)
    {
        //start processing the resource to be viewed
        if(resourceView instanceof ClassViewer)
            resourceView.refresh(null);
    
        //add the resource view to the tabs
        tabs.add(resourceView);
    
        //get the resource view index
        final int tabIndex = tabs.indexOfComponent(resourceView);
    
        //create a new tabbed pane
        TabbedPane tabbedPane = new TabbedPane(tabIndex, workingName, containerName, name, tabs, resourceView);
        resourceView.tabbedPane = tabbedPane;
        resourceView.resource.workingName = workingName;
    
        //set the tabs index
        tabs.setTabComponentAt(tabIndex, tabbedPane);
    
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

    public void resetWorkspace()
    {
        tabs.removeAll();
        tabs.updateUI();
    }
    
    private static final long serialVersionUID = 6542337997679487946L;
}
