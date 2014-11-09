package the.bytecode.club.bytecodeviewer.searching;

import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Regex Searching
 * 
 * @author Konloch
 * @author WaterWolf
 *
 */

public class RegexSearch implements SearchTypeDetails {
    
    JTextField searchText = new JTextField("");
    JPanel myPanel = null;
    
    private static RegexInsnFinder regexFinder;
    
    @Override
    public JPanel getPanel() {
        if (myPanel == null) {
            myPanel = new JPanel(new GridLayout(1, 2));
            myPanel.add(new JLabel("Search Regex: "));
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
            
            if (regexFinder == null) {
                regexFinder = new RegexInsnFinder(node, method);
            }
            else {
                regexFinder.setMethod(node, method);
            }
            
            if (regexFinder.find(srchText).length > 0) {
                srn.notifyOfResult(node.name + "." + method.name + method.desc);
            }
            
        }
    }
}