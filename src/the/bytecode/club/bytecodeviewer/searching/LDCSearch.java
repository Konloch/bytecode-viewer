package the.bytecode.club.bytecodeviewer.searching;

import java.awt.GridLayout;
import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * LDC Searching
 * 
 * @author WaterWolf
 *
 */

public class LDCSearch implements SearchTypeDetails {
    
    JTextField searchText = new JTextField("");
    JPanel myPanel = null;
    
    @Override
    public JPanel getPanel() {
        if (myPanel == null) {
            myPanel = new JPanel(new GridLayout(1, 2));
            myPanel.add(new JLabel("Search String: "));
            myPanel.add(searchText);
        }
            
        return myPanel;
    }
    @SuppressWarnings("unchecked")
    @Override
    public void search(final ClassNode node, final SearchResultNotifier srn, boolean exact) {
        final Iterator<MethodNode> methods = node.methods.iterator();
        final String srchText = searchText.getText();
        if(srchText.isEmpty())
        	return;
        while (methods.hasNext()) {
            final MethodNode method = methods.next();
            
            final InsnList insnlist = method.instructions;
            final ListIterator<AbstractInsnNode> instructions = insnlist.iterator();
            while (instructions.hasNext()) {
                final AbstractInsnNode insnNode = instructions.next();
                if (insnNode instanceof LdcInsnNode) {
                    final Object ldcObject = ((LdcInsnNode) insnNode).cst;
                    final String ldcString = ldcObject.toString();
                    if ((exact && ldcString.equals(srchText)) ||
                    	(!exact && ldcString.contains(srchText)))
                    {
                        srn.notifyOfResult(node, method, insnNode);
                    }
                }
            }
            
        }
    }
}