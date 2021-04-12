package the.bytecode.club.bytecodeviewer.compilers;

public class Compilers {
    public static Compiler krakatau = new KrakatauAssembler();
    public static Compiler smali = new SmaliAssembler();
    public static Compiler java = new JavaCompiler();
}
