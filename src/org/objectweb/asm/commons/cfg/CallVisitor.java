package org.objectweb.asm.commons.cfg;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.cfg.graph.CallGraph;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Tyler Sedlar
 */
public class CallVisitor extends MethodVisitor {

    public CallVisitor() {
        super(Opcodes.ASM5);
    }

    public final CallGraph graph = new CallGraph();

    private MethodNode mn;

    public void visit(MethodNode mn) {
        this.mn = mn;
        mn.accept(this);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        graph.addMethodCall(mn.handle, new Handle(0, owner, name, desc));

    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name,
                                String desc, boolean itf) {
        graph.addMethodCall(mn.handle, new Handle(0, owner, name, desc));

    }
}