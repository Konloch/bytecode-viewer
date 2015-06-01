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

import java.util.List;

import org.objectweb.asm.MethodVisitor;

/**
 * A node that represents a try catch block.
 * 
 * @author Eric Bruneton
 */
public class TryCatchBlockNode {

    /**
     * Beginning of the exception handler's scope (inclusive).
     */
    public LabelNode start;

    /**
     * End of the exception handler's scope (exclusive).
     */
    public LabelNode end;

    /**
     * Beginning of the exception handler's code.
     */
    public LabelNode handler;

    /**
     * Internal name of the type of exceptions handled by the handler. May be
     * <tt>null</tt> to catch any exceptions (for "finally" blocks).
     */
    public String type;

    /**
     * The runtime visible type annotations on the exception handler type. This
     * list is a list of {@link TypeAnnotationNode} objects. May be
     * <tt>null</tt>.
     * 
     * @associates org.objectweb.asm.tree.TypeAnnotationNode
     * @label visible
     */
    public List<TypeAnnotationNode> visibleTypeAnnotations;

    /**
     * The runtime invisible type annotations on the exception handler type.
     * This list is a list of {@link TypeAnnotationNode} objects. May be
     * <tt>null</tt>.
     * 
     * @associates org.objectweb.asm.tree.TypeAnnotationNode
     * @label invisible
     */
    public List<TypeAnnotationNode> invisibleTypeAnnotations;

    /**
     * Constructs a new {@link TryCatchBlockNode}.
     * 
     * @param start
     *            beginning of the exception handler's scope (inclusive).
     * @param end
     *            end of the exception handler's scope (exclusive).
     * @param handler
     *            beginning of the exception handler's code.
     * @param type
     *            internal name of the type of exceptions handled by the
     *            handler, or <tt>null</tt> to catch any exceptions (for
     *            "finally" blocks).
     */
    public TryCatchBlockNode(final LabelNode start, final LabelNode end,
            final LabelNode handler, final String type) {
        this.start = start;
        this.end = end;
        this.handler = handler;
        this.type = type;
    }

    /**
     * Updates the index of this try catch block in the method's list of try
     * catch block nodes. This index maybe stored in the 'target' field of the
     * type annotations of this block.
     * 
     * @param index
     *            the new index of this try catch block in the method's list of
     *            try catch block nodes.
     */
    public void updateIndex(final int index) {
        int newTypeRef = 0x42000000 | (index << 8);
        if (visibleTypeAnnotations != null) {
            for (TypeAnnotationNode tan : visibleTypeAnnotations) {
                tan.typeRef = newTypeRef;
            }
        }
        if (invisibleTypeAnnotations != null) {
            for (TypeAnnotationNode tan : invisibleTypeAnnotations) {
                tan.typeRef = newTypeRef;
            }
        }
    }

    /**
     * Makes the given visitor visit this try catch block.
     * 
     * @param mv
     *            a method visitor.
     */
    public void accept(final MethodVisitor mv) {
        mv.visitTryCatchBlock(start.getLabel(), end.getLabel(),
                handler == null ? null : handler.getLabel(), type);
        int n = visibleTypeAnnotations == null ? 0 : visibleTypeAnnotations
                .size();
        for (int i = 0; i < n; ++i) {
            TypeAnnotationNode an = visibleTypeAnnotations.get(i);
            an.accept(mv.visitTryCatchAnnotation(an.typeRef, an.typePath,
                    an.desc, true));
        }
        n = invisibleTypeAnnotations == null ? 0 : invisibleTypeAnnotations
                .size();
        for (int i = 0; i < n; ++i) {
            TypeAnnotationNode an = invisibleTypeAnnotations.get(i);
            an.accept(mv.visitTryCatchAnnotation(an.typeRef, an.typePath,
                    an.desc, false));
        }
    }
}
