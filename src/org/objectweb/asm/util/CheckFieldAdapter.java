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
package org.objectweb.asm.util;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;

/**
 * A {@link FieldVisitor} that checks that its methods are properly used.
 */
public class CheckFieldAdapter extends FieldVisitor {

    private boolean end;

    /**
     * Constructs a new {@link CheckFieldAdapter}. <i>Subclasses must not use
     * this constructor</i>. Instead, they must use the
     * {@link #CheckFieldAdapter(int, FieldVisitor)} version.
     * 
     * @param fv
     *            the field visitor to which this adapter must delegate calls.
     * @throws IllegalStateException
     *             If a subclass calls this constructor.
     */
    public CheckFieldAdapter(final FieldVisitor fv) {
        this(Opcodes.ASM5, fv);
        if (getClass() != CheckFieldAdapter.class) {
            throw new IllegalStateException();
        }
    }

    /**
     * Constructs a new {@link CheckFieldAdapter}.
     * 
     * @param api
     *            the ASM API version implemented by this visitor. Must be one
     *            of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     * @param fv
     *            the field visitor to which this adapter must delegate calls.
     */
    protected CheckFieldAdapter(final int api, final FieldVisitor fv) {
        super(api, fv);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc,
            final boolean visible) {
        checkEnd();
        CheckMethodAdapter.checkDesc(desc, false);
        return new CheckAnnotationAdapter(super.visitAnnotation(desc, visible));
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef,
            final TypePath typePath, final String desc, final boolean visible) {
        checkEnd();
        int sort = typeRef >>> 24;
        if (sort != TypeReference.FIELD) {
            throw new IllegalArgumentException("Invalid type reference sort 0x"
                    + Integer.toHexString(sort));
        }
        CheckClassAdapter.checkTypeRefAndPath(typeRef, typePath);
        CheckMethodAdapter.checkDesc(desc, false);
        return new CheckAnnotationAdapter(super.visitTypeAnnotation(typeRef,
                typePath, desc, visible));
    }

    @Override
    public void visitAttribute(final Attribute attr) {
        checkEnd();
        if (attr == null) {
            throw new IllegalArgumentException(
                    "Invalid attribute (must not be null)");
        }
        super.visitAttribute(attr);
    }

    @Override
    public void visitEnd() {
        checkEnd();
        end = true;
        super.visitEnd();
    }

    private void checkEnd() {
        if (end) {
            throw new IllegalStateException(
                    "Cannot call a visit method after visitEnd has been called");
        }
    }
}
