package the.bytecode.club.bytecodeviewer.searching.commons;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;

/**
 * 
 * Class containing bytecode search conditions and couple of helper methods to make them
 * 
 * @author Waterwolf
 *
 **/
public class AnalyzerFactory implements Opcodes {
    public static InsnAnalyzer makeOpcodeCond(final int opcode) {
        return new InsnAnalyzer() {
            @Override
            public boolean accept(final AbstractInsnNode node) {
                return node.getOpcode() == opcode;
            }
        };
    }
    public static InsnAnalyzer makeFieldCond(final int opcode,
            final String type) {
        return new InsnAnalyzer() {
            @Override
            public boolean accept(final AbstractInsnNode node) {
                if (node instanceof FieldInsnNode)
                    return node.getOpcode() == opcode && ((FieldInsnNode) node).desc.equals(type);
                else
                    return false;
            }
        };
    }
    public static InsnAnalyzer makeFieldOwnerCond(final int opcode,
            final String owner) {
        return new InsnAnalyzer() {
            @Override
            public boolean accept(final AbstractInsnNode node) {
                if (node instanceof FieldInsnNode)
                    return node.getOpcode() == opcode && ((FieldInsnNode) node).owner.equals(owner);
                else
                    return false;
            }
        };
    }
    public static InsnAnalyzer makeFieldRegexCond(final int opcode,
            final String regex) {
        return new InsnAnalyzer() {
            @Override
            public boolean accept(final AbstractInsnNode node) {
                if (node instanceof FieldInsnNode)
                    return node.getOpcode() == opcode && ((FieldInsnNode) node).desc.matches(regex);
                else
                    return false;
            }
        };
    }
    
    public static InsnAnalyzer makeIntCond(final int opcode,
            final int value) {
        return new InsnAnalyzer() {
            @Override
            public boolean accept(final AbstractInsnNode node) {
                if (node instanceof IntInsnNode)
                    return node.getOpcode() == opcode && ((IntInsnNode) node).operand == value;
                else
                    return false;
            }
        };
    }
    
    /**
     * An instruction condition for a GETFIELD instruction with signature Z.
     */
    public final static InsnAnalyzer GETFIELD_Z =
        makeFieldCond(GETFIELD, "Z");
    /**
     * An instruction condition for a PUTFIELD instruction with signature Z.
     */
    public final static InsnAnalyzer PUTFIELD_Z =
        makeFieldCond(PUTFIELD, "Z");
    /**
     * An instruction condition for a GETSTATIC instruction with signature Z.
     */
    public final static InsnAnalyzer GETSTATIC_Z =
        makeFieldCond(GETSTATIC, "Z");
    /**
     * An instruction condition for a PUTSTATIC instruction with signature Z.
     */
    public final static InsnAnalyzer PUTSTATIC_Z =
        makeFieldCond(PUTSTATIC, "Z");

    /**
     * An instruction condition for a GETFIELD instruction with signature [Z.
     */
    public final static InsnAnalyzer GETFIELD_ZA =
        makeFieldCond(GETFIELD, "[Z");
    /**
     * An instruction condition for a PUTFIELD instruction with signature [Z.
     */
    public final static InsnAnalyzer PUTFIELD_ZA =
        makeFieldCond(PUTFIELD, "[Z");
    /**
     * An instruction condition for a GETSTATIC instruction with signature [Z.
     */
    public final static InsnAnalyzer GETSTATIC_ZA =
        makeFieldCond(GETSTATIC, "[Z");
    /**
     * An instruction condition for a PUTSTATIC instruction with signature [Z.
     */
    public final static InsnAnalyzer PUTSTATIC_ZA =
        makeFieldCond(PUTSTATIC, "[Z");

    /**
     * An instruction condition for a GETFIELD instruction with signature [[Z.
     */
    public final static InsnAnalyzer GETFIELD_ZAA =
        makeFieldCond(GETFIELD, "[[Z");
    /**
     * An instruction condition for a PUTFIELD instruction with signature [[Z.
     */
    public final static InsnAnalyzer PUTFIELD_ZAA =
        makeFieldCond(PUTFIELD, "[[Z");
    /**
     * An instruction condition for a GETSTATIC instruction with signature [[Z.
     */
    public final static InsnAnalyzer GETSTATIC_ZAA =
        makeFieldCond(GETSTATIC, "[[Z");
    /**
     * An instruction condition for a PUTSTATIC instruction with signature [[Z.
     */
    public final static InsnAnalyzer PUTSTATIC_ZAA =
        makeFieldCond(PUTSTATIC, "[[Z");

    /**
     * An instruction condition for a GETFIELD instruction with signature B.
     */
    public final static InsnAnalyzer GETFIELD_B =
        makeFieldCond(GETFIELD, "B");
    /**
     * An instruction condition for a PUTFIELD instruction with signature B.
     */
    public final static InsnAnalyzer PUTFIELD_B =
        makeFieldCond(PUTFIELD, "B");
    /**
     * An instruction condition for a GETSTATIC instruction with signature B.
     */
    public final static InsnAnalyzer GETSTATIC_B =
        makeFieldCond(GETSTATIC, "B");
    /**
     * An instruction condition for a PUTSTATIC instruction with signature B.
     */
    public final static InsnAnalyzer PUTSTATIC_B =
        makeFieldCond(PUTSTATIC, "B");

    /**
     * An instruction condition for a GETFIELD instruction with signature [B.
     */
    public final static InsnAnalyzer GETFIELD_BA =
        makeFieldCond(GETFIELD, "[B");
    /**
     * An instruction condition for a PUTFIELD instruction with signature [B.
     */
    public final static InsnAnalyzer PUTFIELD_BA =
        makeFieldCond(PUTFIELD, "[B");
    /**
     * An instruction condition for a GETSTATIC instruction with signature [B.
     */
    public final static InsnAnalyzer GETSTATIC_BA =
        makeFieldCond(GETSTATIC, "[B");
    /**
     * An instruction condition for a PUTSTATIC instruction with signature [B.
     */
    public final static InsnAnalyzer PUTSTATIC_BA =
        makeFieldCond(PUTSTATIC, "[B");

    /**
     * An instruction condition for a GETFIELD instruction with signature [[B.
     */
    public final static InsnAnalyzer GETFIELD_BAA =
        makeFieldCond(GETFIELD, "[[B");
    
    /**
     * An instruction condition for a PUTFIELD instruction with signature [[B.
     */
    public final static InsnAnalyzer PUTFIELD_BAA =
        makeFieldCond(PUTFIELD, "[[B");
    /**
     * An instruction condition for a GETSTATIC instruction with signature [[B.
     */
    public final static InsnAnalyzer GETSTATIC_BAA =
        makeFieldCond(GETSTATIC, "[[B");
    /**
     * An instruction condition for a PUTSTATIC instruction with signature [[B.
     */
    public final static InsnAnalyzer PUTSTATIC_BAA =
        makeFieldCond(PUTSTATIC, "[[B");

    /**
     * An instruction condition for a GETFIELD instruction with signature C.
     */
    public final static InsnAnalyzer GETFIELD_C =
        makeFieldCond(GETFIELD, "C");
    /**
     * An instruction condition for a PUTFIELD instruction with signature C.
     */
    public final static InsnAnalyzer PUTFIELD_C =
        makeFieldCond(PUTFIELD, "C");
    /**
     * An instruction condition for a GETSTATIC instruction with signature C.
     */
    public final static InsnAnalyzer GETSTATIC_C =
        makeFieldCond(GETSTATIC, "C");
    /**
     * An instruction condition for a PUTSTATIC instruction with signature C.
     */
    public final static InsnAnalyzer PUTSTATIC_C =
        makeFieldCond(PUTSTATIC, "C");

    /**
     * An instruction condition for a GETFIELD instruction with signature [C.
     */
    public final static InsnAnalyzer GETFIELD_CA =
        makeFieldCond(GETFIELD, "[C");
    /**
     * An instruction condition for a PUTFIELD instruction with signature [C.
     */
    public final static InsnAnalyzer PUTFIELD_CA =
        makeFieldCond(PUTFIELD, "[C");
    /**
     * An instruction condition for a GETSTATIC instruction with signature [C.
     */
    public final static InsnAnalyzer GETSTATIC_CA =
        makeFieldCond(GETSTATIC, "[C");
    /**
     * An instruction condition for a PUTSTATIC instruction with signature [C.
     */
    public final static InsnAnalyzer PUTSTATIC_CA =
        makeFieldCond(PUTSTATIC, "[C");

    /**
     * An instruction condition for a GETFIELD instruction with signature [[C.
     */
    public final static InsnAnalyzer GETFIELD_CAA =
        makeFieldCond(GETFIELD, "[[C");
    /**
     * An instruction condition for a PUTFIELD instruction with signature [[C.
     */
    public final static InsnAnalyzer PUTFIELD_CAA =
        makeFieldCond(PUTFIELD, "[[C");
    /**
     * An instruction condition for a GETSTATIC instruction with signature [[C.
     */
    public final static InsnAnalyzer GETSTATIC_CAA =
        makeFieldCond(GETSTATIC, "[[C");
    /**
     * An instruction condition for a PUTSTATIC instruction with signature [[C.
     */
    public final static InsnAnalyzer PUTSTATIC_CAA =
        makeFieldCond(PUTSTATIC, "[[C");

    /**
     * An instruction condition for a GETFIELD instruction with signature S.
     */
    public final static InsnAnalyzer GETFIELD_S =
        makeFieldCond(GETFIELD, "S");
    /**
     * An instruction condition for a PUTFIELD instruction with signature S.
     */
    public final static InsnAnalyzer PUTFIELD_S =
        makeFieldCond(PUTFIELD, "S");
    /**
     * An instruction condition for a GETSTATIC instruction with signature S.
     */
    public final static InsnAnalyzer GETSTATIC_S =
        makeFieldCond(GETSTATIC, "S");
    /**
     * An instruction condition for a PUTSTATIC instruction with signature S.
     */
    public final static InsnAnalyzer PUTSTATIC_S =
        makeFieldCond(PUTSTATIC, "S");

    /**
     * An instruction condition for a GETFIELD instruction with signature [S.
     */
    public final static InsnAnalyzer GETFIELD_SA =
        makeFieldCond(GETFIELD, "[S");
    /**
     * An instruction condition for a PUTFIELD instruction with signature [S.
     */
    public final static InsnAnalyzer PUTFIELD_SA =
        makeFieldCond(PUTFIELD, "[S");
    /**
     * An instruction condition for a GETSTATIC instruction with signature [S.
     */
    public final static InsnAnalyzer GETSTATIC_SA =
        makeFieldCond(GETSTATIC, "[S");
    /**
     * An instruction condition for a PUTSTATIC instruction with signature [S.
     */
    public final static InsnAnalyzer PUTSTATIC_SA =
        makeFieldCond(PUTSTATIC, "[S");

    /**
     * An instruction condition for a GETFIELD instruction with signature [[S.
     */
    public final static InsnAnalyzer GETFIELD_SAA =
        makeFieldCond(GETFIELD, "[[S");
    /**
     * An instruction condition for a PUTFIELD instruction with signature [[S.
     */
    public final static InsnAnalyzer PUTFIELD_SAA =
        makeFieldCond(PUTFIELD, "[[S");
    /**
     * An instruction condition for a GETSTATIC instruction with signature [[S.
     */
    public final static InsnAnalyzer GETSTATIC_SAA =
        makeFieldCond(GETSTATIC, "[[S");
    /**
     * An instruction condition for a PUTSTATIC instruction with signature [[S.
     */
    public final static InsnAnalyzer PUTSTATIC_SAA =
        makeFieldCond(PUTSTATIC, "[[S");
    
    public final static InsnAnalyzer GETFIELD_I =
        makeFieldCond(GETFIELD, "I");
    public final static InsnAnalyzer PUTFIELD_I =
        makeFieldCond(PUTFIELD, "I");
    public final static InsnAnalyzer GETSTATIC_I =
        makeFieldCond(GETSTATIC, "I");
    public final static InsnAnalyzer PUTSTATIC_I =
        makeFieldCond(PUTSTATIC, "I");
    
    public final static InsnAnalyzer GETFIELD_IA =
        makeFieldCond(GETFIELD, "[I");
    public final static InsnAnalyzer PUTFIELD_IA =
        makeFieldCond(PUTFIELD, "[I");
    public final static InsnAnalyzer GETSTATIC_IA =
        makeFieldCond(GETSTATIC, "[I");
    public final static InsnAnalyzer PUTSTATIC_IA =
        makeFieldCond(PUTSTATIC, "[I");
    
    public final static InsnAnalyzer GETFIELD_IAA =
        makeFieldCond(GETFIELD, "[[I");
    public final static InsnAnalyzer PUTFIELD_IAA =
        makeFieldCond(PUTFIELD, "[[I");
    public final static InsnAnalyzer GETSTATIC_IAA =
        makeFieldCond(GETSTATIC, "[[I");
    public final static InsnAnalyzer PUTSTATIC_IAA =
        makeFieldCond(PUTSTATIC, "[[I");
    
    public final static InsnAnalyzer GETFIELD_J =
        makeFieldCond(GETFIELD, "J");
    public final static InsnAnalyzer PUTFIELD_J =
        makeFieldCond(PUTFIELD, "J");
    public final static InsnAnalyzer GETSTATIC_J =
        makeFieldCond(GETSTATIC, "J");
    public final static InsnAnalyzer PUTSTATIC_J =
        makeFieldCond(PUTSTATIC, "J");
    
    public final static InsnAnalyzer GETFIELD_JA =
        makeFieldCond(GETFIELD, "[J");
    public final static InsnAnalyzer PUTFIELD_JA =
        makeFieldCond(PUTFIELD, "[J");
    public final static InsnAnalyzer GETSTATIC_JA =
        makeFieldCond(GETSTATIC, "[J");
    public final static InsnAnalyzer PUTSTATIC_JA =
        makeFieldCond(PUTSTATIC, "[J");
    
    public final static InsnAnalyzer GETFIELD_JAA =
        makeFieldCond(GETFIELD, "[[J");
    public final static InsnAnalyzer PUTFIELD_JAA =
        makeFieldCond(PUTFIELD, "[[J");
    public final static InsnAnalyzer GETSTATIC_JAA =
        makeFieldCond(GETSTATIC, "[[J");
    public final static InsnAnalyzer PUTSTATIC_JAA =
        makeFieldCond(PUTSTATIC, "[[J");
    
    public final static InsnAnalyzer GETFIELD_F =
        makeFieldCond(GETFIELD, "F");
    public final static InsnAnalyzer PUTFIELD_F =
        makeFieldCond(PUTFIELD, "F");
    public final static InsnAnalyzer GETSTATIC_F =
        makeFieldCond(GETSTATIC, "F");
    public final static InsnAnalyzer PUTSTATIC_F =
        makeFieldCond(PUTSTATIC, "F");
    
    public final static InsnAnalyzer GETFIELD_FA =
        makeFieldCond(GETFIELD, "[F");
    public final static InsnAnalyzer PUTFIELD_FA =
        makeFieldCond(PUTFIELD, "[F");
    public final static InsnAnalyzer GETSTATIC_FA =
        makeFieldCond(GETSTATIC, "[F");
    public final static InsnAnalyzer PUTSTATIC_FA =
        makeFieldCond(PUTSTATIC, "[F");
    
    public final static InsnAnalyzer GETFIELD_FAA =
        makeFieldCond(GETFIELD, "[[F");
    public final static InsnAnalyzer PUTFIELD_FAA =
        makeFieldCond(PUTFIELD, "[[F");
    public final static InsnAnalyzer GETSTATIC_FAA =
        makeFieldCond(GETSTATIC, "[[F");
    public final static InsnAnalyzer PUTSTATIC_FAA =
        makeFieldCond(PUTSTATIC, "[[F");
    
    public final static InsnAnalyzer GETFIELD_D =
        makeFieldCond(GETFIELD, "D");
    public final static InsnAnalyzer PUTFIELD_D =
        makeFieldCond(PUTFIELD, "D");
    public final static InsnAnalyzer GETSTATIC_D =
        makeFieldCond(GETSTATIC, "D");
    public final static InsnAnalyzer PUTSTATIC_D =
        makeFieldCond(PUTSTATIC, "D");
    
    public final static InsnAnalyzer GETFIELD_DA =
        makeFieldCond(GETFIELD, "[D");
    public final static InsnAnalyzer PUTFIELD_DA =
        makeFieldCond(PUTFIELD, "[D");
    public final static InsnAnalyzer GETSTATIC_DA =
        makeFieldCond(GETSTATIC, "[D");
    public final static InsnAnalyzer PUTSTATIC_DA =
        makeFieldCond(PUTSTATIC, "[D");
    
    public final static InsnAnalyzer GETFIELD_DAA =
        makeFieldCond(GETFIELD, "[[D");
    public final static InsnAnalyzer PUTFIELD_DAA =
        makeFieldCond(PUTFIELD, "[[D");
    public final static InsnAnalyzer GETSTATIC_DAA =
        makeFieldCond(GETSTATIC, "[[D");
    public final static InsnAnalyzer PUTSTATIC_DAA =
        makeFieldCond(PUTSTATIC, "[[D");
    
    public final static InsnAnalyzer GETFIELD_L =
        makeFieldRegexCond(GETFIELD, "L.*;");
    public final static InsnAnalyzer PUTFIELD_L =
        makeFieldRegexCond(PUTFIELD, "L.*;");
    public final static InsnAnalyzer GETSTATIC_L =
        makeFieldRegexCond(GETSTATIC, "L.*;");
    public final static InsnAnalyzer PUTSTATIC_L =
        makeFieldRegexCond(PUTSTATIC, "L.*;");
    
    public final static InsnAnalyzer GETFIELD_LA =
        makeFieldRegexCond(GETFIELD, "\\[L.*;");
    public final static InsnAnalyzer PUTFIELD_LA =
        makeFieldRegexCond(PUTFIELD, "\\[L.*;");
    public final static InsnAnalyzer GETSTATIC_LA =
        makeFieldRegexCond(GETSTATIC, "\\[L.*;");
    public final static InsnAnalyzer PUTSTATIC_LA =
        makeFieldRegexCond(PUTSTATIC, "\\[L.*;");
    
    public final static InsnAnalyzer GETFIELD_LAA =
        makeFieldRegexCond(GETFIELD, "\\[\\[L.*;");
    public final static InsnAnalyzer PUTFIELD_LAA =
        makeFieldRegexCond(PUTFIELD, "\\[\\[L.*;");
    public final static InsnAnalyzer GETSTATIC_LAA =
        makeFieldRegexCond(GETSTATIC, "\\[\\[L.*;");
    public final static InsnAnalyzer PUTSTATIC_LAA =
        makeFieldRegexCond(PUTSTATIC, "\\[\\[L.*;");
    
    public final static InsnAnalyzer GETFIELD_String =
        makeFieldCond(GETFIELD, "Ljava/lang/String;");
    public final static InsnAnalyzer PUTFIELD_String =
        makeFieldCond(PUTFIELD, "Ljava/lang/String;");
    public final static InsnAnalyzer GETSTATIC_String =
        makeFieldCond(GETSTATIC, "Ljava/lang/String;");
    public final static InsnAnalyzer PUTSTATIC_String =
        makeFieldCond(PUTSTATIC, "Ljava/lang/String;");
    
    public final static InsnAnalyzer GETFIELD_StringA =
        makeFieldCond(GETFIELD, "[Ljava/lang/String;");
    public final static InsnAnalyzer PUTFIELD_StringA =
        makeFieldCond(PUTFIELD, "[Ljava/lang/String;");
    public final static InsnAnalyzer GETSTATIC_StringA =
        makeFieldCond(GETSTATIC, "[Ljava/lang/String;");
    public final static InsnAnalyzer PUTSTATIC_StringA =
        makeFieldCond(PUTSTATIC, "[Ljava/lang/String;");
    
    public final static InsnAnalyzer GETFIELD_StringAA =
        makeFieldCond(GETFIELD, "[[Ljava/lang/String;");
    public final static InsnAnalyzer PUTFIELD_StringAA =
        makeFieldCond(PUTFIELD, "[[Ljava/lang/String;");
    public final static InsnAnalyzer GETSTATIC_StringAA =
        makeFieldCond(GETSTATIC, "[[Ljava/lang/String;");
    public final static InsnAnalyzer PUTSTATIC_StringAA =
        makeFieldCond(PUTSTATIC, "[[Ljava/lang/String;");
    
}
