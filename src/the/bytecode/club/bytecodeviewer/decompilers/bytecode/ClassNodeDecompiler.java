package the.bytecode.club.bytecodeviewer.decompilers.bytecode;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;

/**
 * 
 * @author Konloch
 * @author Bibl
 * 
 */

public class ClassNodeDecompiler extends Decompiler {

	public String decompileClassNode(ClassNode cn, byte[] b) {
		return decompile(new PrefixedStringBuilder(),
				new ArrayList<String>(), cn).toString();
	}

	@SuppressWarnings("unchecked")
	protected static PrefixedStringBuilder decompile(
			PrefixedStringBuilder sb, ArrayList<String> decompiledClasses,
			ClassNode cn) {
		ArrayList<String> unableToDecompile = new ArrayList<String>();
		decompiledClasses.add(cn.name);
		sb.append(getAccessString(cn.access));
		sb.append(" ");
		sb.append(cn.name);
		if (cn.superName != null && !cn.superName.equals("java/lang/Object")) {
			sb.append(" extends ");
			sb.append(cn.superName);
		}

		int amountOfInterfaces = cn.interfaces.size();
		if (amountOfInterfaces > 0) {
			sb.append(" implements ");
			sb.append(cn.interfaces.get(0));
			if (amountOfInterfaces > 1) {
				// sb.append(",");
			}
			for (int i = 1; i < amountOfInterfaces; i++) {
				sb.append(", ");
				sb.append(cn.interfaces.get(i));
			}
		}
		sb.append(" {");
		sb.append(BytecodeViewer.nl);
		for (FieldNode fn : (List<FieldNode>) cn.fields) {
			sb.append(BytecodeViewer.nl);
			sb.append("     ");
			FieldNodeDecompiler.decompile(sb, fn);
		}
		if (cn.fields.size() > 0) {
			sb.append(BytecodeViewer.nl);
		}
		for (MethodNode mn : (List<MethodNode>) cn.methods) {
			sb.append(BytecodeViewer.nl);
			MethodNodeDecompiler.decompile(sb, mn, cn);
		}

		for (Object o : cn.innerClasses) {
			InnerClassNode innerClassNode = (InnerClassNode) o;
			String innerClassName = innerClassNode.name;
			if ((innerClassName != null)
					&& !decompiledClasses.contains(innerClassName)) {
				decompiledClasses.add(innerClassName);
				ClassNode cn1 = BytecodeViewer.getClassNode(innerClassName);
				if (cn1 != null) {
					sb.appendPrefix("     ");
					sb.append(BytecodeViewer.nl + BytecodeViewer.nl);
					sb = decompile(sb, decompiledClasses, cn1);
					sb.trimPrefix(5);
					sb.append(BytecodeViewer.nl);
				} else {
					unableToDecompile.add(innerClassName);
				}
			}
		}

		if (!unableToDecompile.isEmpty()) {
			sb.append("//the following inner classes couldn't be decompiled: ");
			for (String s : unableToDecompile) {
				sb.append(s);
				sb.append(" ");
			}
			sb.append(BytecodeViewer.nl);
		}

		sb.append("}");
		// System.out.println("Wrote end for " + cn.name +
		// " with prefix length: " + sb.prefix.length());
		return sb;
	}

	public static String getAccessString(int access) {
		List<String> tokens = new ArrayList<String>();
		if ((access & Opcodes.ACC_PUBLIC) != 0)
			tokens.add("public");
		if ((access & Opcodes.ACC_PRIVATE) != 0)
			tokens.add("private");
		if ((access & Opcodes.ACC_PROTECTED) != 0)
			tokens.add("protected");
		if ((access & Opcodes.ACC_FINAL) != 0)
			tokens.add("final");
		if ((access & Opcodes.ACC_SYNTHETIC) != 0)
			tokens.add("synthetic");
		// if ((access & Opcodes.ACC_SUPER) != 0)
		// tokens.add("super"); implied by invokespecial insn
		if ((access & Opcodes.ACC_ABSTRACT) != 0)
			tokens.add("abstract");
		if ((access & Opcodes.ACC_INTERFACE) != 0)
			tokens.add("interface");
		if ((access & Opcodes.ACC_ENUM) != 0)
			tokens.add("enum");
		if ((access & Opcodes.ACC_ANNOTATION) != 0)
			tokens.add("annotation");
		if (!tokens.contains("interface") && !tokens.contains("enum")
				&& !tokens.contains("annotation"))
			tokens.add("class");
		if (tokens.size() == 0)
			return "[Error parsing]";

		// hackery delimeters
		StringBuilder sb = new StringBuilder(tokens.get(0));
		for (int i = 1; i < tokens.size(); i++) {
			sb.append(" ");
			sb.append(tokens.get(i));
		}
		return sb.toString();
	}

	@Override public void decompileToZip(String zipName) { }
	@Override public void decompileToClass(String className, String classNameSaved) { }
}