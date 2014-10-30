package the.bytecode.club.bytecodeviewer.decompilers.bytecode;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LineNumberNode;

/**
 * 
 * @author Bibl
 *
 */

public class InstructionSearcher implements Opcodes {
	
	protected InsnList insns;
	protected InstructionPattern pattern;
	
	protected List<AbstractInsnNode[]> matches;
	
	public InstructionSearcher(InsnList insns, int[] opcodes) {
		this(insns, new InstructionPattern(opcodes));
	}
	
	public InstructionSearcher(InsnList insns, AbstractInsnNode[] ains) {
		this(insns, new InstructionPattern(ains));
	}
	
	public InstructionSearcher(InsnList insns, InstructionPattern pattern) {
		this.insns = insns;
		this.pattern = pattern;
		matches = new ArrayList<AbstractInsnNode[]>();
	}
	
	public boolean search() {
		for(AbstractInsnNode ain : insns.toArray()) {
			if (ain instanceof LineNumberNode || ain instanceof FrameNode)
				continue;
			if (pattern.accept(ain)) {
				matches.add(pattern.getLastMatch());
				pattern.resetMatch();
			}
		}
		return size() != 0;
	}
	
	public List<AbstractInsnNode[]> getMatches() {
		return matches;
	}
	
	public int size() {
		return matches.size();
	}
}