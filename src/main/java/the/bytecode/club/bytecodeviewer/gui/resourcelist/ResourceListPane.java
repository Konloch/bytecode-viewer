package the.bytecode.club.bytecodeviewer.gui.resourcelist;

import java.awt.*;
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import me.konloch.kontainer.io.DiskWriter;
import org.apache.commons.io.FilenameUtils;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.contextmenu.ContextMenu;
import the.bytecode.club.bytecodeviewer.gui.theme.LAFTheme;
import the.bytecode.club.bytecodeviewer.resources.IconResources;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.importing.Import;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJCheckBox;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJTextField;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedVisibleComponent;
import the.bytecode.club.bytecodeviewer.util.FileDrop;
import the.bytecode.club.bytecodeviewer.util.LazyNameUtil;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import static the.bytecode.club.bytecodeviewer.Constants.fs;
import static the.bytecode.club.bytecodeviewer.Constants.tempDirectory;

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
 * @since 09/26/2011
 */

public class ResourceListPane extends TranslatedVisibleComponent implements FileDrop.Listener
{
    public final JPopupMenu rightClickMenu = new JPopupMenu();
    public final JCheckBox autoOpen = new TranslatedJCheckBox("Auto open", TranslatedComponents.EXACT_PATH);
    public final JCheckBox exact = new TranslatedJCheckBox("Exact path", TranslatedComponents.EXACT_PATH);
    public final JCheckBox caseSensitive = new TranslatedJCheckBox("Match case", TranslatedComponents.MATCH_CASE);
    public final JButton open = new JButton(IconResources.add);
    public final JButton close = new JButton(IconResources.remove);
    public final ResourceTreeNode treeRoot = new ResourceTreeNode("Loaded Files:");
    public final ResourceTree tree = new ResourceTree(treeRoot);
    public final JTextField quickSearch = new TranslatedJTextField("Quick file search (no file extension)", TranslatedComponents.QUICK_FILE_SEARCH_NO_FILE_EXTENSION);
    public final FileDrop fileDrop;
    public boolean cancel = false;
    
    public final KeyAdapter search = new SearchKeyAdapter(this);
    
    private void showContextMenu(ResourceTree tree, TreePath selPath, int x, int y)
    {
        if (selPath == null)
            return;
    
        ContextMenu.buildMenu(tree, selPath, null, rightClickMenu);
        rightClickMenu.show(this.tree, x, y);
    }
    
    public ResourceListPane()
    {
        super("Files", TranslatedComponents.FILES);
        
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        quickSearch.setForeground(quickSearch.getDisabledTextColor());
    
        attachTreeListeners();
        attachQuickSearchListeners();
    
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(tree), BorderLayout.CENTER);
    
        JPanel exactPanel = new JPanel(new BorderLayout());
        JPanel quickSearchPanel = new JPanel();
        JPanel buttonPanel = new JPanel(new BorderLayout());
        
        quickSearchPanel.setLayout(new BorderLayout());
        quickSearchPanel.add(quickSearch, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout());
        btns.add(exact);
        btns.add(caseSensitive);
        btns.add(autoOpen);
        exactPanel.add(btns, BorderLayout.WEST);

        buttonPanel.add(open, BorderLayout.EAST);
        buttonPanel.add(close, BorderLayout.WEST);
        quickSearchPanel.add(buttonPanel, BorderLayout.EAST);
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
    
    public void addResourceContainer(ResourceContainer container)
    {
        ResourceTreeNode root = container.treeNode = new ResourceTreeNode(container.name);
    
        treeRoot.add(root);
        tree.setCellRenderer(new ResourceListIconRenderer());
        
        buildTree(container, root);
    
        treeRoot.sort();
        
        tree.expandPath(new TreePath(tree.getModel().getRoot()));
        tree.updateUI();
    
        //TODO add a setting to expand on resource import
        // expandAll(tree, true);
    }
    
    public void removeResource(ResourceContainer container)
    {
        container.treeNode.removeFromParent();
        tree.updateUI();
    }
    
    private void buildTree(ResourceContainer container, ResourceTreeNode root)
    {
        if (!container.resourceClasses.isEmpty())
        {
            for (String name : container.resourceClasses.keySet())
            {
                final String[] spl = name.split("/");
                int splLength = spl.length;
                if (splLength < 2)
                {
                    root.add(new ResourceTreeNode(name + ".class"));
                }
                else
                {
                    ResourceTreeNode parent = root;
                    for (int i1 = 0; i1 < splLength; i1++)
                    {
                        String s = spl[i1];
                    
                        if (i1 == splLength - 1)
                            s += ".class";
                    
                        ResourceTreeNode child = parent.getChildByUserObject(s);
                    
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
    
        if (!container.resourceFiles.isEmpty())
        {
            for (final Entry<String, byte[]> entry : container.resourceFiles.entrySet())
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
                        ResourceTreeNode child = parent.getChildByUserObject(s);

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

    @SuppressWarnings("rawtypes")
    public void expandAll(final JTree tree, final TreePath parent,
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

    public void removeNode(final JTree tree, final TreePath nodePath)
    {
        MutableTreeNode node = findNodeByPath(nodePath);
		
        if (node == null)
            return;
        
        node.removeFromParent();
        tree.repaint();
        tree.updateUI();
    }
    
    private MutableTreeNode findNodeByPath(TreePath path) {
        MutableTreeNode node = treeRoot;
        for (int pathStep = 1; pathStep < path.getPathCount(); pathStep++) {
            TreeNode pathNode = (TreeNode) path.getPathComponent(pathStep);
            int childIndex = node.getIndex(pathNode);
            if (childIndex < 0) {
                return null;
            }
            node = (MutableTreeNode) node.getChildAt(childIndex);
            
            if (node == null) {
                return null;
            }
        }
        
        return node;
    }
    
    public void resetWorkspace()
    {
        treeRoot.removeAllChildren();
        tree.repaint();
        tree.updateUI();
    }
    
    /**
     * Opens and decompiles the TreePath in a new tab
     */
    public void quickDecompile(Decompiler decompiler, TreePath selPath, boolean quickEdit)
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
        
        openPath(selPath);
    
        BytecodeViewer.viewer.viewPane1.setSelectedDecompiler(tempDecompiler1);
        BytecodeViewer.viewer.viewPane1.setPaneEditable(editable1);
        BytecodeViewer.viewer.viewPane2.setSelectedDecompiler(tempDecompiler2);
        BytecodeViewer.viewer.viewPane2.setPaneEditable(editable2);
        BytecodeViewer.viewer.viewPane3.setSelectedDecompiler(tempDecompiler3);
        BytecodeViewer.viewer.viewPane3.setPaneEditable(editable3);
    }

    public void openPath(TreePath path)
    {
		//do not open null path, or gui root path
        if (path == null || path.getPathCount() == 1)
            return;

        final StringBuilder nameBuffer = new StringBuilder();
        for (int i = 2; i < path.getPathCount(); i++)
        {
            nameBuffer.append(path.getPathComponent(i));
            if (i < path.getPathCount() - 1)
                nameBuffer.append("/");
        }

        String pathName = path.getPathComponent(1).toString();
	    ResourceContainer container = getContainerFromName(pathName);
        String name = nameBuffer.toString();
        
        boolean resourceMode = false;
        byte[] content = container.resourceClassBytes.get(name);
        
        if(content == null)
        {
            content = container.resourceFiles.get(name);
            resourceMode = true;
        }
        
        //view classes
        if (content != null && MiscUtils.getFileHeaderMagicNumber(content).equalsIgnoreCase("cafebabe")
                || name.endsWith(".class"))
        {
            try
            {
                if(resourceMode)
                {
                    //TODO load this cn into the resource viewer
                    //final ClassNode cn = ASMUtil.bytesToNode(content);
                }
                
                //display via name
                BytecodeViewer.viewer.workPane.addClassResource(container, name);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                BytecodeViewer.viewer.workPane.addFileResource(container, name);
            }
        }
        //view non-classfile resources
        else if(container.resourceFiles.containsKey(name))
        {
            final String fn = name.toLowerCase();
            final String extension = fn.contains(":") ? null : FilenameUtils.getExtension(fn);
    
            Import imp = Import.extensionMap.get(extension);
            if(imp == null) //show images, text files, or hex view
                BytecodeViewer.viewer.workPane.addFileResource(container, name);
            else //attempt to import known resources
            {
                int hash = (container.name + name).hashCode();
                
                //TODO make a settings toggle to disable preservation of the original name
                // it should also detect if the file name is not compatible with the current OS and enable automatically
                File tempFile = new File(tempDirectory + fs + hash + fs + name + "." + extension);
                if(!tempFile.exists())
                {
                    DiskWriter.replaceFileBytes(tempFile.getAbsolutePath(), content, false);
    
                    try
                    {
                        imp.getImporter().open(tempFile);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
        
                        //failsafe
                        BytecodeViewer.viewer.workPane.addFileResource(container, name);
                    }
                }
                else
                {
                    //alert the user
                }
            }
        }
    }
	
	//TODO support non-containers being removed
	// this will require us finding all child nodes in the tree path provided,
	// then removing each one by one from both memory and the GUI
    public void deletePath(TreePath path)
    {
	    //do not open null path, or gui root path
        if (path == null || path.getPathCount() == 1)
            return;
		
		//verify the path is a container root
		if(path.getPathCount() != 2)
			return;

        String pathName = path.getPathComponent(1).toString();
        ResourceContainer container = getContainerFromName(pathName);
		
		if(container != null)
		{
			deleteContainer(container);
		}
    }
	
	public void deleteContainer(ResourceContainer container)
	{
		container.resourceFiles.clear();
		container.resourceClasses.clear();
		container.resourceClassBytes.clear();
		BytecodeViewer.resourceContainers.values().remove(container);
		LazyNameUtil.removeName(container.name);
	}
	
	public ResourceContainer getContainerFromName(String name)
	{
		for (ResourceContainer c : BytecodeViewer.resourceContainers.values())
		{
			if (c.name.equals(name))
				return c;
		}
		
		return null;
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
                    TreePath selPath = ResourceListPane.this.tree.getClosestPathForLocation(e.getX(), e.getY());
                    
                    if (selPath == null)
                        return;
                    
                    showContextMenu(tree, selPath, e.getX(), e.getY());
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
                if(e.getButton() == MouseEvent.BUTTON1) //right-click
                    openPath(tree.getPathForLocation(e.getX(), e.getY()));
            }
        });
    
        /*this.tree.addTreeSelectionListener(arg0 -> {
            if (cancel) {
                cancel = false;
                return;
            }
            
            openPath(arg0.getPath());
        });*/
    
        this.tree.addKeyListener(new KeyListener()
        {
            @Override
            public void keyReleased(KeyEvent e) { }
        
            @Override
            public void keyTyped(KeyEvent e) { }
        
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    if (e.getSource() instanceof ResourceTree)
                    {
                        ResourceTree tree = (ResourceTree) e.getSource();
                        openPath(tree.getSelectionPath());
                    }
                }
                else if ((int) e.getKeyChar() != 0 &&
                        (int) e.getKeyChar() != 8 &&
                        (int) e.getKeyChar() != 127 &&
                        (int) e.getKeyChar() != 65535 &&
                        !e.isControlDown() &&
                        !e.isAltDown())
                {
                    quickSearch.grabFocus();
                    quickSearch.setText("" + e.getKeyChar());
                    cancel = true;
                }
                else if (e.isControlDown() && (int) e.getKeyChar() == 6) //ctrl + f
                    quickSearch.grabFocus();
                else
                    cancel = true;
            }
        });
    }
    
    public void attachQuickSearchListeners()
    {
        quickSearch.addKeyListener(search);
        quickSearch.addFocusListener(new FocusListener()
        {
            @Override
            public void focusGained(final FocusEvent arg0)
            {
                if (quickSearch.getText().equals(TranslatedStrings.QUICK_FILE_SEARCH_NO_FILE_EXTENSION.toString()))
                {
                    quickSearch.setText("");
					
					if(Configuration.lafTheme != LAFTheme.SYSTEM)
                        quickSearch.setForeground(quickSearch.getSelectedTextColor());
                }
            }
        
            @Override
            public void focusLost(final FocusEvent arg0)
            {
                if (quickSearch.getText().isEmpty())
                {
                    quickSearch.setText(TranslatedStrings.QUICK_FILE_SEARCH_NO_FILE_EXTENSION.toString());
	
	                if(Configuration.lafTheme != LAFTheme.SYSTEM)
                        quickSearch.setForeground(quickSearch.getDisabledTextColor());
                }
            }
        });
    }
}