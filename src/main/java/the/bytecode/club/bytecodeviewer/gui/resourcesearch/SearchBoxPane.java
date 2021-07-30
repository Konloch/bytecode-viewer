package the.bytecode.club.bytecodeviewer.gui.resourcesearch;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemListener;
import java.util.Objects;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;
import the.bytecode.club.bytecodeviewer.searching.BackgroundSearchThread;
import the.bytecode.club.bytecodeviewer.searching.LDCSearchTreeNodeResult;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;
import the.bytecode.club.bytecodeviewer.translation.components.*;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;

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
    public final JComboBox searchRadiusBox;

    public JButton search = new TranslatedJButton("Search", TranslatedComponents.SEARCH);
    public BackgroundSearchThread performSearchThread;

    @SuppressWarnings("unchecked")
    public SearchBoxPane()
    {
        super("Search", TranslatedComponents.SEARCH);

        final JPanel optionPanel = new JPanel(new BorderLayout());
        final JPanel searchRadiusOpt = new JPanel(new BorderLayout());
        final JPanel searchOpts = new JPanel(new GridLayout(2, 1));

        searchRadiusOpt.add(new TranslatedJLabel("Search from ", TranslatedComponents.SEARCH_FROM), BorderLayout.WEST);

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        
        for (final SearchRadius st : SEARCH_RADII)
            model.addElement(st);

        searchRadiusBox = new JComboBox(model);
        searchRadiusOpt.add(searchRadiusBox, BorderLayout.CENTER);
        searchOpts.add(searchRadiusOpt);

        model = new DefaultComboBoxModel();
        for (final SearchType st : SEARCH_TYPES)
            model.addElement(st);

        typeBox = new JComboBox<SearchType>(model);
        final JPanel searchOptPanel = new JPanel();

        final ItemListener il = arg0 -> {
            searchOptPanel.removeAll();
            searchType = (SearchType) typeBox.getSelectedItem();
            searchOptPanel.add(Objects.requireNonNull(searchType).panel.getPanel());

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

        this.tree.addTreeSelectionListener(selectionEvent ->
        {
            try
            {
                if (selectionEvent.getPath().getPathComponent(0).equals(TranslatedStrings.RESULTS))
                    return;
    
                LDCSearchTreeNodeResult result = (LDCSearchTreeNodeResult) tree.getLastSelectedPathComponent();
    
                final String name = result.resourceWorkingName;
                
                BytecodeViewer.viewer.workPane.addClassResource(result.container, name);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        this.setVisible(true);

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
    
    public void resetWorkspace()
    {
        treeRoot.removeAllChildren();
        tree.updateUI();
    }
    
    private static final long serialVersionUID = -1098524689236993932L;
}