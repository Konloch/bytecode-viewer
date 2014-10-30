package the.bytecode.club.bytecodeviewer.decompilers.bytecode;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;

/**
 * 
 * @author Konloch
 * @author Bibl
 *
 */

public class FieldNodeDecompiler {
	
	public static PrefixedStringBuilder decompile(PrefixedStringBuilder sb, FieldNode f) {
		String s = getAccessString(f.access);
		sb.append(s);
		if (s.length() > 0)
			sb.append(" ");
		sb.append(Type.getType(f.desc).getClassName());
		sb.append(" ");
		sb.append(f.name);
		if (f.value != null) {
			sb.append(" = ");
			if (f.value instanceof String) {
				sb.append("\"");
				sb.append(f.value);
				sb.append("\"");
			} else {
				sb.append(f.value);
				sb.append(" (");
				sb.append(f.value.getClass().getCanonicalName());
				sb.append(")");
			}
		}
		sb.append(";");
		return sb;
	}
	
	private static String getAccessString(int access) {
		List<String> tokens = new ArrayList<String>();
		if ((access & Opcodes.ACC_PUBLIC) != 0)
			tokens.add("public");
		if ((access & Opcodes.ACC_PRIVATE) != 0)
			tokens.add("private");
		if ((access & Opcodes.ACC_PROTECTED) != 0)
			tokens.add("protected");
		if ((access & Opcodes.ACC_SYNTHETIC) != 0)
			tokens.add("synthetic");
		if ((access & Opcodes.ACC_STATIC) != 0)
			tokens.add("static");
		if ((access & Opcodes.ACC_FINAL) != 0)
			tokens.add("final");
		if ((access & Opcodes.ACC_TRANSIENT) != 0)
			tokens.add("transient");
		if ((access & Opcodes.ACC_VOLATILE) != 0)
			tokens.add("volatile");
		if (tokens.size() == 0)
			return "";
		// hackery delimeters
		StringBuilder sb = new StringBuilder(tokens.get(0));
		for (int i = 1; i < tokens.size(); i++) {
			sb.append(" ");
			sb.append(tokens.get(i));
		}
		return sb.toString();
	}
}