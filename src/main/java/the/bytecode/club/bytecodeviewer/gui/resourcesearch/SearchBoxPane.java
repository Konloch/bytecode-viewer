package the.bytecode.club.bytecodeviewer.gui.resourcesearch;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemListener;
import java.util.Objects;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.MainViewerGUI;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.WorkPaneMainComponent;
import the.bytecode.club.bytecodeviewer.gui.components.VisibleComponent;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;
import the.bytecode.club.bytecodeviewer.searching.BackgroundSearchThread;
import the.bytecode.club.bytecodeviewer.searching.SearchResultNotifier;
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
 * A pane dedicating to searching the loaded files.
 *
 * @author Konloch
 * @author WaterWolf
 */

@SuppressWarnings("rawtypes")
public class SearchBoxPane extends VisibleComponent
{
    private static final long serialVersionUID = -1098524689236993932L;
    public static final SearchRadius[] SEARCH_RADII = SearchRadius.values();
    public static final SearchType[] SEARCH_TYPES = SearchType.values();

    public final JCheckBox exact = new JCheckBox("Exact");
    public final DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("Results");
    public final JTree tree;
    public final JComboBox typeBox;
    
    public SearchType searchType = null;
    public final JComboBox searchRadiusBox;

    public JButton search = new JButton("Search");
    BackgroundSearchThread t = new BackgroundSearchThread(true) {
        @Override
        public void doSearch() {
            // empty
        }
    };

    @SuppressWarnings("unchecked")
    public SearchBoxPane() {
        super("Search");

        final JPanel optionPanel = new JPanel(new BorderLayout());
        final JPanel searchRadiusOpt = new JPanel(new BorderLayout());
        final JPanel searchOpts = new JPanel(new GridLayout(2, 1));

        searchRadiusOpt.add(new JLabel("Search from "), BorderLayout.WEST);

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (final SearchRadius st : SEARCH_RADII) {
            model.addElement(st);
        }

        searchRadiusBox = new JComboBox(model);
        searchRadiusOpt.add(searchRadiusBox, BorderLayout.CENTER);
        searchOpts.add(searchRadiusOpt);

        model = new DefaultComboBoxModel();
        for (final SearchType st : SEARCH_TYPES)
            model.addElement(st);

        typeBox = new JComboBox(model);
        final JPanel searchOptPanel = new JPanel();

        final ItemListener il = arg0 -> {
            searchOptPanel.removeAll();
            searchType = (SearchType) typeBox.getSelectedItem();
            searchOptPanel.add(Objects.requireNonNull(searchType).details.getPanel());

            searchOptPanel.revalidate();
            searchOptPanel.repaint();
        };

        typeBox.addItemListener(il);

        typeBox.setSelectedItem(SearchType.Strings);
        il.itemStateChanged(null);

        searchOpts.add(typeBox);

        optionPanel.add(searchOpts, BorderLayout.NORTH);

        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        p2.add(searchOptPanel, BorderLayout.NORTH);
        p2.add(exact, BorderLayout.SOUTH);

        optionPanel.add(p2, BorderLayout.CENTER);

        search.addActionListener(arg0 -> search());

        optionPanel.add(search, BorderLayout.SOUTH);

        this.tree = new JTree(treeRoot);

        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(new JScrollPane(optionPanel), BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(tree), BorderLayout.CENTER);

        this.tree.addTreeSelectionListener(arg0 -> {
            if (arg0.getPath().getPathComponent(0).equals("Results"))
                return;

            arg0.getPath().getPathComponent(1);

            String path = arg0.getPath().getPathComponent(1).toString();

            String containerName = path.split(">", 2)[0];
            String className = path.split(">", 2)[1].split("\\.")[0];
            FileContainer container = BytecodeViewer.getFileContainer(containerName);

            final ClassNode fN = Objects.requireNonNull(container).getClassNode(className);

            if (fN != null) {
                BytecodeViewer.viewer.openClassFile(container, className + ".class", fN);
            }
        });

        this.setVisible(true);

    }

    public void search() {
        treeRoot.removeAllChildren();
        searchType = (SearchType) typeBox.getSelectedItem();
        final SearchRadius radius = (SearchRadius) searchRadiusBox
                .getSelectedItem();
        final SearchResultNotifier srn = debug -> treeRoot.add(new DefaultMutableTreeNode(debug));
        if (radius == SearchRadius.All_Classes) {
            if (t.finished) {
                t = new PerformSearch(this, srn);
                Objects.requireNonNull(MainViewerGUI.getComponent(SearchBoxPane.class))
                        .search.setEnabled(false);
                Objects.requireNonNull(MainViewerGUI.getComponent(SearchBoxPane.class))
                        .search.setText("Searching, please wait..");
                t.start();
            } else { // this should really never be called.
                BytecodeViewer.showMessage("You currently have a search performing in the background, please wait for that to finish.");
            }
        } else if (radius == SearchRadius.Current_Class) {
            final ResourceViewer cv = Objects.requireNonNull(MainViewerGUI.getComponent(WorkPaneMainComponent.class)).getCurrentViewer();
            if (cv != null) {
                searchType.details.search(cv.container, cv.cn, srn, exact.isSelected());
            }
        }
    }
    
    public void resetWorkspace() {
        treeRoot.removeAllChildren();
        tree.updateUI();
    }
    
}
