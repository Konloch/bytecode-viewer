package org.objectweb.asm.commons.cfg;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

public class InsnVisitor extends MethodVisitor {

    public InsnVisitor(int api) {
        super(api);
    }

    public InsnVisitor(int api, InsnVisitor mv) {
        super(api, mv);
    }

    @Override
    public void visitParameter(String name, int access) {
        if (api < Opcodes.ASM5) {
            throw new RuntimeException();
        }
        if (mv != null) {
            mv.visitParameter(name, access);
        }
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        if (mv != null) {
            return mv.visitAnnotationDefault();
        }
        return null;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (mv != null) {
            return mv.visitAnnotation(desc, visible);
        }
        return null;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef,
                                                 TypePath typePath, String desc, boolean visible) {
        if (api < Opcodes.ASM5) {
            throw new RuntimeException();
        }
        if (mv != null) {
            return mv.visitTypeAnnotation(typeRef, typePath, desc, visible);
        }
        return null;
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter,
                                                      String desc, boolean visible) {
        if (mv != null) {
            return mv.visitParameterAnnotation(parameter, desc, visible);
        }
        return null;
    }

    @Override
    public void visitAttribute(Attribute attr) {
        if (mv != null) {
            mv.visitAttribute(attr);
        }
    }

    @Override
    public void visitCode() {
        if (mv != null) {
            mv.visitCode();
        }
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack,
                           Object[] stack) {
        if (mv != null) {
            mv.visitFrame(type, nLocal, local, nStack, stack);
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if (mv != null) {
            mv.visitInsn(opcode);
        }
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        if (mv != null) {
            mv.visitIntInsn(opcode, operand);
        }
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (mv != null) {
            mv.visitVarInsn(opcode, var);
        }
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (mv != null) {
            mv.visitTypeInsn(opcode, type);
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name,
                               String desc) {
        if (mv != null) {
            mv.visitFieldInsn(opcode, owner, name, desc);
        }
    }

    @Override
    @Deprecated
    public void visitMethodInsn(int opcode, String owner, String name,
                                String desc) {
        if (api >= Opcodes.ASM5) {
            boolean itf = opcode == Opcodes.INVOKEINTERFACE;
            visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        if (mv != null) {
            mv.visitMethodInsn(opcode, owner, name, desc);
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name,
                                String desc, boolean itf) {
        if (api < Opcodes.ASM5) {
            if (itf != (opcode == Opcodes.INVOKEINTERFACE)) {
                throw new IllegalArgumentException(
                        "INVOKESPECIAL/STATIC on interfaces require ASM 5");
            }
            visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        if (mv != null) {
            mv.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm,
                                       Object... bsmArgs) {
        if (mv != null) {
            mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        }
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if (mv != null) {
            mv.visitJumpInsn(opcode, label);
        }
    }

    @Override
    public void visitLabel(Label label) {
        if (mv != null) {
            mv.visitLabel(label);
        }
    }

    @Override
    public void visitLdcInsn(Object cst) {
        if (mv != null) {
            mv.visitLdcInsn(cst);
        }
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        if (mv != null) {
            mv.visitIincInsn(var, increment);
        }
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt,
                                     Label... labels) {
        if (mv != null) {
            mv.visitTableSwitchInsn(min, max, dflt, labels);
        }
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        if (mv != null) {
            mv.visitLookupSwitchInsn(dflt, keys, labels);
        }
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        if (mv != null) {
            mv.visitMultiANewArrayInsn(desc, dims);
        }
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef,
                                                 TypePath typePath, String desc, boolean visible) {
        if (api < Opcodes.ASM5) {
            throw new RuntimeException();
        }
        if (mv != null) {
            return mv.visitInsnAnnotation(typeRef, typePath, desc, visible);
        }
        return null;
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler,
                                   String type) {
        if (mv != null) {
            mv.visitTryCatchBlock(start, end, handler, type);
        }
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef,
                                                     TypePath typePath, String desc, boolean visible) {
        if (api < Opcodes.ASM5) {
            throw new RuntimeException();
        }
        if (mv != null) {
            return mv.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
        }
        return null;
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature,
                                   Label start, Label end, int index) {
        if (mv != null) {
            mv.visitLocalVariable(name, desc, signature, start, end, index);
        }
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef,
                                                          TypePath typePath, Label[] start, Label[] end, int[] index,
                                                          String desc, boolean visible) {
        if (api < Opcodes.ASM5) {
            throw new RuntimeException();
        }
        if (mv != null) {
            return mv.visitLocalVariableAnnotation(typeRef, typePath, start,
                    end, index, desc, visible);
        }
        return null;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        if (mv != null) {
            mv.visitLineNumber(line, start);
        }
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        if (mv != null) {
            mv.visitMaxs(maxStack, maxLocals);
        }
    }

    @Override
    public void visitEnd() {
        if (mv != null) {
            mv.visitEnd();
        }
    }
}