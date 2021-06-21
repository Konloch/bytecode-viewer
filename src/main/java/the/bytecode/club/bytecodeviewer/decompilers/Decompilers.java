package the.bytecode.club.bytecodeviewer.decompilers;

import the.bytecode.club.bytecodeviewer.decompilers.bytecode.ClassNodeDecompiler;

public class Decompilers {

    public final static Decompiler bytecode = new ClassNodeDecompiler();
    public final static Decompiler fernflower = new FernFlowerDecompiler();
    public final static Decompiler procyon = new ProcyonDecompiler();
    public final static Decompiler cfr = new CFRDecompiler();
    public final static KrakatauDecompiler krakatau = new KrakatauDecompiler();
    public final static KrakatauDisassembler krakatauDA = new KrakatauDisassembler();
    public final static SmaliDisassembler smali = new SmaliDisassembler();
    public final static Decompiler jdgui = new JDGUIDecompiler();
    public final static Decompiler jadx = new JADXDecompiler();
    public final static Decompiler textifier = new ASMTextifierDecompiler();

}
