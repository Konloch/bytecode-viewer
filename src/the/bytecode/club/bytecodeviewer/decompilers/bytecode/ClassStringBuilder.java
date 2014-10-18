package the.bytecode.club.bytecodeviewer.decompilers.bytecode;

/**
 * The buffer where decompiler classes output generated source
 * 
 * @author Waterwolf
 *
 */
public class ClassStringBuilder {
    private final StringBuilder builder;
    public final IndentationLevel iLevel;
    private static final String nl = System.getProperty("line.separator");
    private static final int TAB_SPACES = 4;
    private boolean isNewline = true;
    
    public ClassStringBuilder(final StringBuilder builder) {
        this.builder = builder;
        this.iLevel = new IndentationLevel();
    }
    
    public void append(final Object obj) {
        if (isNewline) {
            for (int i = 0;i < TAB_SPACES*iLevel.indentation; i++) {
                builder.append(" ");
            }
        }
        builder.append(obj);
        isNewline = false;
    }
    
    public void appendnl(final String s) {
        appendnl(s, 1);
    }
    
    public void appendnl() {
        appendnl("", 1);
    }
    
    public void appendnl(final String s, final int nlAmount) {
        append(s);
        for (int i = 0;i < nlAmount; i++) {
            builder.append(nl);
        }
        if (nlAmount > 0) {
            isNewline = true;
        }
    }
    
    public int increase() {
        return iLevel.increase();
    }
    
    public int decrease() {
        return iLevel.decrease();
    }
    
    public int get() {
        return iLevel.get();
    }
    
    public static class IndentationLevel {
        private int indentation = 0;
        
        public int increase() {
            return ++indentation;
        }
        
        public int decrease() {
            return --indentation;
        }
        
        public int get() {
            return indentation;
        }
    }
}
