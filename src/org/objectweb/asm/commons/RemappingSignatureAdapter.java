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

package org.objectweb.asm.commons;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

/**
 * A {@link SignatureVisitor} adapter for type mapping.
 * 
 * @author Eugene Kuleshov
 */
public class RemappingSignatureAdapter extends SignatureVisitor {

    private final SignatureVisitor v;

    private final Remapper remapper;

    private String className;

    public RemappingSignatureAdapter(final SignatureVisitor v,
            final Remapper remapper) {
        this(Opcodes.ASM5, v, remapper);
    }

    protected RemappingSignatureAdapter(final int api,
            final SignatureVisitor v, final Remapper remapper) {
        super(api);
        this.v = v;
        this.remapper = remapper;
    }

    @Override
    public void visitClassType(String name) {
        className = name;
        v.visitClassType(remapper.mapType(name));
    }

    @Override
    public void visitInnerClassType(String name) {
        String remappedOuter = remapper.mapType(className) + '$';
        className = className + '$' + name;
        String remappedName = remapper.mapType(className);
        int index = remappedName.startsWith(remappedOuter) ? remappedOuter
                .length() : remappedName.lastIndexOf('$') + 1;
        v.visitInnerClassType(remappedName.substring(index));
    }

    @Override
    public void visitFormalTypeParameter(String name) {
        v.visitFormalTypeParameter(name);
    }

    @Override
    public void visitTypeVariable(String name) {
        v.visitTypeVariable(name);
    }

    @Override
    public SignatureVisitor visitArrayType() {
        v.visitArrayType();
        return this;
    }

    @Override
    public void visitBaseType(char descriptor) {
        v.visitBaseType(descriptor);
    }

    @Override
    public SignatureVisitor visitClassBound() {
        v.visitClassBound();
        return this;
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        v.visitExceptionType();
        return this;
    }

    @Override
    public SignatureVisitor visitInterface() {
        v.visitInterface();
        return this;
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        v.visitInterfaceBound();
        return this;
    }

    @Override
    public SignatureVisitor visitParameterType() {
        v.visitParameterType();
        return this;
    }

    @Override
    public SignatureVisitor visitReturnType() {
        v.visitReturnType();
        return this;
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        v.visitSuperclass();
        return this;
    }

    @Override
    public void visitTypeArgument() {
        v.visitTypeArgument();
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
        v.visitTypeArgument(wildcard);
        return this;
    }

    @Override
    public void visitEnd() {
        v.visitEnd();
    }
}
