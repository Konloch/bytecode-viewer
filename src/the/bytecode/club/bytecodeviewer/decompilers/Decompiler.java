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

	public abstract String decompileClassNode(ClassNode cn, byte[] b);

	public abstract void decompileToZip(String zipName);

	public abstract void decompileToClass(String className, String classNameSaved);

	public static Decompiler bytecode = new ClassNodeDecompiler();
	public static Decompiler fernflower = new FernFlowerDecompiler();
	public static Decompiler procyon = new ProcyonDecompiler();
	public static Decompiler cfr = new CFRDecompiler();
	public static Decompiler krakatau = new KrakatauDecompiler();
	public static Decompiler krakatauDA = new KrakatauDisassembler();
	public static Decompiler smali = new SmaliDisassembler();
}
