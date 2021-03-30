package the.bytecode.club.bytecodeviewer.gui;

import com.sun.java.swing.plaf.windows.WindowsTreeUI;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.util.FileChangeNotifier;
import the.bytecode.club.bytecodeviewer.util.FileContainer;
import the.bytecode.club.bytecodeviewer.util.FileDrop;
import the.bytecode.club.bytecodeviewer.util.LazyNameUtil;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.*;
import java.util.Map.Entry;

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

@SuppressWarnings("serial")
public class FileNavigationPane extends VisibleComponent implements
        FileDrop.Listener {

    FileChangeNotifier fcn;
    JCheckBox exact = new JCheckBox("Exact");
    JButton open = new JButton("+");
    JButton close = new JButton("-");

    MyTreeNode treeRoot = new MyTreeNode("Loaded Files:");
    MyTree tree = new MyTree(treeRoot);
    final String quickSearchText = "Quick file search (no file extension)";
    final JTextField quickSearch = new JTextField(quickSearchText);
    boolean cancel = false;

    public KeyAdapter search = new KeyAdapter() {
        @Override
        public void keyPressed(final KeyEvent ke) {
            if (ke.getKeyCode() == KeyEvent.VK_ENTER) {

                final String qt = quickSearch.getText();
                quickSearch.setText("");

                if(qt.isEmpty()) //NOPE
                    return;


                String[] path = null;
                int found = 0;

                if (qt.contains(".")) {
                    path = qt.split("\\.");
                    String[] path2 = new String[path.length];
                    for (int i = 0; i < path.length; i++) {
                        path2[i] = path[i];
                        if (i + 2 == path.length) {
                            path2[i + 1] = "." + path[i + 1];
                        }
                    }
                } else {
                    path = new String[]{qt};
                }

                MyTreeNode curNode = treeRoot;
                if (exact.isSelected()) {
                    pathLoop:
                    for (int i = 0; i < path.length; i++) {
                        final String pathName = path[i];
                        final boolean isLast = i == path.length - 1;

                        for (int c = 0; c < curNode.getChildCount(); c++) {
                            final MyTreeNode child = (MyTreeNode) curNode.getChildAt(c);
                            System.out.println(pathName + ":" + ((String) child.getUserObject()));

                            if (((String) child.getUserObject()).equals(pathName)) {
                                curNode = child;
                                if (isLast) {
                                    System.out.println("Found! " + curNode);
                                    found++;
                                    if(found >= 30)
                                    {
                                        BytecodeViewer.showMessage("Uh oh, there could be more results but you've triggered the 30 classes at once limit. Try refining your search.");
                                        return;
                                    }
                                    final TreePath pathn = new TreePath(curNode.getPath());
                                    tree.setSelectionPath(pathn);
                                    tree.makeVisible(pathn);
                                    tree.scrollPathToVisible(pathn);
                                    openPath(pathn); //auto open
                                    break pathLoop;
                                }
                                continue pathLoop;
                            }
                        }

                        System.out.println("Could not find " + pathName);
                        break;
                    }
                } else {
                    {
                        @SuppressWarnings("unchecked")
                        Enumeration<MyTreeNode> enums = curNode.depthFirstEnumeration();
                        while (enums != null && enums.hasMoreElements()) {

                            MyTreeNode node = enums.nextElement();
                            if (node.isLeaf()) {
                                if (((String) (node.getUserObject())).toLowerCase().contains(path[path.length - 1].toLowerCase())) {
                                    TreeNode pathArray[] = node.getPath();
                                    int k = 0;
                                    StringBuilder fullPath = new StringBuilder();
                                    while (pathArray != null
                                            && k < pathArray.length) {
                                        MyTreeNode n = (MyTreeNode) pathArray[k];
                                        String s = (String) (n.getUserObject());
                                        fullPath.append(s);
                                        if (k++ != pathArray.length - 1) {
                                            fullPath.append(".");
                                        }
                                    }
                                    String fullPathString = fullPath.toString();
                                    if (fullPathString != null && fullPathString.toLowerCase().contains(qt.toLowerCase())) {
                                        System.out.println("Found! " + node);
                                        found++;
                                        if(found >= 30)
                                        {
                                            BytecodeViewer.showMessage("Uh oh, there could be more results but you've triggered the 30 classes at once limit. Try refining your search.");
                                            return;
                                        }
                                        final TreePath pathn = new TreePath(node.getPath());
                                        tree.setSelectionPath(pathn.getParentPath());
                                        tree.setSelectionPath(pathn);
                                        tree.makeVisible(pathn);
                                        tree.scrollPathToVisible(pathn);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    private void showPopMenu(MyTree tree, TreePath selPath, int x, int y) {
        if (selPath == null) {
            return;
        }

        final JPopupMenu pop = new JPopupMenu();
        pop.add(new AbstractAction("Remove") {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath selPath = FileNavigationPane.this.tree.getPathForLocation(x, y);
                DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                Enumeration enumeration = treeRoot.children();
                while (enumeration.hasMoreElements()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
                    if (node.isNodeAncestor(selectNode)) {
                        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
                        root.remove(node);
                        for (FileContainer fileContainer : BytecodeViewer.files) {
                            if (fileContainer.name.equals(selectNode.toString())) {
                                removeFile(fileContainer);
                                break;
                            }
                        }
                        updateTree();
                        return;
                    }
                }
            }
        });

        pop.add(new AbstractAction("Expand", WindowsTreeUI.ExpandedIcon.createExpandedIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath selPath = FileNavigationPane.this.tree.getPathForLocation(x, y);
                expandAll(tree, selPath, true);
            }
        });

        pop.add(new AbstractAction("Collapse", WindowsTreeUI.CollapsedIcon.createCollapsedIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath selPath = FileNavigationPane.this.tree.getPathForLocation(x, y);
                expandAll(tree, selPath, false);
            }
        });

        pop.show(this.tree, x, y);
    }

    private void removeFile(FileContainer fileContainer) {
        BytecodeViewer.files.remove(fileContainer);
        LazyNameUtil.removeName(fileContainer.name);
    }

    public FileNavigationPane(final FileChangeNotifier fcn) {
        super("ClassNavigation");
        this.fcn = fcn;
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        quickSearch.setForeground(Color.gray);
        setTitle("Files");

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isMetaDown()) {
                    MyTree tree = (MyTree) e.getSource();
                    TreePath selPath = FileNavigationPane.this.tree.getPathForLocation(e.getX(), e.getY());
                    if (selPath == null) {
                        return;
                    }

                    DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                    Enumeration enumeration = treeRoot.children();
                    while (enumeration.hasMoreElements()) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
                        if (node.isNodeAncestor(selectNode)) {
                            // pop.show(tree, e.getX(), e.getY());
                            showPopMenu(tree, selPath, e.getX(), e.getY());
                            break;
                        }
                    }
                }
            }
        });

        this.open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final TreeNode root = (TreeNode) tree.getModel().getRoot();
                expandAll(tree, new TreePath(root), true);
            }
        });

        this.close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final TreeNode root = (TreeNode) tree.getModel().getRoot();
                final TreePath path = new TreePath(root);
                expandAll(tree, path, false);
                tree.expandPath(path);
            }
        });

        this.tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                openPath(tree.getPathForLocation(e.getX(), e.getY()));
            }
        });

        this.tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(final TreeSelectionEvent arg0) {
                if (cancel) {
                    cancel = false;
                    return;
                }
                openPath(arg0.getPath());
            }
        });

        this.tree.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println((int)e.getKeyChar());
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    if (e.getSource() instanceof MyTree)
                    {
                        MyTree tree = (MyTree) e.getSource();
                        openPath(tree.getSelectionPath());
                    }
                }
                else if((int)e.getKeyChar() != 0 &&(int)e.getKeyChar() != 8 &&(int)e.getKeyChar() != 127 && (int)e.getKeyChar() != 65535 && !e.isControlDown() && !e.isAltDown())
                {
                    quickSearch.grabFocus();
                    quickSearch.setText("" + e.getKeyChar());
                    cancel = true;
                }
                else if(e.isControlDown() && (int)e.getKeyChar() == 6) //ctrl + f
                {
                    quickSearch.grabFocus();
                }
                else
                {
                    cancel = true;
                }
            }
        });

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

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(tree), BorderLayout.CENTER);

        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        p2.add(quickSearch, BorderLayout.NORTH);
        JPanel p3 = new JPanel(new BorderLayout());
        p3.add(exact, BorderLayout.WEST);
        JPanel p4 = new JPanel(new BorderLayout());
        p4.add(open, BorderLayout.EAST);
        p4.add(close, BorderLayout.WEST);
        p3.add(p4, BorderLayout.EAST);
        p2.add(p3, BorderLayout.SOUTH);

        getContentPane().add(p2, BorderLayout.SOUTH);

        this.setVisible(true);
        new FileDrop(this, this);
    }

    public void openClassFileToWorkSpace(final FileContainer container, final String name, final ClassNode node) {
        fcn.openClassFile(container, name, node);
    }

    public void openFileToWorkSpace(final FileContainer container, String name, byte[] contents) {
        fcn.openFile(container, name, contents);
    }

    @Override
    public void filesDropped(final File[] files) {
        if (files.length < 1)
            return;
        BytecodeViewer.openFiles(files, true);
    }

    public void updateTree() {
        try {
            treeRoot.removeAllChildren();
            for (FileContainer container : BytecodeViewer.files) {
                MyTreeNode root = new MyTreeNode(container.name);
                treeRoot.add(root);
                ImageRenderer renderer = new ImageRenderer();
                tree.setCellRenderer(renderer);

                if (!container.classes.isEmpty()) {
                    for (ClassNode c : container.classes) {
                        String name = c.name;
                        final String[] spl = name.split("/");
                        if (spl.length < 2) {
                            root.add(new MyTreeNode(name + ".class"));
                        } else {
                            MyTreeNode parent = root;
                            for (int i1 = 0; i1 < spl.length; i1++) {
                                String s = spl[i1];
                                if (i1 == spl.length - 1)
                                    s += ".class";
                                MyTreeNode child = null;
                                for (int i = 0; i < parent.getChildCount(); i++) {
                                    if (((MyTreeNode) parent.getChildAt(i)).getUserObject()
                                            .equals(s)) {
                                        child = (MyTreeNode) parent.getChildAt(i);
                                        break;
                                    }
                                }
                                if (child == null) {
                                    child = new MyTreeNode(s);
                                    parent.add(child);
                                }
                                parent = child;
                            }
                        }
                    }
                }

                if (!container.files.isEmpty()) {
                    for (final Entry<String, byte[]> entry : container.files.entrySet()) {
                        String name = entry.getKey();
                        final String[] spl = name.split("/");
                        if (spl.length < 2) {
                            root.add(new MyTreeNode(name));
                        } else {
                            MyTreeNode parent = root;
                            for (final String s : spl) {
                                MyTreeNode child = null;
                                for (int i = 0; i < parent.getChildCount(); i++) {
                                    if (((MyTreeNode) parent.getChildAt(i)).getUserObject()
                                            .equals(s)) {
                                        child = (MyTreeNode) parent.getChildAt(i);
                                        break;
                                    }
                                }
                                if (child == null) {
                                    child = new MyTreeNode(s);
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

    public class MyTree extends JTree {

        private static final long serialVersionUID = -2355167326094772096L;
        DefaultMutableTreeNode treeRoot;

        public MyTree(final DefaultMutableTreeNode treeRoot) {
            super(treeRoot);
            this.treeRoot = treeRoot;
        }

        StringMetrics m = null;

        @Override
        public void paint(final Graphics g) {
            try {
                super.paint(g);
                if (m == null) {
                    m = new StringMetrics((Graphics2D) g);
                }
                if (treeRoot.getChildCount() < 1) {
                    g.setColor(new Color(0, 0, 0, 100));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.white);
                    String s = "Drag class/jar/zip/APK/DEX here";
                    g.drawString(s,
                            ((int) ((getWidth() / 2) - (m.getWidth(s) / 2))),
                            getHeight() / 2);
                }
            } catch (java.lang.InternalError | java.lang.NullPointerException e) {

            }
        }
    }

    public class MyTreeNode extends DefaultMutableTreeNode {

        private static final long serialVersionUID = -8817777566176729571L;

        public MyTreeNode(final Object o) {
            super(o);
        }

        @Override
        public void insert(final MutableTreeNode newChild, final int childIndex) {
            super.insert(newChild, childIndex);
        }

        public void sort() {
            recursiveSort(this);
        }

        @SuppressWarnings("unchecked")
        private void recursiveSort(final MyTreeNode node) {
            Collections.sort(node.children, nodeComparator);
            final Iterator<MyTreeNode> it = node.children.iterator();
            while (it.hasNext()) {
                final MyTreeNode nextNode = it.next();
                if (nextNode.getChildCount() > 0) {
                    recursiveSort(nextNode);
                }
            }
        }

        protected Comparator<MyTreeNode> nodeComparator = new Comparator<MyTreeNode>() {
            @Override
            public int compare(final MyTreeNode o1, final MyTreeNode o2) {
                // To make sure nodes with children are always on top
                final int firstOffset = o1.getChildCount() > 0 ? -1000 : 0;
                final int secondOffset = o2.getChildCount() > 0 ? 1000 : 0;
                return o1.toString().compareToIgnoreCase(o2.toString())
                        + firstOffset + secondOffset;
            }

            @Override
            public boolean equals(final Object obj) {
                return false;
            }

            @Override
            public int hashCode() {
                final int hash = 7;
                return hash;
            }
        };

    }

    /**
     * @author http://stackoverflow.com/a/18450804
     */
    class StringMetrics {

        Font font;
        FontRenderContext context;

        public StringMetrics(Graphics2D g2) {

            font = g2.getFont();
            context = g2.getFontRenderContext();
        }

        Rectangle2D getBounds(String message) {

            return font.getStringBounds(message, context);
        }

        double getWidth(String message) {

            Rectangle2D bounds = getBounds(message);
            return bounds.getWidth();
        }

        double getHeight(String message) {

            Rectangle2D bounds = getBounds(message);
            return bounds.getHeight();
        }

    }

    public void resetWorkspace() {
        treeRoot.removeAllChildren();
        tree.repaint();
        tree.updateUI();
    }

    public void openPath(TreePath path)
    {
        if (path == null || path.getPathCount() == 1) {
            return;
        }

        final StringBuilder nameBuffer = new StringBuilder();
        for (int i = 2; i < path.getPathCount(); i++) {
            nameBuffer.append(path.getPathComponent(i));
            if (i < path.getPathCount() - 1) {
                nameBuffer.append("/");
            }
        }

        String cheapHax = path.getPathComponent(1).toString();
        FileContainer container = null;

        for(FileContainer c : BytecodeViewer.files)
        {
            if(c.name.equals(cheapHax))
                container = c;
        }

        String name = nameBuffer.toString();
        if (name.endsWith(".class"))
        {

            final ClassNode cn = BytecodeViewer.getClassNode(container, name.substring(0, name.length() - ".class".length()));
            if (cn != null) {
                openClassFileToWorkSpace(container, nameBuffer.toString(), cn);
            }
            else
            {
                openFileToWorkSpace(container, nameBuffer.toString(), BytecodeViewer.getFileContents(nameBuffer.toString()));
            }
        } else {
            openFileToWorkSpace(container, nameBuffer.toString(), BytecodeViewer.getFileContents(nameBuffer.toString()));
        }
    }

    /**
     * @author http://stackoverflow.com/questions/14968005
     * @author Konloch
     */
    public class ImageRenderer extends DefaultTreeCellRenderer {

        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) { //called every time there is a pane update, I.E. whenever you expand a folder

            Component ret = super.getTreeCellRendererComponent(tree, value,
                    selected, expanded, leaf, row, hasFocus);

            if (value != null && value instanceof the.bytecode.club.bytecodeviewer.gui.FileNavigationPane.MyTreeNode) {
                the.bytecode.club.bytecodeviewer.gui.FileNavigationPane.MyTreeNode node = (the.bytecode.club.bytecodeviewer.gui.FileNavigationPane.MyTreeNode) value;
                String name = node.toString().toLowerCase();

                if (name.endsWith(".jar") || name.endsWith(".war")) {
                    setIcon(Resources.jarIcon);
                } else if (name.endsWith(".zip")) {
                    setIcon(Resources.zipIcon);
                } else if (name.endsWith(".bat")) {
                    setIcon(Resources.batIcon);
                } else if (name.endsWith(".sh")) {
                    setIcon(Resources.shIcon);
                } else if (name.endsWith(".cs")) {
                    setIcon(Resources.csharpIcon);
                } else if (name.endsWith(".c") || name.endsWith(".cpp") || name.endsWith(".h")) {
                    setIcon(Resources.cplusplusIcon);
                } else if (name.endsWith(".apk") || name.endsWith(".dex")) {
                    setIcon(Resources.androidIcon);
                } else if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".bmp") || name.endsWith(".gif")) {
                    setIcon(Resources.imageIcon);
                } else if (name.endsWith(".class")) {
                    setIcon(Resources.classIcon);
                } else if (name.endsWith(".java")) {
                    setIcon(Resources.javaIcon);
                } else if (name.endsWith(".txt") || name.endsWith(".md")) {
                    setIcon(Resources.textIcon);
                } else if (name.equals("decoded resources")) {
                    setIcon(Resources.decodedIcon);
                } else if (name.endsWith(".properties") || name.endsWith(".xml") || name.endsWith(".jsp") || name.endsWith(".mf") || name.endsWith(".config") || name.endsWith(".cfg")) {
                    setIcon(Resources.configIcon);
                } else if (node.getChildCount() <= 0) { //random file
                    setIcon(Resources.fileIcon);
                } else { //folder
                    ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();
                    ArrayList<TreeNode> totalNodes = new ArrayList<TreeNode>();

                    nodes.add(node);
                    totalNodes.add(node);

                    boolean isJava = false;
                    boolean finished = false;

                    while (!finished) { //may cause a clusterfuck with huge files
                        if (nodes.isEmpty())
                            finished = true;
                        else {
                            TreeNode treeNode = nodes.get(0);
                            nodes.remove(treeNode);
                            int children = treeNode.getChildCount();
                            if (children >= 1)
                                for (int i = 0; i < children; i++) {
                                    TreeNode child = treeNode.getChildAt(i);

                                    if (!totalNodes.contains(child)) {
                                        nodes.add(child);
                                        totalNodes.add(child);
                                    }

                                    if (child.toString().endsWith(".class"))
                                        isJava = true;
                                }

                            if (isJava)
                                nodes.clear();
                        }
                    }

                    if (isJava)
                        setIcon(Resources.packagesIcon);
                    else {
                        setIcon(Resources.folderIcon);
                    }
                }
            }

            return ret;
        }
    }
}