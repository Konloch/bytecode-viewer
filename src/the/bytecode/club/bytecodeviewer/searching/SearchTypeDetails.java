package the.bytecode.club.bytecodeviewer.searching;

import javax.swing.JPanel;

import org.objectweb.asm.tree.ClassNode;

/**
 * Search type details
 * 
 * @author WaterWolf
 *
 */

public interface SearchTypeDetails {
    public JPanel getPanel();

    public void search(ClassNode node, SearchResultNotifier srn, boolean exact);
}
