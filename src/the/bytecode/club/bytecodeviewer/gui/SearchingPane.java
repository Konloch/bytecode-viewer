package the.bytecode.club.bytecodeviewer.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.*;
import the.bytecode.club.bytecodeviewer.searching.*;
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
 * A pane dedicating to searching the loaded files.
 *
 * @author Konloch
 * @author WaterWolf
 */

@SuppressWarnings("rawtypes")
public class SearchingPane extends VisibleComponent
{

    private static final long serialVersionUID = -1098524689236993932L;

    FileChangeNotifier fcn;

    JCheckBox exact = new JCheckBox("Exact");
    DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("Results");
    JTree tree;
    JComboBox typeBox;

    SearchType searchType = null;
    JComboBox searchRadiusBox;

    public JButton search = new JButton("Search");
    BackgroundSearchThread t = new BackgroundSearchThread(true)
    {
        @Override
        public void doSearch()
        {
            // empty
        }

    };

    @SuppressWarnings("unchecked")
    public SearchingPane(final FileChangeNotifier fcn)
    {
        super("Search");

        this.fcn = fcn;

        final JPanel optionPanel = new JPanel(new BorderLayout());

        final JPanel searchRadiusOpt = new JPanel(new BorderLayout());

        final JPanel searchOpts = new JPanel(new GridLayout(2, 1));

        searchRadiusOpt.add(new JLabel("Search from "), BorderLayout.WEST);

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (final SearchRadius st : SearchRadius.values())
        {
            model.addElement(st);
        }

        searchRadiusBox = new JComboBox(model);

        searchRadiusOpt.add(searchRadiusBox, BorderLayout.CENTER);

        searchOpts.add(searchRadiusOpt);

        model = new DefaultComboBoxModel();
        for (final SearchType st : SearchType.values())
        {
            model.addElement(st);
        }

        typeBox = new JComboBox(model);
        final JPanel searchOptPanel = new JPanel();

        final ItemListener il = new ItemListener()
        {
            @Override
            public void itemStateChanged(final ItemEvent arg0)
            {
                searchOptPanel.removeAll();
                searchType = (SearchType) typeBox.getSelectedItem();
                searchOptPanel.add(searchType.details.getPanel());

                searchOptPanel.revalidate();
                searchOptPanel.repaint();
            }
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

        search.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent arg0)
            {
                search();
            }
        });

        optionPanel.add(search, BorderLayout.SOUTH);

        this.tree = new JTree(treeRoot);

        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(new JScrollPane(optionPanel), BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(tree), BorderLayout.CENTER);

        this.tree.addTreeSelectionListener(new TreeSelectionListener()
        {
            @Override
            public void valueChanged(final TreeSelectionEvent arg0)
            {
                if(arg0.getPath().getPathComponent(0).equals("Results"))
                    return;

                String cheapHax = arg0.getPath().getPathComponent(1).toString();

                String path = arg0.getPath().getPathComponent(1).toString();

                String containerName = path.split(">",2)[0];
                String className = path.split(">",2)[1].split("\\.")[0];
                FileContainer container = BytecodeViewer.getFileContainer(containerName);

                final ClassNode fN = container.getClassNode(className);

                if (fN != null)
                {
                    MainViewerGUI.getComponent(FileNavigationPane.class).openClassFileToWorkSpace(container, className + ".class", fN);
                }
            }
        });

        this.setVisible(true);

    }

    public void search()
    {
        treeRoot.removeAllChildren();
        searchType = (SearchType) typeBox.getSelectedItem();
        final SearchRadius radius = (SearchRadius) searchRadiusBox
                .getSelectedItem();
        final SearchResultNotifier srn = new SearchResultNotifier() {
            @Override
            public void notifyOfResult(String debug) {
                treeRoot.add(new DefaultMutableTreeNode(debug));
            }
        };
        if (radius == SearchRadius.All_Classes) {
            if (t.finished) {
                t = new BackgroundSearchThread() {
                    @Override
                    public void doSearch() {

                        try {
                            Pattern.compile(RegexInsnFinder.processRegex(RegexSearch.searchText.getText()), Pattern.MULTILINE);
                        } catch (PatternSyntaxException ex) {
                            BytecodeViewer.showMessage("You have an error in your regex syntax.");
                        }

                        for (FileContainer container : BytecodeViewer.files)
                            for (ClassNode c : container.classes)
                                searchType.details.search(container, c, srn, exact.isSelected());

                        MainViewerGUI.getComponent(SearchingPane.class).search.setEnabled(true);
                        MainViewerGUI.getComponent(SearchingPane.class).search.setText("Search");
                        tree.expandPath(new TreePath(tree.getModel().getRoot()));
                        tree.updateUI();
                    }

                };
                MainViewerGUI.getComponent(SearchingPane.class).search
                        .setEnabled(false);
                MainViewerGUI.getComponent(SearchingPane.class).search
                        .setText("Searching, please wait..");
                t.start();
            } else { // this should really never be called.
                BytecodeViewer
                        .showMessage("You currently have a search performing in the background, please wait for that to finish.");
            }
        } else if (radius == SearchRadius.Current_Class) {
            final Viewer cv = MainViewerGUI.getComponent(WorkPane.class).getCurrentViewer();
            if (cv != null) {
                searchType.details.search(cv.container, cv.cn, srn, exact.isSelected());
            }
        }
    }

    public enum SearchType {
        Strings(new LDCSearch()),
        Regex(new RegexSearch()),
        MethodCall(new MethodCallSearch()),
        FieldCall(new FieldCallSearch());

        public final SearchTypeDetails details;

        SearchType(final SearchTypeDetails details) {
            this.details = details;
        }
    }

    public enum SearchRadius {
        All_Classes, Current_Class;
    }

    public void resetWorkspace() {
        treeRoot.removeAllChildren();
        tree.updateUI();
    }

    @Override
    public void openFile(final FileContainer container, String name, byte[] contents) {
    }
}
