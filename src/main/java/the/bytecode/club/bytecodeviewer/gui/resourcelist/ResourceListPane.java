package the.bytecode.club.bytecodeviewer.gui.resourcelist;

//TODO fix for Java 9+
import com.sun.java.swing.plaf.windows.WindowsTreeUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.components.VisibleComponent;
import the.bytecode.club.bytecodeviewer.util.FileContainer;
import the.bytecode.club.bytecodeviewer.util.FileDrop;
import the.bytecode.club.bytecodeviewer.util.LazyNameUtil;

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
 * The file navigation pane.
 *
 * @author Konloch
 * @author WaterWolf
 * @author afffsdd
 */

public class ResourceListPane extends VisibleComponent implements FileDrop.Listener
{
    public final JPopupMenu rightClickMenu = new JPopupMenu();
    public final JCheckBox exact = new JCheckBox("Exact");
    public final JButton open = new JButton("+");
    public final JButton close = new JButton("-");
    public final ResourceTreeNode treeRoot = new ResourceTreeNode("Loaded Files:");
    public final ResourceTree tree = new ResourceTree(treeRoot);
    public final String quickSearchText = "Quick file search (no file extension)";
    public final JTextField quickSearch = new JTextField(quickSearchText);
    public final FileDrop fileDrop;
    public boolean cancel = false;

    public final KeyAdapter search = new SearchKeyAdapter(this);

    private void showPopMenu(ResourceTree tree, TreePath selPath, int x, int y) {
        if (selPath == null)
            return;

        rightClickMenu.removeAll();
        
        rightClickMenu.add(new ResourceListRightClickRemove(this, x, y, tree));

        //TODO fix for Java 9+
        //  You can replace this with any Icon loaded from disk,
        //  they could be re-used for the plus and minus symbols on the resource list too
        rightClickMenu.add(new AbstractAction("Expand", WindowsTreeUI.ExpandedIcon.createExpandedIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath selPath = ResourceListPane.this.tree.getPathForLocation(x, y);
                expandAll(tree, Objects.requireNonNull(selPath), true);
            }
        });
        rightClickMenu.add(new AbstractAction("Collapse", WindowsTreeUI.CollapsedIcon.createCollapsedIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath selPath = ResourceListPane.this.tree.getPathForLocation(x, y);
                expandAll(tree, Objects.requireNonNull(selPath), false);
            }
        });

        rightClickMenu.show(this.tree, x, y);
    }

    //used to remove resources from the resource list
    public void removeFile(FileContainer fileContainer)
    {
        BytecodeViewer.files.remove(fileContainer);
        LazyNameUtil.removeName(fileContainer.name);
    }

    public ResourceListPane()
    {
        super("ClassNavigation");
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        quickSearch.setForeground(Color.gray);
        setTitle("Files");

        attachTreeListeners();
        attachQuickSearchListeners();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(tree), BorderLayout.CENTER);
    
        JPanel exactPanel = new JPanel(new BorderLayout());
        JPanel quickSearchPanel = new JPanel();
        JPanel buttonPanel = new JPanel(new BorderLayout());
        
        quickSearchPanel.setLayout(new BorderLayout());
        quickSearchPanel.add(quickSearch, BorderLayout.NORTH);
        exactPanel.add(exact, BorderLayout.WEST);
        buttonPanel.add(open, BorderLayout.EAST);
        buttonPanel.add(close, BorderLayout.WEST);
        exactPanel.add(buttonPanel, BorderLayout.EAST);
        quickSearchPanel.add(exactPanel, BorderLayout.SOUTH);

        getContentPane().add(quickSearchPanel, BorderLayout.SOUTH);

        this.setVisible(true);
        fileDrop = new FileDrop(this, this);
    }

    @Override
    public void filesDropped(final File[] files)
    {
        if (files.length < 1)
            return;
        
        BytecodeViewer.openFiles(files, true);
    }

    public void updateTree()
    {
        try
        {
            treeRoot.removeAllChildren();
            for (FileContainer container : BytecodeViewer.files)
            {
                ResourceTreeNode root = new ResourceTreeNode(container.name);
                treeRoot.add(root);
                
                ImageRenderer renderer = new ImageRenderer();
                tree.setCellRenderer(renderer);

                if (!container.classes.isEmpty())
                {
                    for (ClassNode c : container.classes)
                    {
                        String name = c.name;
                        final String[] spl = name.split("/");
                        if (spl.length < 2)
                        {
                            root.add(new ResourceTreeNode(name + ".class"));
                        }
                        else
                        {
                            ResourceTreeNode parent = root;
                            for (int i1 = 0; i1 < spl.length; i1++)
                            {
                                String s = spl[i1];
                                
                                if (i1 == spl.length - 1)
                                    s += ".class";
                                
                                ResourceTreeNode child = null;
                                for (int i = 0; i < parent.getChildCount(); i++)
                                {
                                    if (((ResourceTreeNode) parent.getChildAt(i)).getUserObject().equals(s))
                                    {
                                        child = (ResourceTreeNode) parent.getChildAt(i);
                                        break;
                                    }
                                }
                                
                                if (child == null)
                                {
                                    child = new ResourceTreeNode(s);
                                    parent.add(child);
                                }
                                
                                parent = child;
                            }
                        }
                    }
                }

                if (!container.files.isEmpty())
                {
                    for (final Entry<String, byte[]> entry : container.files.entrySet())
                    {
                        String name = entry.getKey();
                        final String[] spl = name.split("/");
                        if (spl.length < 2)
                        {
                            root.add(new ResourceTreeNode(name));
                        }
                        else
                        {
                            ResourceTreeNode parent = root;
                            for (final String s : spl)
                            {
                                ResourceTreeNode child = null;
                                for (int i = 0; i < parent.getChildCount(); i++)
                                {
                                    if (((ResourceTreeNode) parent.getChildAt(i)).getUserObject().equals(s))
                                    {
                                        child = (ResourceTreeNode) parent.getChildAt(i);
                                        break;
                                    }
                                }
                                if (child == null)
                                {
                                    child = new ResourceTreeNode(s);
                                    parent.add(child);
                                }
                                parent = child;
                            }
                        }
                    }
                }
            }

            treeRoot.sort();
            tree.expandPath(new TreePath(tree.getModel().getRoot()));
            tree.updateUI();
        } catch (java.util.ConcurrentModificationException e) {
            //ignore, the last file will reset everything
        }
        
        //TODO add a setting for this
        // expandAll(tree, true);
    }

    @SuppressWarnings("rawtypes")
    private void expandAll(final JTree tree, final TreePath parent,
                           final boolean expand) {
        // Traverse children
        final TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (final Enumeration e = node.children(); e.hasMoreElements(); ) {
                final TreeNode n = (TreeNode) e.nextElement();
                final TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }
    
    public void resetWorkspace()
    {
        treeRoot.removeAllChildren();
        tree.repaint();
        tree.updateUI();
    }

    public void openPath(TreePath path)
    {
        if (path == null || path.getPathCount() == 1)
            return;

        final StringBuilder nameBuffer = new StringBuilder();
        for (int i = 2; i < path.getPathCount(); i++)
        {
            nameBuffer.append(path.getPathComponent(i));
            if (i < path.getPathCount() - 1)
                nameBuffer.append("/");
        }

        String cheapHax = path.getPathComponent(1).toString();
        FileContainer container = null;

        for (FileContainer c : BytecodeViewer.files)
        {
            if (c.name.equals(cheapHax))
                container = c;
        }

        String name = nameBuffer.toString();
        if (name.endsWith(".class"))
        {
            final ClassNode cn = BytecodeViewer.getClassNode(Objects.requireNonNull(container),
                    name.substring(0, name.length() - ".class".length()));
            
            if (cn != null)
                BytecodeViewer.viewer.openClassFile(container, nameBuffer.toString(), cn);
            else
                BytecodeViewer.viewer.openFile(container, nameBuffer.toString(),
                        BytecodeViewer.getFileContents(nameBuffer.toString()));
        }
        else
        {
            BytecodeViewer.viewer.openFile(container, nameBuffer.toString(),
                    BytecodeViewer.getFileContents(nameBuffer.toString()));
        }
    }
    
    public void attachTreeListeners()
    {
        tree.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (e.isMetaDown())
                {
                    ResourceTree tree = (ResourceTree) e.getSource();
                    TreePath selPath = ResourceListPane.this.tree.getPathForLocation(e.getX(), e.getY());
                    if (selPath == null)
                        return;
                
                    DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                    Enumeration<?> enumeration = treeRoot.children();
                    while (enumeration.hasMoreElements())
                    {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
                        if (node.isNodeAncestor(selectNode))
                        {
                            //rightClickMenu.show(tree, e.getX(), e.getY());
                            showPopMenu(tree, selPath, e.getX(), e.getY());
                            break;
                        }
                    }
                }
            }
        });
    
        this.open.addActionListener(e -> {
            final TreeNode root = (TreeNode) tree.getModel().getRoot();
            expandAll(tree, new TreePath(root), true);
        });
    
        this.close.addActionListener(e -> {
            final TreeNode root = (TreeNode) tree.getModel().getRoot();
            final TreePath path = new TreePath(root);
            expandAll(tree, path, false);
            tree.expandPath(path);
        });
    
        this.tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                openPath(tree.getPathForLocation(e.getX(), e.getY()));
            }
        });
    
        this.tree.addTreeSelectionListener(arg0 -> {
            if (cancel) {
                cancel = false;
                return;
            }
            
            openPath(arg0.getPath());
        });
    
        this.tree.addKeyListener(new KeyListener()
        {
            @Override
            public void keyReleased(KeyEvent e) { }
        
            @Override
            public void keyTyped(KeyEvent e) { }
        
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (e.getSource() instanceof ResourceTree) {
                        ResourceTree tree = (ResourceTree) e.getSource();
                        openPath(tree.getSelectionPath());
                    }
                } else if ((int) e.getKeyChar() != 0 && (int) e.getKeyChar() != 8 && (int) e.getKeyChar() != 127 && (int) e.getKeyChar() != 65535 && !e.isControlDown() && !e.isAltDown()) {
                    quickSearch.grabFocus();
                    quickSearch.setText("" + e.getKeyChar());
                    cancel = true;
                } else if (e.isControlDown() && (int) e.getKeyChar() == 6) //ctrl + f
                {
                    quickSearch.grabFocus();
                } else {
                    cancel = true;
                }
            }
        });
    }
    
    public void attachQuickSearchListeners()
    {
        quickSearch.addKeyListener(search);
        quickSearch.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(final FocusEvent arg0) {
                if (quickSearch.getText().equals(quickSearchText)) {
                    quickSearch.setText("");
                    quickSearch.setForeground(Color.black);
                }
            }
        
            @Override
            public void focusLost(final FocusEvent arg0) {
                if (quickSearch.getText().isEmpty()) {
                    quickSearch.setText(quickSearchText);
                    quickSearch.setForeground(Color.gray);
                }
            }
        });
    }
    
}