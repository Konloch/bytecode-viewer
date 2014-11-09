package the.bytecode.club.bytecodeviewer.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.*;
import the.bytecode.club.bytecodeviewer.searching.*;

/**
 * A pane dedicating to searching the loaded files.
 * 
 * @author Konloch
 * @author WaterWolf
 *
 */

@SuppressWarnings("rawtypes")
public class SearchingPane extends VisibleComponent {
    
	private static final long serialVersionUID = -1098524689236993932L;
    
    FileChangeNotifier fcn;
    
    JCheckBox exact = new JCheckBox("Exact");
    DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("Root");
    JTree tree;
    
    SearchType searchType = null;
	JComboBox searchRadiusBox;
    
    public JButton search = new JButton("Search");
	BackgroundSearchThread t = new BackgroundSearchThread(true) {
		@Override
		public void doSearch() {
			// empty
		}
		
	};
    
	@SuppressWarnings("unchecked")
	public SearchingPane(final FileChangeNotifier fcn) {
        super("Search");
        
        this.fcn = fcn;
        
        final JPanel optionPanel = new JPanel(new BorderLayout());
        
        final JPanel searchRadiusOpt = new JPanel(new BorderLayout());
        
        final JPanel searchOpts = new JPanel(new GridLayout(2, 1));
        
        searchRadiusOpt.add(new JLabel("Search from "), BorderLayout.WEST);
        
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (final SearchRadius st : SearchRadius.values()) {
            model.addElement(st);
        }
        
        searchRadiusBox = new JComboBox(model);
        
        searchRadiusOpt.add(searchRadiusBox, BorderLayout.CENTER);
        
        searchOpts.add(searchRadiusOpt);
        
        model = new DefaultComboBoxModel();
        for (final SearchType st : SearchType.values()) {
            model.addElement(st);
        }
        
        final JComboBox typeBox = new JComboBox(model);
        final JPanel searchOptPanel = new JPanel();
        
        final ItemListener il = new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent arg0) {
                searchOptPanel.removeAll();
                searchType = (SearchType) typeBox.getSelectedItem();
                searchOptPanel.add(searchType.details.getPanel());

                searchOptPanel.revalidate();
                searchOptPanel.repaint();
            }
        };
        
        typeBox.addItemListener(il);
        
        typeBox.setSelectedItem(SearchType.LDC);
        il.itemStateChanged(null);
        
        searchOpts.add(typeBox);
        
        optionPanel.add(searchOpts, BorderLayout.NORTH);
        
        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        p2.add(searchOptPanel, BorderLayout.NORTH);
        p2.add(exact, BorderLayout.SOUTH);
        
        optionPanel.add(p2, BorderLayout.CENTER);
        
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                treeRoot.removeAllChildren();
                searchType = (SearchType) typeBox.getSelectedItem();
                final SearchRadius radius = (SearchRadius) searchRadiusBox.getSelectedItem();
                final SearchResultNotifier srn = new SearchResultNotifier() {
                    @Override
                    public void notifyOfResult(String debug) {
                        treeRoot.add(new DefaultMutableTreeNode(debug));
                    }
                };
                if (radius == SearchRadius.All_Classes) {
                	if(t.finished) {
                		t = new BackgroundSearchThread() {
							@Override
							public void doSearch() {
								for (ClassNode cln : BytecodeViewer.getLoadedClasses())
						    	    searchType.details.search(cln, srn, exact.isSelected());
						    	
						    	MainViewerGUI.getComponent(SearchingPane.class).search.setEnabled(true);
						    	MainViewerGUI.getComponent(SearchingPane.class).search.setText("Search");
				                tree.expandPath(new TreePath(tree.getModel().getRoot()));
				                tree.updateUI();
							}
	                		
	                	};
				    	MainViewerGUI.getComponent(SearchingPane.class).search.setEnabled(false);
				    	MainViewerGUI.getComponent(SearchingPane.class).search.setText("Searching, please wait..");
	                	t.start();
                	} else { //this should really never be called.
                		BytecodeViewer.showMessage("You currently have a search performing in the background, please wait for that to finish.");
                	}
                }
                else if (radius == SearchRadius.Current_Class) {
                    final ClassViewer cv = MainViewerGUI.getComponent(WorkPane.class).getCurrentClass();
                    if (cv != null) {
                        searchType.details.search(cv.cn, srn, exact.isSelected());
                    }
                }
            }
        });
        
        optionPanel.add(search, BorderLayout.SOUTH);
        
        this.tree = new JTree(treeRoot);
        
        getContentPane().setLayout(new BorderLayout());
        
        getContentPane().add(optionPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(tree), BorderLayout.CENTER);
        
        this.tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(final TreeSelectionEvent arg0) {
                final TreePath path = arg0.getPath();
                if ( ((TreeNode)path.getLastPathComponent()).getChildCount() > 0)
                    return;
                final String clazzName = path.getLastPathComponent().toString();
                final ClassNode fN = BytecodeViewer.getClassNode(clazzName);
                if (fN != null) {
                    MainViewerGUI.getComponent(FileNavigationPane.class).openClassFileToWorkSpace(clazzName, fN);
                }
            }
        });
        
        this.setVisible(true);
        
    }
    
    public enum SearchType {
        LDC (new LDCSearch()),
        Regex (new RegexSearch()),
        MethodCall (new MethodCallSearch()),
        FieldCall (new FieldCallSearch());
        
        public final SearchTypeDetails details;
        
        SearchType(final SearchTypeDetails details) {
            this.details = details;
        }
    }
    
    public enum SearchRadius {
        All_Classes,
        Current_Class;
    }

	public void resetWorkspace() {
		treeRoot.removeAllChildren();
        tree.updateUI();
	}
    
}
