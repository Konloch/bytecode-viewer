package the.bytecode.club.bytecodeviewer.obfuscators.rename;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.api.ASMUtil_OLD;
import the.bytecode.club.bytecodeviewer.obfuscators.JavaObfuscator;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.MappingData;

/**
 * Rename classes.
 * 
 * @author Konloch
 *
 */

public class RenameClasses extends JavaObfuscator {

	@Override
	public void obfuscate() {
		int stringLength = getStringLength();

		System.out.println("Obfuscating class names...");
		classLoop: for (ClassNode c : BytecodeViewer.getLoadedClasses()) {
			
			/** As we dont want to rename classes that contain native dll methods */
			for (Object o : c.methods) {
				MethodNode m = (MethodNode) o;
				/* As we dont want to rename native dll methods */
				if ((m.access & Opcodes.ACC_NATIVE) != 0)
					continue classLoop;
			}
			
			String newName = generateUniqueName(stringLength);
			
			BytecodeViewer.refactorer.getHooks().addClass(new MappingData(c.name, newName));
			
			ASMUtil_OLD.renameClassNode(c.name, newName);
			c.name = newName;
		}

		System.out.println("Obfuscated class names.");
	}

}
