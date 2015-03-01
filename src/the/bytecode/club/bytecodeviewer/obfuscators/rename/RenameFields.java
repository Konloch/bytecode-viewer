package the.bytecode.club.bytecodeviewer.obfuscators.rename;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.obfuscators.JavaObfuscator;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.FieldMappingData;
import the.bytecode.club.bytecodeviewer.obfuscators.mapping.data.MappingData;

/**
 * Rename fields.
 * 
 * @author Konloch
 *
 */

public class RenameFields extends JavaObfuscator {

	@Override
	public void obfuscate() {
		int stringLength = getStringLength();

		System.out.println("Obfuscating fields names...");
		for (ClassNode c : BytecodeViewer.getLoadedClasses()) {
			for (Object o : c.fields.toArray()) {
				FieldNode f = (FieldNode) o;
				
				String newName = generateUniqueName(stringLength);
				
				BytecodeViewer.refactorer.getHooks().addField(new FieldMappingData(c.name, new MappingData(f.name, newName), f.desc));
				
				/*ASMUtil_OLD.renameFieldNode(c.name, f.name, f.desc, null, newName, null);
				f.name = newName;*/
			}
		}

		System.out.println("Obfuscated field names.");
	}

}
