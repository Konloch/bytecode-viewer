package the.bytecode.club.bytecodeviewer.gui.resourcelist;

import the.bytecode.club.bytecodeviewer.gui.util.StringMetricsUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * @author Konloch
 * @since 6/22/2021
 */
public class ResourceTree extends JTree
{
	private static final long serialVersionUID = -2355167326094772096L;
    DefaultMutableTreeNode treeRoot;
	
	public ResourceTree(final DefaultMutableTreeNode treeRoot)
	{
        super(treeRoot);
        this.treeRoot = treeRoot;
    }
	
	StringMetricsUtil m = null;
	
	@Override
    public void paint(final Graphics g)
	{
        try
        {
            super.paint(g);
            if (m == null)
            {
                m = new StringMetricsUtil((Graphics2D) g);
            }
            if (treeRoot.getChildCount() < 1)
            {
                g.setColor(new Color(0, 0, 0, 100));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.white);
                String s = "Drag class/jar/zip/APK/DEX here";
                g.drawString(s,
                        ((int) ((getWidth() / 2) - (m.getWidth(s) / 2))),
                        getHeight() / 2);
            }
        }
        catch (InternalError | NullPointerException ignored)
        {
        }
    }
}
