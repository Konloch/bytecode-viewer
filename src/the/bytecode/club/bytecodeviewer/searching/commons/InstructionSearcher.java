package the.bytecode.club.bytecodeviewer.searching.commons;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
 
/**
 *  Class that searches bytecode instructions using given search conditions.
 * 
 * @author WaterWolf
 * @author Matthew Bovard
 * 
 */
public class InstructionSearcher {
    private final InsnList list;
    private AbstractInsnNode current;
 
    public InstructionSearcher(final MethodNode m) {
        this.list = m.instructions;
        this.current = list.getFirst();
    }
 
    public AbstractInsnNode getCurrent() {
        return current;
    }
 
    public void setCurrent(final AbstractInsnNode in) {
        current = in;
    }
 
    public AbstractInsnNode getNext(final int opcode) {
        return getNext(AnalyzerFactory.makeOpcodeCond(opcode));
    }
    
    public AbstractInsnNode getNext(final InsnAnalyzer analyzer) {
        while (current != null) {
            if (analyzer.accept(current)) {
                final AbstractInsnNode old = current;
                current = current.getNext();
                return old;
            }
            current = current.getNext();
        }
        return null;
    }
 
    public AbstractInsnNode getNext() {
        if (current == null)
            return null;
        current = current.getNext();
        while (current != null && current.getOpcode() == -1) {
            current = current.getNext();
        }
        return current;
    }
    
    public AbstractInsnNode getPrevious(final InsnAnalyzer analyzer) {
        while (current != null) {
            if (analyzer.accept(current)) {
                final AbstractInsnNode old = current;
                current = current.getPrevious();
                return old;
            }
            current = current.getPrevious();
        }
        return null;
    }
 
    public AbstractInsnNode getPrevious(final int opcode) {
        return getPrevious(AnalyzerFactory.makeOpcodeCond(opcode));
    }
 
    public AbstractInsnNode getPrevious() {
        current = current.getPrevious();
        while (current.getOpcode() == -1) {
            current = current.getPrevious();
        }
        return current;
    }
 
    public LdcInsnNode getNextLDC(final Object cst) {
        AbstractInsnNode in;
        while ((in = getNext(Opcodes.LDC)) != null) {
            final LdcInsnNode ln = (LdcInsnNode) in;
            if (ln.cst.equals(cst)) return ln;
        }
        return null;
    }
 
    public LdcInsnNode getPreviousLDC(final Object cst) {
        AbstractInsnNode in;
        while ((in = getPrevious(Opcodes.LDC)) != null) {
            final LdcInsnNode ln = (LdcInsnNode) in;
            if (ln.cst.equals(cst))
                return ln;
        }
        return null;
    }
    
    public IntInsnNode getNextInt(final int opcode, final int i) {
        return (IntInsnNode) this.getNext(AnalyzerFactory.makeIntCond(opcode, i));
    }
    
    public IntInsnNode getPreviousInt(final int opcode, final int i) {
        return (IntInsnNode) this.getPrevious(AnalyzerFactory.makeIntCond(opcode, i));
    }
 
    /**
     * @param opcode One of Opcodes.BIPUSH/Opcodes.SIPUSH
     * @param value  Value to look for
     * @return
     */
    public IntInsnNode getNextPush(final int opcode, final int value) {
        AbstractInsnNode in;
        while ((in = getNext(opcode)) != null) {
            final IntInsnNode iin = (IntInsnNode) in;
            if (iin.operand == value) return iin;
        }
        return null;
    }
 
    public List<AbstractInsnNode> analyze(final int opcode) {
        reset();
        final List<AbstractInsnNode> list = new ArrayList<AbstractInsnNode>();
        AbstractInsnNode in;
        while ((in = getNext(opcode)) != null) {
            list.add(in);
        }
        return list;
    }
 
    public int getIndex() {
        return list.indexOf(current);
    }
 
    public void setIndex(final int index) {
        current = list.get(index);
    }
 
    /**
     * Resets us back to the first instruction
     */
    public void reset() {
        current = list.getFirst();
    }
    
    public void resetToEnd() {
        current = list.getLast();
    }
    
    public void insert(final AbstractInsnNode location, final AbstractInsnNode insn) {
        this.list.insert(location, insn);
    }
    
    public int computePosition(final AbstractInsnNode node) {
        AbstractInsnNode poller = list.getFirst();
        int index = 0;
        while ((poller = poller.getNext()) != null) {
            if (poller.equals(node))
                return index;
            index++;
        }
        return -1;
    }
}