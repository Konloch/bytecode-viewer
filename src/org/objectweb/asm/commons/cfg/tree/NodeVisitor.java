package org.objectweb.asm.commons.cfg.tree;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.node.*;

public abstract class NodeVisitor implements Opcodes {

    private NodeVisitor nv;
    private BlockVisitor bv;

    public boolean validate() {
        return bv == null || bv.validate();
    }

    public NodeVisitor() {
        this.nv = null;
    }

    public NodeVisitor(BlockVisitor bv) {
        this.bv = bv;
    }

    public NodeVisitor(NodeVisitor nv) {
        this.nv = nv;
    }

    public void visitAny(AbstractNode n) {
        if (nv != null)
            nv.visitAny(n);
    }

    public void visit(AbstractNode n) {
        if (nv != null)
            nv.visit(n);
    }

    public void visitCode() {
        if (nv != null)
            nv.visitCode();
    }

    public void visitEnd() {
        if (nv != null)
            nv.visitEnd();
    }

    public void visitField(FieldMemberNode fmn) {
        if (nv != null)
            nv.visitField(fmn);
    }

    public void visitFrame(AbstractNode n) {
        if (nv != null)
            nv.visitFrame(n);
    }

    public void visitIinc(IincNode in) {
        if (nv != null)
            nv.visitIinc(in);
    }

    public void visitJump(JumpNode jn) {
        if (nv != null)
            nv.visitJump(jn);
    }

    public void visitLabel(AbstractNode n) {
        if (nv != null)
            nv.visitLabel(n);
    }

    public void visitConversion(ConversionNode cn) {
        if (nv != null)
            nv.visitConversion(cn);
    }

    public void visitConstant(ConstantNode cn) {
        if (nv != null)
            nv.visitConstant(cn);
    }

    public void visitNumber(NumberNode nn) {
        if (nv != null)
            nv.visitNumber(nn);
    }

    public void visitOperation(ArithmeticNode an) {
        if (nv != null)
            nv.visitOperation(an);
    }

    public void visitVariable(VariableNode vn) {
        if (nv != null) {
            nv.visitVariable(vn);
        }
    }

    public void visitLine(AbstractNode n) {
        if (nv != null)
            nv.visitLine(n);
    }

    public void visitLookupSwitch(AbstractNode n) {
        if (nv != null)
            nv.visitLookupSwitch(n);
    }

    public void visitMethod(MethodMemberNode mmn) {
        if (nv != null)
            nv.visitMethod(mmn);
    }

    public void visitMultiANewArray(AbstractNode n) {
        if (nv != null)
            nv.visitMultiANewArray(n);
    }

    public void visitTableSwitch(AbstractNode n) {
        if (nv != null)
            nv.visitTableSwitch(n);
    }

    public void visitType(TypeNode tn) {
        if (nv != null)
            nv.visitType(tn);
    }
}