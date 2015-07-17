package the.bytecode.club.bytecodeviewer.decompilers;

import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.decompilers.bytecode.ClassNodeDecompiler;

/**
 * Used to represent all of the decompilers/disassemblers BCV contains.
 * 
 * @author Konloch
 * 
 */

public abstract class Decompiler {

	public final static Decompiler bytecode = new ClassNodeDecompiler();
	public final static Decompiler fernflower = new FernFlowerDecompiler();
	public final static Decompiler procyon = new ProcyonDecompiler();
	public final static Decompiler cfr = new CFRDecompiler();
	public final static Decompiler krakatau = new KrakatauDecompiler();
	public final static Decompiler krakatauDA = new KrakatauDisassembler();
	public final static Decompiler smali = new SmaliDisassembler();
	public final static Decompiler jdgui = new JDGUIDecompiler();
	
	public abstract String decompileClassNode(ClassNode cn, byte[] b);
	
	public abstract void decompileToZip(String zipName);
	
	
}
