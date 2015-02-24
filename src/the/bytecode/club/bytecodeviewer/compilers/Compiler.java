package the.bytecode.club.bytecodeviewer.compilers;

/**
 * Used to represent all the compilers/assemblers BCV contains.
 * 
 * @author Konloch
 *
 */

public abstract class Compiler {

	public abstract byte[] compile(String contents, String name);

	public static Compiler krakatau = new KrakatauAssembler();
	public static Compiler smali = new SmaliAssembler();
	public static Compiler java = new JavaCompiler();
	
}
