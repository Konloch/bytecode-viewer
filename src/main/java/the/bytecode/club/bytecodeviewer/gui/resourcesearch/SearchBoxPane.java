package the.bytecode.club.bytecodeviewer.gui.resourcesearch;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.contextmenu.ContextMenu;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;
import the.bytecode.club.bytecodeviewer.searching.BackgroundSearchThread;
import the.bytecode.club.bytecodeviewer.searching.LDCSearchTreeNodeResult;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedDefaultMutableTreeNode;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJButton;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJCheckBox;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJLabel;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedVisibleComponent;

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
 * A pane dedicating to searching the loaded files.
 *
 * @author Konloch
 * @author WaterWolf
 * @since 09/29/2011
 */
public class SearchBoxPane extends TranslatedVisibleComponent
{
    public static final SearchRadius[] SEARCH_RADII = SearchRadius.values();
    public static final SearchType[] SEARCH_TYPES = SearchType.values();

    public final JCheckBox exact = new TranslatedJCheckBox("Exact", TranslatedComponents.EXACT);
    public final TranslatedDefaultMutableTreeNode treeRoot = new TranslatedDefaultMutableTreeNode("Results", TranslatedComponents.RESULTS);
    public final JTree tree;
    public final JComboBox<SearchType> typeBox;
    
    public SearchType searchType = null;
    public final JComboBox<SearchRadius> searchRadiusBox;
    public final JPopupMenu rightClickMenu = new JPopupMenu();

    public JButton search = new TranslatedJButton("Search", TranslatedComponents.SEARCH);
    public BackgroundSearchThread performSearchThread;

    public SearchBoxPane()
    {
        super("Search", TranslatedComponents.SEARCH);

        final JPanel optionPanel = new JPanel(new BorderLayout());
        final JPanel searchRadiusOpt = new JPanel(new BorderLayout());
        final JPanel searchOpts = new JPanel(new GridLayout(2, 1));

        searchRadiusOpt.add(new TranslatedJLabel("Search from ", TranslatedComponents.SEARCH_FROM), BorderLayout.WEST);

        DefaultComboBoxModel<SearchRadius> radiusModel = new DefaultComboBoxModel<>();
        
        for (final SearchRadius st : SEARCH_RADII)
            radiusModel.addElement(st);

        searchRadiusBox = new JComboBox<>(radiusModel);
        searchRadiusOpt.add(searchRadiusBox, BorderLayout.CENTER);
        searchOpts.add(searchRadiusOpt);

        DefaultComboBoxModel<SearchType> typeModel = new DefaultComboBoxModel<>();
        for (final SearchType st : SEARCH_TYPES)
            typeModel.addElement(st);

        typeBox = new JComboBox<>(typeModel);
        final JPanel searchOptPanel = new JPanel(new BorderLayout());
        searchOptPanel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        final ItemListener il = arg0 -> {
            searchOptPanel.removeAll();
            searchType = (SearchType) typeBox.getSelectedItem();
            searchOptPanel.add(Objects.requireNonNull(searchType).panel.getPanel(), BorderLayout.CENTER);

            searchOptPanel.revalidate();
            searchOptPanel.repaint();
        };

        typeBox.addItemListener(il);

        typeBox.setSelectedItem(SearchType.Strings);
        il.itemStateChanged(null);

        searchOpts.add(typeBox);

        optionPanel.add(searchOpts, BorderLayout.NORTH);

        JPanel sharedPanel = new JPanel();
        sharedPanel.setLayout(new BorderLayout());
        sharedPanel.add(searchOptPanel, BorderLayout.NORTH);
        sharedPanel.add(exact, BorderLayout.SOUTH);

        optionPanel.add(sharedPanel, BorderLayout.CENTER);

        search.addActionListener(arg0 -> search());

        optionPanel.add(search, BorderLayout.SOUTH);

        this.tree = new JTree(treeRoot);
        treeRoot.setTree((DefaultTreeModel) tree.getModel());

        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(optionPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(tree), BorderLayout.CENTER);
        
        tree.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                //TODO right-click context menu
                if (e.isMetaDown())
                {
                    TreePath selPath = SearchBoxPane.this.tree.getClosestPathForLocation(e.getX(), e.getY());
    
                    if (selPath == null)
                        return;
                    
                    //select the closest path
                    SearchBoxPane.this.tree.clearSelection();
                    SearchBoxPane.this.tree.addSelectionPath(selPath);
                    
                    if(!(tree.getLastSelectedPathComponent() instanceof LDCSearchTreeNodeResult))
                        return;
                    
                    //get selected path
                    LDCSearchTreeNodeResult result = (LDCSearchTreeNodeResult) tree.getLastSelectedPathComponent();
    
                    showContextMenu(result, e.getX(), e.getY());
                }
                else if (e.getButton() == MouseEvent.BUTTON1)
                {
                    if(!(tree.getLastSelectedPathComponent() instanceof  LDCSearchTreeNodeResult))
                        return;
                    
                    LDCSearchTreeNodeResult result = (LDCSearchTreeNodeResult) tree.getLastSelectedPathComponent();
    
                    final String name = result.resourceWorkingName;
    
                    BytecodeViewer.viewer.workPane.addClassResource(result.container, name);
                }
            }
        });

        this.setVisible(true);
    }
    
    public void resetWorkspace()
    {
        treeRoot.removeAllChildren();
        tree.updateUI();
    }

    public void search()
    {
        treeRoot.removeAllChildren();
        searchType = (SearchType) typeBox.getSelectedItem();
        final SearchRadius radius = (SearchRadius) searchRadiusBox.getSelectedItem();
        
        if (radius == SearchRadius.All_Classes)
        {
            if (performSearchThread == null || performSearchThread.finished)
            {
                BytecodeViewer.viewer.searchBoxPane.search.setEnabled(false);
                BytecodeViewer.viewer.searchBoxPane.search.setText("Searching, please wait..");
                
                performSearchThread = new PerformSearch(this);
                performSearchThread.start();
            }
            else
            { // this should really never be called.
                BytecodeViewer.showMessage("You currently have a search performing in the background, please wait for that to finish.");
            }
        }
        else if (radius == SearchRadius.Current_Class)
        {
            final ResourceViewer cv = BytecodeViewer.getActiveResource();
            
            if (cv != null)
                searchType.panel.search(cv.resource.container, cv.resource.workingName, cv.resource.getResourceClassNode(), exact.isSelected());
        }
    }
    
    private void showContextMenu(LDCSearchTreeNodeResult selectedNode, int x, int y)
    {
        if (selectedNode == null)
            return;
        
        ContextMenu.buildMenu(null, null, selectedNode, rightClickMenu);
        rightClickMenu.show(this.tree, x, y);
    }
    
    /**
     * Opens and decompiles the LDCSearchTreeNodeResult in a new tab
     */
    public void quickDecompile(Decompiler decompiler, LDCSearchTreeNodeResult result,  boolean quickEdit)
    {
        Decompiler tempDecompiler1 = BytecodeViewer.viewer.viewPane1.getSelectedDecompiler();
        boolean editable1 = BytecodeViewer.viewer.viewPane1.isPaneEditable();
        Decompiler tempDecompiler2 = BytecodeViewer.viewer.viewPane2.getSelectedDecompiler();
        boolean editable2 = BytecodeViewer.viewer.viewPane2.isPaneEditable();
        Decompiler tempDecompiler3 = BytecodeViewer.viewer.viewPane3.getSelectedDecompiler();
        boolean editable3 = BytecodeViewer.viewer.viewPane3.isPaneEditable();
        
        BytecodeViewer.viewer.viewPane1.setSelectedDecompiler(decompiler);
        BytecodeViewer.viewer.viewPane1.setPaneEditable(quickEdit);
        BytecodeViewer.viewer.viewPane2.setSelectedDecompiler(Decompiler.NONE);
        BytecodeViewer.viewer.viewPane2.setPaneEditable(false);
        BytecodeViewer.viewer.viewPane3.setSelectedDecompiler(Decompiler.NONE);
        BytecodeViewer.viewer.viewPane3.setPaneEditable(false);
    
        BytecodeViewer.viewer.workPane.addClassResource(result.container, result.resourceWorkingName);
        
        BytecodeViewer.viewer.viewPane1.setSelectedDecompiler(tempDecompiler1);
        BytecodeViewer.viewer.viewPane1.setPaneEditable(editable1);
        BytecodeViewer.viewer.viewPane2.setSelectedDecompiler(tempDecompiler2);
        BytecodeViewer.viewer.viewPane2.setPaneEditable(editable2);
        BytecodeViewer.viewer.viewPane3.setSelectedDecompiler(tempDecompiler3);
        BytecodeViewer.viewer.viewPane3.setPaneEditable(editable3);
    }
    
    private static final long serialVersionUID = -1098524689236993932L;
}
