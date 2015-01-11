package the.bytecode.club.bytecodeviewer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class CoolClassAdapter extends ClassAdapter {
	
	String oldName;
	String newName;
	
	public CoolClassAdapter(ClassVisitor cv, String oldName, String newName) {
		super(cv);
		this.oldName = oldName;
		this.newName = newName;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if(name != null)
			name = name.replace(oldName, newName);
		if(signature != null)
			signature = signature.replace(oldName, newName);
		if(superName != null)
			superName = superName.replace(oldName, newName);
		if(interfaces != null)
			for(int i = 0; i < interfaces.length; i++)
				interfaces[i] = interfaces[i].replace(oldName, newName);
		
		cv.visit(49, access, name, signature, superName, interfaces);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if(desc != null)
			desc = desc.replace(oldName, newName);
		return cv.visitAnnotation(desc, visible);
	}

	@Override
	public void visitAttribute(Attribute attr) {
		
	}

	@Override
	public void visitEnd() {
		
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if(name != null)
			name = name.replace(oldName, newName);
		if(desc != null)
			desc = desc.replace(oldName, newName);
		if(signature != null)
			signature = signature.replace(oldName, newName);
		
		return cv.visitField(access, name, desc, signature, value);
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		if(name != null)
			name = name.replace(oldName, newName);
		if(outerName != null)
			outerName = outerName.replace(oldName, newName);
		if(innerName != null)
			innerName = innerName.replace(oldName, newName);
		
		cv.visitInnerClass(name, outerName, innerName, access);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if(name != null)
			name = name.replace(oldName, newName);
		if(desc != null)
			desc = desc.replace(oldName, newName);
		if(signature != null)
			signature = signature.replace(oldName, newName);
		
		if(exceptions != null)
			for(int i = 0; i < exceptions.length; i++)
				exceptions[i] = exceptions[i].replace(oldName, newName);
		
		//return cv.visitMethod(access, name, desc, signature, exceptions);
		return null;
	}

	@Override
	public void visitOuterClass(String owner, String name, String desc) {
		if(owner != null)
			owner = owner.replace(oldName, newName);
		if(name != null)
			name = name.replace(oldName, newName);
		if(desc != null)
			desc = desc.replace(oldName, newName);
		
		cv.visitOuterClass(owner, name, desc);
	}

	@Override
	public void visitSource(String source, String debug) {
		if(source != null)
			source = source.replace(oldName, newName);
		cv.visitSource(source, debug);
	}

}
