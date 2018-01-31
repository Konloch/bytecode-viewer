/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

/**
 * A node that represents a class.
 *
 * @author Eric Bruneton
 */
public class ClassNode extends ClassVisitor {

    /**
     * The class version.
     */
    public int version;

    /**
     * The class's access flags (see {@link org.objectweb.asm.Opcodes}). This
     * field also indicates if the class is deprecated.
     */
    public int access;

    /**
     * The internal name of the class (see
     * {@link org.objectweb.asm.Type#getInternalName() getInternalName}).
     */
    public String name;

    /**
     * The signature of the class. May be <tt>null</tt>.
     */
    public String signature;

    /**
     * The internal of name of the super class (see
     * {@link org.objectweb.asm.Type#getInternalName() getInternalName}). For
     * interfaces, the super class is {@link Object}. May be <tt>null</tt>, but
     * only for the {@link Object} class.
     */
    public String superName;

    /**
     * The internal names of the class's interfaces (see
     * {@link org.objectweb.asm.Type#getInternalName() getInternalName}). This
     * list is a list of {@link String} objects.
     */
    public List<String> interfaces;

    /**
     * The name of the source file from which this class was compiled. May be
     * <tt>null</tt>.
     */
    public String sourceFile;

    /**
     * Debug information to compute the correspondence between source and
     * compiled elements of the class. May be <tt>null</tt>.
     */
    public String sourceDebug;

    /**
     * The internal name of the enclosing class of the class. May be
     * <tt>null</tt>.
     */
    public String outerClass;

    /**
     * The name of the method that contains the class, or <tt>null</tt> if the
     * class is not enclosed in a method.
     */
    public String outerMethod;

    /**
     * The descriptor of the method that contains the class, or <tt>null</tt> if
     * the class is not enclosed in a method.
     */
    public String outerMethodDesc;

    /**
     * The runtime visible annotations of this class. This list is a list of
     * {@link AnnotationNode} objects. May be <tt>null</tt>.
     *
     * @associates org.objectweb.asm.tree.AnnotationNode
     * @label visible
     */
    public List<AnnotationNode> visibleAnnotations;

    /**
     * The runtime invisible annotations of this class. This list is a list of
     * {@link AnnotationNode} objects. May be <tt>null</tt>.
     *
     * @associates org.objectweb.asm.tree.AnnotationNode
     * @label invisible
     */
    public List<AnnotationNode> invisibleAnnotations;

    /**
     * The runtime visible type annotations of this class. This list is a list
     * of {@link TypeAnnotationNode} objects. May be <tt>null</tt>.
     *
     * @associates org.objectweb.asm.tree.TypeAnnotationNode
     * @label visible
     */
    public List<TypeAnnotationNode> visibleTypeAnnotations;

    /**
     * The runtime invisible type annotations of this class. This list is a list
     * of {@link TypeAnnotationNode} objects. May be <tt>null</tt>.
     *
     * @associates org.objectweb.asm.tree.TypeAnnotationNode
     * @label invisible
     */
    public List<TypeAnnotationNode> invisibleTypeAnnotations;

    /**
     * The non standard attributes of this class. This list is a list of
     * {@link Attribute} objects. May be <tt>null</tt>.
     *
     * @associates org.objectweb.asm.Attribute
     */
    public List<Attribute> attrs;

    /**
     * Informations about the inner classes of this class. This list is a list
     * of {@link InnerClassNode} objects.
     *
     * @associates org.objectweb.asm.tree.InnerClassNode
     */
    public List<InnerClassNode> innerClasses;

    /**
     * The fields of this class. This list is a list of {@link FieldNode}
     * objects.
     *
     * @associates org.objectweb.asm.tree.FieldNode
     */
    public List<FieldNode> fields;

    /**
     * The methods of this class. This list is a list of {@link MethodNode}
     * objects.
     *
     * @associates org.objectweb.asm.tree.MethodNode
     */
    public List<MethodNode> methods;

    public Set<String> references;

    /**
     * Constructs a new {@link ClassNode}. <i>Subclasses must not use this
     * constructor</i>. Instead, they must use the {@link #ClassNode(int)}
     * version.
     *
     * @throws IllegalStateException If a subclass calls this constructor.
     */
    public ClassNode() {
        this(Opcodes.ASM5);
        if (getClass() != ClassNode.class) {
            throw new IllegalStateException();
        }
    }

    /**
     * Constructs a new {@link ClassNode}.
     *
     * @param api the ASM API version implemented by this visitor. Must be one
     *            of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     */
    public ClassNode(final int api) {
        super(api);
        this.interfaces = new ArrayList<String>();
        this.innerClasses = new ArrayList<InnerClassNode>();
        this.fields = new ArrayList<FieldNode>();
        this.methods = new ArrayList<MethodNode>();
        references = new HashSet<String>();
    }

    // ------------------------------------------------------------------------
    // Implementation of the ClassVisitor abstract class
    // ------------------------------------------------------------------------

    @Override
    public void visit(final int version, final int access, final String name,
                      final String signature, final String superName,
                      final String[] interfaces) {
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        if (interfaces != null) {
            this.interfaces.addAll(Arrays.asList(interfaces));
        }
    }

    @Override
    public void visitSource(final String file, final String debug) {
        sourceFile = file;
        sourceDebug = debug;
    }

    @Override
    public void visitOuterClass(final String owner, final String name,
                                final String desc) {
        outerClass = owner;
        outerMethod = name;
        outerMethodDesc = desc;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc,
                                             final boolean visible) {
        AnnotationNode an = new AnnotationNode(desc);
        if (visible) {
            if (visibleAnnotations == null) {
                visibleAnnotations = new ArrayList<AnnotationNode>(1);
            }
            visibleAnnotations.add(an);
        } else {
            if (invisibleAnnotations == null) {
                invisibleAnnotations = new ArrayList<AnnotationNode>(1);
            }
            invisibleAnnotations.add(an);
        }
        return an;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef,
                                                 TypePath typePath, String desc, boolean visible) {
        TypeAnnotationNode an = new TypeAnnotationNode(typeRef, typePath, desc);
        if (visible) {
            if (visibleTypeAnnotations == null) {
                visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            visibleTypeAnnotations.add(an);
        } else {
            if (invisibleTypeAnnotations == null) {
                invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            invisibleTypeAnnotations.add(an);
        }
        return an;
    }

    @Override
    public void visitAttribute(final Attribute attr) {
        if (attrs == null) {
            attrs = new ArrayList<Attribute>(1);
        }
        attrs.add(attr);
    }

    @Override
    public void visitInnerClass(final String name, final String outerName,
                                final String innerName, final int access) {
        InnerClassNode icn = new InnerClassNode(name, outerName, innerName,
                access);
        innerClasses.add(icn);
    }

    @Override
    public FieldVisitor visitField(final int access, final String name,
                                   final String desc, final String signature, final Object value) {
        FieldNode fn = new FieldNode(this, access, name, desc, signature, value);
        fields.add(fn);
        return fn;
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name,
                                     final String desc, final String signature, final String[] exceptions) {
        MethodNode mn = new MethodNode(this, access, name, desc, signature,
                exceptions);
        methods.add(mn);
        return mn;
    }

    @Override
    public void visitEnd() {
    }

    // ------------------------------------------------------------------------
    // Accept method
    // ------------------------------------------------------------------------

    /**
     * Checks that this class node is compatible with the given ASM API version.
     * This methods checks that this node, and all its nodes recursively, do not
     * contain elements that were introduced in more recent versions of the ASM
     * API than the given version.
     *
     * @param api an ASM API version. Must be one of {@link Opcodes#ASM4} or
     *            {@link Opcodes#ASM5}.
     */
    public void check(final int api) {
        if (api == Opcodes.ASM4) {
            if (visibleTypeAnnotations != null
                    && visibleTypeAnnotations.size() > 0) {
                throw new RuntimeException();
            }
            if (invisibleTypeAnnotations != null
                    && invisibleTypeAnnotations.size() > 0) {
                throw new RuntimeException();
            }
            for (FieldNode f : fields) {
                f.check(api);
            }
            for (MethodNode m : methods) {
                m.check(api);
            }
        }
    }

    /**
     * Makes the given class visitor visit this class.
     *
     * @param cv a class visitor.
     */
    public void accept(final ClassVisitor cv) {
        // visits header
        String[] interfaces = new String[this.interfaces.size()];
        this.interfaces.toArray(interfaces);
        cv.visit(version, access, name, signature, superName, interfaces);
        // visits source
        if (sourceFile != null || sourceDebug != null) {
            cv.visitSource(sourceFile, sourceDebug);
        }
        // visits outer class
        if (outerClass != null) {
            cv.visitOuterClass(outerClass, outerMethod, outerMethodDesc);
        }
        // visits attributes
        int i, n;
        n = visibleAnnotations == null ? 0 : visibleAnnotations.size();
        for (i = 0; i < n; ++i) {
            AnnotationNode an = visibleAnnotations.get(i);
            an.accept(cv.visitAnnotation(an.desc, true));
        }
        n = invisibleAnnotations == null ? 0 : invisibleAnnotations.size();
        for (i = 0; i < n; ++i) {
            AnnotationNode an = invisibleAnnotations.get(i);
            an.accept(cv.visitAnnotation(an.desc, false));
        }
        n = visibleTypeAnnotations == null ? 0 : visibleTypeAnnotations.size();
        for (i = 0; i < n; ++i) {
            TypeAnnotationNode an = visibleTypeAnnotations.get(i);
            an.accept(cv.visitTypeAnnotation(an.typeRef, an.typePath, an.desc,
                    true));
        }
        n = invisibleTypeAnnotations == null ? 0 : invisibleTypeAnnotations
                .size();
        for (i = 0; i < n; ++i) {
            TypeAnnotationNode an = invisibleTypeAnnotations.get(i);
            an.accept(cv.visitTypeAnnotation(an.typeRef, an.typePath, an.desc,
                    false));
        }
        n = attrs == null ? 0 : attrs.size();
        for (i = 0; i < n; ++i) {
            cv.visitAttribute(attrs.get(i));
        }
        // visits inner classes
        for (i = 0; i < innerClasses.size(); ++i) {
            innerClasses.get(i).accept(cv);
        }
        // visits fields
        for (i = 0; i < fields.size(); ++i) {
            fields.get(i).accept(cv);
        }
        // visits methods
        for (i = 0; i < methods.size(); ++i) {
            methods.get(i).accept(cv);
        }
        // visits end
        cv.visitEnd();
    }

    public List<String> constructors() {
        List<String> constructors = new ArrayList<>();
        for (MethodNode mn : methods) {
            if (mn.name.equals("<init>"))
                constructors.add(mn.desc);
        }
        return constructors;
    }

    public MethodNode getMethodByName(String name) {
        for (MethodNode mn : methods) {
            if (mn.name.equals(name))
                return mn;
        }
        return null;
    }

    public FieldNode getField(String field, String desc, boolean ignoreStatic) {
        for (FieldNode fn : fields) {
            if (ignoreStatic && (fn.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC)
                continue;
            if ((field == null || fn.name.equals(field)) && (desc == null || desc.equals(fn.desc)))
                return fn;
        }
        return null;
    }

    public FieldNode getField(String field, String desc) {
        return getField(field, desc, true);
    }

    public FieldNode getPublicField(String field, String desc, boolean ignoreStatic) {
        for (FieldNode fn : fields) {
            if ((fn.access & Opcodes.ACC_PUBLIC) != Opcodes.ACC_PUBLIC)
                continue;
            if (ignoreStatic && (fn.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC)
                continue;
            if ((field == null || fn.name.equals(field)) && (desc == null || desc.equals(fn.desc)))
                return fn;
        }
        return null;
    }

    public FieldNode getPublicField(String field, String desc) {
        return getPublicField(field, desc, true);
    }

    public MethodNode getMethod(String method, String desc) {
        for (MethodNode mn : methods) {
            if (mn.name.equals(method) && (desc == null || desc.equals(mn.desc)))
                return mn;
        }
        return null;
    }

    public MethodNode getMethod(String desc) {
        for (MethodNode mn : methods) {
            if (desc.endsWith(mn.desc))
                return mn;
        }
        return null;
    }

    public int methodCount(String desc, boolean ignoreStatic) {
        int count = 0;
        for (MethodNode mn : methods) {
            if (ignoreStatic && (mn.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
                continue;
            }
            if (mn.desc.equals(desc)) {
                count++;
            }
        }
        return count;
    }

    public int methodCount(String desc) {
        return methodCount(desc, true);
    }

    public int fieldCount(String desc, boolean ignoreStatic) {
        int count = 0;
        for (FieldNode fn : fields) {
            if (ignoreStatic && (fn.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC)
                continue;
            if (fn.desc.equals(desc))
                count++;
        }
        return count;
    }

    public int fieldCount(String desc) {
        return fieldCount(desc, true);
    }

    public int getAbnormalFieldCount(boolean ignoreStatic) {
        int count = 0;
        for (FieldNode fn : fields) {
            if (ignoreStatic && (fn.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC)
                continue;
            if (fn.desc.contains("L") && fn.desc.endsWith(";") && !fn.desc.contains("java"))
                count++;
        }
        return count;
    }

    public int getAbnormalFieldCount() {
        return getAbnormalFieldCount(true);
    }

    public int getFieldTypeCount(boolean ignoreStatic) {
        List<String> types = new ArrayList<>();
        for (FieldNode fn : fields) {
            if (ignoreStatic && (fn.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC)
                continue;
            if (!types.contains(fn.desc))
                types.add(fn.desc);
        }
        return types.size();
    }

    public int getFieldTypeCount() {
        return getFieldTypeCount(true);
    }

    public boolean ownerless() {
        return superName.equals("java/lang/Object");
    }

    @Override
    public String toString() {
        return name;
    }
}