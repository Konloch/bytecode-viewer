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
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.commons.cfg.query.InsnQuery;

/**
 * A node that represents a method.
 * 
 * @author Eric Bruneton
 */
public class MethodNode extends MethodVisitor {

	public ClassNode owner;
	
    /**
     * The method's access flags (see {@link Opcodes}). This field also
     * indicates if the method is synthetic and/or deprecated.
     */
    public int access;

    /**
     * The method's name.
     */
    public String name;

    /**
     * The method's descriptor (see {@link Type}).
     */
    public String desc;

    /**
     * The method's signature. May be <tt>null</tt>.
     */
    public String signature;

    /**
     * The internal names of the method's exception classes (see
     * {@link Type#getInternalName() getInternalName}). This list is a list of
     * {@link String} objects.
     */
    public List<String> exceptions;

    /**
     * The method parameter info (access flags and name)
     */
    public List<ParameterNode> parameters;

    /**
     * The runtime visible annotations of this method. This list is a list of
     * {@link AnnotationNode} objects. May be <tt>null</tt>.
     * 
     * @associates org.objectweb.asm.tree.AnnotationNode
     * @label visible
     */
    public List<AnnotationNode> visibleAnnotations;

    /**
     * The runtime invisible annotations of this method. This list is a list of
     * {@link AnnotationNode} objects. May be <tt>null</tt>.
     * 
     * @associates org.objectweb.asm.tree.AnnotationNode
     * @label invisible
     */
    public List<AnnotationNode> invisibleAnnotations;

    /**
     * The runtime visible type annotations of this method. This list is a list
     * of {@link TypeAnnotationNode} objects. May be <tt>null</tt>.
     * 
     * @associates org.objectweb.asm.tree.TypeAnnotationNode
     * @label visible
     */
    public List<TypeAnnotationNode> visibleTypeAnnotations;

    /**
     * The runtime invisible type annotations of this method. This list is a
     * list of {@link TypeAnnotationNode} objects. May be <tt>null</tt>.
     * 
     * @associates org.objectweb.asm.tree.TypeAnnotationNode
     * @label invisible
     */
    public List<TypeAnnotationNode> invisibleTypeAnnotations;

    /**
     * The non standard attributes of this method. This list is a list of
     * {@link Attribute} objects. May be <tt>null</tt>.
     * 
     * @associates org.objectweb.asm.Attribute
     */
    public List<Attribute> attrs;

    /**
     * The default value of this annotation interface method. This field must be
     * a {@link Byte}, {@link Boolean}, {@link Character}, {@link Short},
     * {@link Integer}, {@link Long}, {@link Float}, {@link Double},
     * {@link String} or {@link Type}, or an two elements String array (for
     * enumeration values), a {@link AnnotationNode}, or a {@link List} of
     * values of one of the preceding types. May be <tt>null</tt>.
     */
    public Object annotationDefault;

    /**
     * The runtime visible parameter annotations of this method. These lists are
     * lists of {@link AnnotationNode} objects. May be <tt>null</tt>.
     * 
     * @associates org.objectweb.asm.tree.AnnotationNode
     * @label invisible parameters
     */
    public List<AnnotationNode>[] visibleParameterAnnotations;

    /**
     * The runtime invisible parameter annotations of this method. These lists
     * are lists of {@link AnnotationNode} objects. May be <tt>null</tt>.
     * 
     * @associates org.objectweb.asm.tree.AnnotationNode
     * @label visible parameters
     */
    public List<AnnotationNode>[] invisibleParameterAnnotations;

    /**
     * The instructions of this method. This list is a list of
     * {@link AbstractInsnNode} objects.
     * 
     * @associates org.objectweb.asm.tree.AbstractInsnNode
     * @label instructions
     */
    public InsnList instructions;

    /**
     * The try catch blocks of this method. This list is a list of
     * {@link TryCatchBlockNode} objects.
     * 
     * @associates org.objectweb.asm.tree.TryCatchBlockNode
     */
    public List<TryCatchBlockNode> tryCatchBlocks;

    /**
     * The maximum stack size of this method.
     */
    public int maxStack;

    /**
     * The maximum number of local variables of this method.
     */
    public int maxLocals;

    /**
     * The local variables of this method. This list is a list of
     * {@link LocalVariableNode} objects. May be <tt>null</tt>
     * 
     * @associates org.objectweb.asm.tree.LocalVariableNode
     */
    public List<LocalVariableNode> localVariables;

    /**
     * The visible local variable annotations of this method. This list is a
     * list of {@link LocalVariableAnnotationNode} objects. May be <tt>null</tt>
     * 
     * @associates org.objectweb.asm.tree.LocalVariableAnnotationNode
     */
    public List<LocalVariableAnnotationNode> visibleLocalVariableAnnotations;

    /**
     * The invisible local variable annotations of this method. This list is a
     * list of {@link LocalVariableAnnotationNode} objects. May be <tt>null</tt>
     * 
     * @associates org.objectweb.asm.tree.LocalVariableAnnotationNode
     */
    public List<LocalVariableAnnotationNode> invisibleLocalVariableAnnotations;

    /**
     * If the accept method has been called on this object.
     */
    private boolean visited;

    public Handle handle;
    
    private String cachedKey;
    
    /**
     * Constructs an uninitialized {@link MethodNode}. <i>Subclasses must not
     * use this constructor</i>. Instead, they must use the
     * {@link #MethodNode(int)} version.
     * 
     * @throws IllegalStateException
     *             If a subclass calls this constructor.
     */
    public MethodNode(ClassNode owner) {
        this(Opcodes.ASM5, owner);
        if (getClass() != MethodNode.class) {
            throw new IllegalStateException();
        }
    }

    /**
     * Constructs an uninitialized {@link MethodNode}.
     * 
     * @param api
     *            the ASM API version implemented by this visitor. Must be one
     *            of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     */
    public MethodNode(final int api, final ClassNode owner) {
        super(api);
        this.owner = owner;
        this.instructions = new InsnList();
        if(this.owner != null)
            this.handle = new Handle(0, owner.name, name, desc);
    }

    /**
     * Constructs a new {@link MethodNode}. <i>Subclasses must not use this
     * constructor</i>. Instead, they must use the
     * {@link #MethodNode(int, int, String, String, String, String[])} version.
     * 
     * @param access
     *            the method's access flags (see {@link Opcodes}). This
     *            parameter also indicates if the method is synthetic and/or
     *            deprecated.
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type}).
     * @param signature
     *            the method's signature. May be <tt>null</tt>.
     * @param exceptions
     *            the internal names of the method's exception classes (see
     *            {@link Type#getInternalName() getInternalName}). May be
     *            <tt>null</tt>.
     * @throws IllegalStateException
     *             If a subclass calls this constructor.
     */
    public MethodNode(final ClassNode owner, final int access, final String name, final String desc,
            final String signature, final String[] exceptions) {
        this(Opcodes.ASM5, owner, access, name, desc, signature, exceptions);
        if (getClass() != MethodNode.class) {
            throw new IllegalStateException();
        }
    }

    /**
     * Constructs a new {@link MethodNode}.
     * 
     * @param api
     *            the ASM API version implemented by this visitor. Must be one
     *            of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     * @param access
     *            the method's access flags (see {@link Opcodes}). This
     *            parameter also indicates if the method is synthetic and/or
     *            deprecated.
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type}).
     * @param signature
     *            the method's signature. May be <tt>null</tt>.
     * @param exceptions
     *            the internal names of the method's exception classes (see
     *            {@link Type#getInternalName() getInternalName}). May be
     *            <tt>null</tt>.
     */
    public MethodNode(final int api, final ClassNode owner, final int access, final String name,
            final String desc, final String signature, final String[] exceptions) {
        super(api);
        this.owner = owner;
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = new ArrayList<String>(exceptions == null ? 0
                : exceptions.length);
        boolean isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;
        if (!isAbstract) {
            this.localVariables = new ArrayList<LocalVariableNode>(5);
        }
        this.tryCatchBlocks = new ArrayList<TryCatchBlockNode>();
        if (exceptions != null) {
            this.exceptions.addAll(Arrays.asList(exceptions));
        }
        this.instructions = new InsnList();
        if(this.owner != null)
            this.handle = new Handle(0, owner.name, name, desc);
    }

    // ------------------------------------------------------------------------
    // Implementation of the MethodVisitor abstract class
    // ------------------------------------------------------------------------

    @Override
    public void visitParameter(String name, int access) {
        if (parameters == null) {
            parameters = new ArrayList<ParameterNode>(5);
        }
        parameters.add(new ParameterNode(name, access));
    }

    @Override
    @SuppressWarnings("serial")
    public AnnotationVisitor visitAnnotationDefault() {
        return new AnnotationNode(new ArrayList<Object>(0) {
            @Override
            public boolean add(final Object o) {
                annotationDefault = o;
                return super.add(o);
            }
        });
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
    @SuppressWarnings("unchecked")
    public AnnotationVisitor visitParameterAnnotation(final int parameter,
            final String desc, final boolean visible) {
        AnnotationNode an = new AnnotationNode(desc);
        if (visible) {
            if (visibleParameterAnnotations == null) {
                int params = Type.getArgumentTypes(this.desc).length;
                visibleParameterAnnotations = (List<AnnotationNode>[]) new List<?>[params];
            }
            if (visibleParameterAnnotations[parameter] == null) {
                visibleParameterAnnotations[parameter] = new ArrayList<AnnotationNode>(
                        1);
            }
            visibleParameterAnnotations[parameter].add(an);
        } else {
            if (invisibleParameterAnnotations == null) {
                int params = Type.getArgumentTypes(this.desc).length;
                invisibleParameterAnnotations = (List<AnnotationNode>[]) new List<?>[params];
            }
            if (invisibleParameterAnnotations[parameter] == null) {
                invisibleParameterAnnotations[parameter] = new ArrayList<AnnotationNode>(
                        1);
            }
            invisibleParameterAnnotations[parameter].add(an);
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
    public void visitCode() {
    }

    @Override
    public void visitFrame(final int type, final int nLocal,
            final Object[] local, final int nStack, final Object[] stack) {
        instructions.add(new FrameNode(type, nLocal, local == null ? null
                : getLabelNodes(local), nStack, stack == null ? null
                : getLabelNodes(stack)));
    }

    @Override
    public void visitInsn(final int opcode) {
    	InsnNode in = new InsnNode(opcode);
    	in.method = this;
        instructions.add(in);
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
    	IntInsnNode iin = new IntInsnNode(opcode, operand);
    	iin.method = this;
        instructions.add(iin);
    }

    @Override
    public void visitVarInsn(final int opcode, final int var) {
    	VarInsnNode vin = new VarInsnNode(opcode, var);
    	vin.method = this;
        instructions.add(vin);
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
    	TypeInsnNode tin = new TypeInsnNode(opcode, type);
    	tin.method = this;
        instructions.add(tin);
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner,
            final String name, final String desc) {
    	FieldInsnNode fin = new FieldInsnNode(opcode, owner, name, desc);
    	fin.method = this;
        instructions.add(fin);
    }

    @Deprecated
    @Override
    public void visitMethodInsn(int opcode, String owner, String name,
            String desc) {
        if (api >= Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        MethodInsnNode min = new MethodInsnNode(opcode, owner, name, desc);
        min.method = this;
        this.owner.references.add(min.key());
        instructions.add(min);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name,
            String desc, boolean itf) {
        if (api < Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        MethodInsnNode min = new MethodInsnNode(opcode, owner, name, desc, itf);
        min.method = this;
        this.owner.references.add(min.key());
        instructions.add(min);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm,
            Object... bsmArgs) {
    	InvokeDynamicInsnNode d = new InvokeDynamicInsnNode(name, desc, bsm, bsmArgs);
    	d.method = this;
        instructions.add(d);
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
    	JumpInsnNode jin = new JumpInsnNode(opcode, getLabelNode(label));
    	jin.method = this;
        instructions.add(jin);
    }

    @Override
    public void visitLabel(final Label label) {
        instructions.add(getLabelNode(label));
    }

    @Override
    public void visitLdcInsn(final Object cst) {
    	LdcInsnNode ldc = new LdcInsnNode(cst);
    	ldc.method = this;
        instructions.add(ldc);
    }

    @Override
    public void visitIincInsn(final int var, final int increment) {
    	IincInsnNode in = new IincInsnNode(var, increment);
    	in.method = this;
        instructions.add(in);
    }

    @Override
    public void visitTableSwitchInsn(final int min, final int max,
            final Label dflt, final Label... labels) {
    	TableSwitchInsnNode in = new TableSwitchInsnNode(min, max, getLabelNode(dflt),
                getLabelNodes(labels));
    	in.method = this;
        instructions.add(in);
    }

    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys,
            final Label[] labels) {
    	LookupSwitchInsnNode in = new LookupSwitchInsnNode(getLabelNode(dflt), keys,
                getLabelNodes(labels));
    	in.method = this;
        instructions.add(in);
    }

    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
       MultiANewArrayInsnNode in = new MultiANewArrayInsnNode(desc, dims);
       in.method = this;
       instructions.add(in);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible) {
        // Finds the last real instruction, i.e. the instruction targeted by
        // this annotation.
        AbstractInsnNode insn = instructions.getLast();
        while (insn.opcode() == -1) {
            insn = insn.getPrevious();
        }
        // Adds the annotation to this instruction.
        TypeAnnotationNode an = new TypeAnnotationNode(typeRef, typePath, desc);
        if (visible) {
            if (insn.visibleTypeAnnotations == null) {
                insn.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(
                        1);
            }
            insn.visibleTypeAnnotations.add(an);
        } else {
            if (insn.invisibleTypeAnnotations == null) {
                insn.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(
                        1);
            }
            insn.invisibleTypeAnnotations.add(an);
        }
        return an;
    }

    @Override
    public void visitTryCatchBlock(final Label start, final Label end,
            final Label handler, final String type) {
        tryCatchBlocks.add(new TryCatchBlockNode(getLabelNode(start),
                getLabelNode(end), getLabelNode(handler), type));
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible) {
        TryCatchBlockNode tcb = tryCatchBlocks.get((typeRef & 0x00FFFF00) >> 8);
        TypeAnnotationNode an = new TypeAnnotationNode(typeRef, typePath, desc);
        if (visible) {
            if (tcb.visibleTypeAnnotations == null) {
                tcb.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(
                        1);
            }
            tcb.visibleTypeAnnotations.add(an);
        } else {
            if (tcb.invisibleTypeAnnotations == null) {
                tcb.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(
                        1);
            }
            tcb.invisibleTypeAnnotations.add(an);
        }
        return an;
    }

    @Override
    public void visitLocalVariable(final String name, final String desc,
            final String signature, final Label start, final Label end,
            final int index) {
        localVariables.add(new LocalVariableNode(name, desc, signature,
                getLabelNode(start), getLabelNode(end), index));
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef,
            TypePath typePath, Label[] start, Label[] end, int[] index,
            String desc, boolean visible) {
        LocalVariableAnnotationNode an = new LocalVariableAnnotationNode(
                typeRef, typePath, getLabelNodes(start), getLabelNodes(end),
                index, desc);
        if (visible) {
            if (visibleLocalVariableAnnotations == null) {
                visibleLocalVariableAnnotations = new ArrayList<LocalVariableAnnotationNode>(
                        1);
            }
            visibleLocalVariableAnnotations.add(an);
        } else {
            if (invisibleLocalVariableAnnotations == null) {
                invisibleLocalVariableAnnotations = new ArrayList<LocalVariableAnnotationNode>(
                        1);
            }
            invisibleLocalVariableAnnotations.add(an);
        }
        return an;
    }

    @Override
    public void visitLineNumber(final int line, final Label start) {
    	LineNumberNode lin = new LineNumberNode(line, getLabelNode(start));
        lin.method = this;
    	instructions.add(lin);
    }

    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
    }

    @Override
    public void visitEnd() {
    }

    /**
     * Returns the LabelNode corresponding to the given Label. Creates a new
     * LabelNode if necessary. The default implementation of this method uses
     * the {@link Label#info} field to store associations between labels and
     * label nodes.
     * 
     * @param l
     *            a Label.
     * @return the LabelNode corresponding to l.
     */
    protected LabelNode getLabelNode(final Label l) {
        if (!(l.info instanceof LabelNode)) {
            l.info = new LabelNode();
        }
        return (LabelNode) l.info;
    }

    private LabelNode[] getLabelNodes(final Label[] l) {
        LabelNode[] nodes = new LabelNode[l.length];
        for (int i = 0; i < l.length; ++i) {
            nodes[i] = getLabelNode(l[i]);
        }
        return nodes;
    }

    private Object[] getLabelNodes(final Object[] objs) {
        Object[] nodes = new Object[objs.length];
        for (int i = 0; i < objs.length; ++i) {
            Object o = objs[i];
            if (o instanceof Label) {
                o = getLabelNode((Label) o);
            }
            nodes[i] = o;
        }
        return nodes;
    }

    // ------------------------------------------------------------------------
    // Accept method
    // ------------------------------------------------------------------------

    /**
     * Checks that this method node is compatible with the given ASM API
     * version. This methods checks that this node, and all its nodes
     * recursively, do not contain elements that were introduced in more recent
     * versions of the ASM API than the given version.
     * 
     * @param api
     *            an ASM API version. Must be one of {@link Opcodes#ASM4} or
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
            int n = tryCatchBlocks == null ? 0 : tryCatchBlocks.size();
            for (int i = 0; i < n; ++i) {
                TryCatchBlockNode tcb = tryCatchBlocks.get(i);
                if (tcb.visibleTypeAnnotations != null
                        && tcb.visibleTypeAnnotations.size() > 0) {
                    throw new RuntimeException();
                }
                if (tcb.invisibleTypeAnnotations != null
                        && tcb.invisibleTypeAnnotations.size() > 0) {
                    throw new RuntimeException();
                }
            }
            for (int i = 0; i < instructions.size(); ++i) {
                AbstractInsnNode insn = instructions.get(i);
                if (insn.visibleTypeAnnotations != null
                        && insn.visibleTypeAnnotations.size() > 0) {
                    throw new RuntimeException();
                }
                if (insn.invisibleTypeAnnotations != null
                        && insn.invisibleTypeAnnotations.size() > 0) {
                    throw new RuntimeException();
                }
                if (insn instanceof MethodInsnNode) {
                    boolean itf = ((MethodInsnNode) insn).itf;
                    if (itf != (insn.opcode == Opcodes.INVOKEINTERFACE)) {
                        throw new RuntimeException();
                    }
                }
            }
            if (visibleLocalVariableAnnotations != null
                    && visibleLocalVariableAnnotations.size() > 0) {
                throw new RuntimeException();
            }
            if (invisibleLocalVariableAnnotations != null
                    && invisibleLocalVariableAnnotations.size() > 0) {
                throw new RuntimeException();
            }
        }
    }

    /**
     * Makes the given class visitor visit this method.
     * 
     * @param cv
     *            a class visitor.
     */
    public void accept(final ClassVisitor cv) {
        String[] exceptions = new String[this.exceptions.size()];
        this.exceptions.toArray(exceptions);
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature,
                exceptions);
        if (mv != null) {
            accept(mv);
        }
    }

    /**
     * Makes the given method visitor visit this method.
     * 
     * @param mv
     *            a method visitor.
     */
    public void accept(final MethodVisitor mv) {
        // visits the method parameters
        int i, j, n;
        n = parameters == null ? 0 : parameters.size();
        for (i = 0; i < n; i++) {
            ParameterNode parameter = parameters.get(i);
            mv.visitParameter(parameter.name, parameter.access);
        }
        // visits the method attributes
        if (annotationDefault != null) {
            AnnotationVisitor av = mv.visitAnnotationDefault();
            AnnotationNode.accept(av, null, annotationDefault);
            if (av != null) {
                av.visitEnd();
            }
        }
        n = visibleAnnotations == null ? 0 : visibleAnnotations.size();
        for (i = 0; i < n; ++i) {
            AnnotationNode an = visibleAnnotations.get(i);
            an.accept(mv.visitAnnotation(an.desc, true));
        }
        n = invisibleAnnotations == null ? 0 : invisibleAnnotations.size();
        for (i = 0; i < n; ++i) {
            AnnotationNode an = invisibleAnnotations.get(i);
            an.accept(mv.visitAnnotation(an.desc, false));
        }
        n = visibleTypeAnnotations == null ? 0 : visibleTypeAnnotations.size();
        for (i = 0; i < n; ++i) {
            TypeAnnotationNode an = visibleTypeAnnotations.get(i);
            an.accept(mv.visitTypeAnnotation(an.typeRef, an.typePath, an.desc,
                    true));
        }
        n = invisibleTypeAnnotations == null ? 0 : invisibleTypeAnnotations
                .size();
        for (i = 0; i < n; ++i) {
            TypeAnnotationNode an = invisibleTypeAnnotations.get(i);
            an.accept(mv.visitTypeAnnotation(an.typeRef, an.typePath, an.desc,
                    false));
        }
        n = visibleParameterAnnotations == null ? 0
                : visibleParameterAnnotations.length;
        for (i = 0; i < n; ++i) {
            List<?> l = visibleParameterAnnotations[i];
            if (l == null) {
                continue;
            }
            for (j = 0; j < l.size(); ++j) {
                AnnotationNode an = (AnnotationNode) l.get(j);
                an.accept(mv.visitParameterAnnotation(i, an.desc, true));
            }
        }
        n = invisibleParameterAnnotations == null ? 0
                : invisibleParameterAnnotations.length;
        for (i = 0; i < n; ++i) {
            List<?> l = invisibleParameterAnnotations[i];
            if (l == null) {
                continue;
            }
            for (j = 0; j < l.size(); ++j) {
                AnnotationNode an = (AnnotationNode) l.get(j);
                an.accept(mv.visitParameterAnnotation(i, an.desc, false));
            }
        }
        
        if (visited) {
            instructions.resetLabels();
        }
        
        n = attrs == null ? 0 : attrs.size();
        for (i = 0; i < n; ++i) {
            mv.visitAttribute(attrs.get(i));
        }
        // visits the method's code
        if (instructions.size() > 0) {
            mv.visitCode();
            // visits try catch blocks
            n = tryCatchBlocks == null ? 0 : tryCatchBlocks.size();
            for (i = 0; i < n; ++i) {
                tryCatchBlocks.get(i).updateIndex(i);
                tryCatchBlocks.get(i).accept(mv);
            }
            // visits instructions
            instructions.accept(mv);
            // visits local variables
            n = localVariables == null ? 0 : localVariables.size();
            for (i = 0; i < n; ++i) {
                localVariables.get(i).accept(mv);
            }
            // visits local variable annotations
            n = visibleLocalVariableAnnotations == null ? 0
                    : visibleLocalVariableAnnotations.size();
            for (i = 0; i < n; ++i) {
                visibleLocalVariableAnnotations.get(i).accept(mv, true);
            }
            n = invisibleLocalVariableAnnotations == null ? 0
                    : invisibleLocalVariableAnnotations.size();
            for (i = 0; i < n; ++i) {
                invisibleLocalVariableAnnotations.get(i).accept(mv, false);
            }
            // visits maxs
            mv.visitMaxs(maxStack, maxLocals);
            visited = true;
        }
        mv.visitEnd();
    }
    
	/**
	 * Gets the amount of times the given opcodes has been matched
	 *
	 * @param opcode
	 *            The opcode to match
	 * @return The amount of times the given opcode has been matched.
	 */
	public int count(int opcode) {
		int count = 0;
		for (AbstractInsnNode ain : instructions.toArray()) {
			if (ain.opcode() == opcode)
				count++;
		}
		return count;
	}

	/**
	 * Gets the amount of times the given query has been matched
	 *
	 * @param entry
	 *            The query to match
	 * @return The amount of times the given query has been matched.
	 */
	public int count(InsnQuery entry) {
		int count = 0;
		for (AbstractInsnNode ain : instructions.toArray()) {
			if (entry.matches(ain))
				count++;
		}
		return count;
	}

	public int parameters() {
		String params = desc.split("\\(")[1].split("\\)")[0];
		if (params.isEmpty())
			return 0;
		int index = 0, count = 0;
		loop: while (index < params.length()) {
			if (params.charAt(index) == 'L') {
				for (int i = 0; i < params.length() - index; i++) {
					if (params.charAt(i + index) == ';') {
						index += i;
						count++;
						continue loop;
					}
				}
				return 0;
			} else {
				index++;
				count++;
			}
		}
		return count;
	}

	public boolean referenced(ClassNode cn) {
		return cn.references.contains(owner.name + "." + name + desc);
	}

	public void cacheKey(){
		cachedKey = key();
	}
	
	public String cachedKey() {
		return cachedKey;
	}
	
	public String key() {
		return owner.name + "." + name + desc;
	}

	public String halfKey() {
		return name + desc;
	}

	@Override
	public String toString() {
		return key();
	}
}