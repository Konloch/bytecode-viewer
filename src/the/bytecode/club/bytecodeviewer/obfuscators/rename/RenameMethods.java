package the.bytecode.club.bytecodeviewer.obfuscators.rename;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.ASMUtil_OLD;
import the.bytecode.club.bytecodeviewer.obfuscators.JavaObfuscator;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.MappingData;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.MethodMappingData;

/**
 * Rename methods.
 * 
 * @author Konloch
 *
 */

public class RenameMethods extends JavaObfuscator {

	@Override
	public void obfuscate() {
		int stringLength = getStringLength();

		System.out.println("Obfuscating method names...");
		for (ClassNode c : BytecodeViewer.getLoadedClasses()) {
			methodLoop: for (Object o : c.methods.toArray()) {
				MethodNode m = (MethodNode) o;
				
				/* As we dont want to rename native dll methods */
				if ((m.access & Opcodes.ACC_NATIVE) != 0)
					continue methodLoop;
				
				if (m.access != Opcodes.ACC_ABSTRACT
						&& m.access != Opcodes.ACC_ABSTRACT
								+ Opcodes.ACC_STATIC
						&& m.access != Opcodes.ACC_ABSTRACT
								+ Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC
						&& m.access != Opcodes.ACC_ABSTRACT
								+ Opcodes.ACC_STATIC + Opcodes.ACC_PRIVATE
						&& m.access != Opcodes.ACC_ABSTRACT
								+ Opcodes.ACC_STATIC + Opcodes.ACC_PROTECTED
						&& m.access != Opcodes.ACC_ABSTRACT
								+ Opcodes.ACC_PUBLIC
						&& m.access != Opcodes.ACC_ABSTRACT
								+ Opcodes.ACC_PRIVATE
						&& m.access != Opcodes.ACC_ABSTRACT
								+ Opcodes.ACC_PROTECTED) {
					if (!m.name.equals("main") && !m.name.equals("<init>")
							&& !m.name.equals("<clinit>")) {
						String newName = generateUniqueName(stringLength);
						
						BytecodeViewer.refactorer.getHooks().addMethod(new MethodMappingData(c.name, new MappingData(m.name, newName), m.desc));
						
						/*ASMUtil_OLD.renameMethodNode(c.name, m.name, m.desc,
								null, newName, null);*/
					}
				}
			}
		}

		System.out.println("Obfuscated method names.");
	}

}
