package the.bytecode.club.bytecodeviewer.searching;

import java.awt.GridLayout;
import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import eu.bibl.banalysis.asm.desc.OpcodeInfo;

/**
 * Field call searching
 * 
 * @author Konloch
 * @author Water Wolf
 *
 */

public class FieldCallSearch implements SearchTypeDetails {
    
    JTextField mOwner = new JTextField(""), mName = new JTextField(""), mDesc = new JTextField("");
    JPanel myPanel = null;
    
    @Override
    public JPanel getPanel() {
        if (myPanel == null) {
            myPanel = new JPanel(new GridLayout(3, 2));
            myPanel.add(new JLabel("Owner: "));
            myPanel.add(mOwner);
            myPanel.add(new JLabel("Name: "));
            myPanel.add(mName);
            myPanel.add(new JLabel("Desc: "));
            myPanel.add(mDesc);
        }
            
        return myPanel;
    }
    @Override
    public void search(final ClassNode node, final SearchResultNotifier srn, boolean exact) {
        @SuppressWarnings("unchecked")
		final Iterator<MethodNode> methods = node.methods.iterator();
        String owner = mOwner.getText();
        if (owner.isEmpty()) {
            owner = null;
        }
        String name = mName.getText();
        if (name.isEmpty()) {
            name = null;
        }
        String desc = mDesc.getText();
        if (desc.isEmpty()) {
            desc = null;
        }
        while (methods.hasNext()) {
            final MethodNode method = methods.next();
            
            final InsnList insnlist = method.instructions;
            @SuppressWarnings("unchecked")
			final ListIterator<AbstractInsnNode> instructions = insnlist.iterator();
            while (instructions.hasNext()) {
                final AbstractInsnNode insnNode = instructions.next();
                if (insnNode instanceof FieldInsnNode) {
                    final FieldInsnNode min = (FieldInsnNode) insnNode;
                    if(name == null && owner == null && desc == null)
                    	continue;
	                if(exact) {
	                    if (name != null && !name.equals(min.name)) {
	                        continue;
	                    }
	                    if (owner != null && !owner.equals(min.owner)) {
	                        continue;
	                    }
	                    if (desc != null && !desc.equals(min.desc)) {
	                        continue;
	                    }
	                    srn.notifyOfResult(node.name + "." + method.name + Type.getType(method.desc) + " > " + OpcodeInfo.OPCODES.get(insnNode.getOpcode()).toLowerCase());
                    } else {

                        if (name != null && !name.contains(min.name)) {
                            continue;
                        }
                        if (owner != null && !owner.contains(min.owner)) {
                            continue;
                        }
                        if (desc != null && !desc.contains(min.desc)) {
                            continue;
                        }
	                    srn.notifyOfResult(node.name + "." + method.name + Type.getType(method.desc) + " > " + OpcodeInfo.OPCODES.get(insnNode.getOpcode()).toLowerCase());
                    }
                }
            }
            
        }
    }
}