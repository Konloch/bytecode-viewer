/***
 * ASM XML Adapter
 * Copyright (c) 2004-2011, Eugene Kuleshov
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
package org.objectweb.asm.xml;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;

/**
 * A {@link org.objectweb.asm.ClassVisitor ClassVisitor} that generates SAX 2.0
 * events from the visited class. It can feed any kind of
 * {@link org.xml.sax.ContentHandler ContentHandler}, e.g. XML serializer, XSLT
 * or XQuery engines.
 * 
 * @see org.objectweb.asm.xml.Processor
 * @see org.objectweb.asm.xml.ASMContentHandler
 * 
 * @author Eugene Kuleshov
 */
public final class SAXClassAdapter extends ClassVisitor {

    SAXAdapter sa;

    private final boolean singleDocument;

    /**
     * Pseudo access flag used to distinguish class access flags.
     */
    private static final int ACCESS_CLASS = 262144;

    /**
     * Pseudo access flag used to distinguish field access flags.
     */
    private static final int ACCESS_FIELD = 524288;

    /**
     * Pseudo access flag used to distinguish inner class flags.
     */
    private static final int ACCESS_INNER = 1048576;

    /**
     * Constructs a new {@link SAXClassAdapter SAXClassAdapter} object.
     * 
     * @param h
     *            content handler that will be used to send SAX 2.0 events.
     * @param singleDocument
     *            if <tt>true</tt> adapter will not produce
     *            {@link ContentHandler#startDocument() startDocument()} and
     *            {@link ContentHandler#endDocument() endDocument()} events.
     */
    public SAXClassAdapter(final ContentHandler h, boolean singleDocument) {
        super(Opcodes.ASM5);
        this.sa = new SAXAdapter(h);
        this.singleDocument = singleDocument;
        if (!singleDocument) {
            sa.addDocumentStart();
        }
    }

    @Override
    public void visitSource(final String source, final String debug) {
        AttributesImpl att = new AttributesImpl();
        if (source != null) {
            att.addAttribute("", "file", "file", "", encode(source));
        }
        if (debug != null) {
            att.addAttribute("", "debug", "debug", "", encode(debug));
        }

        sa.addElement("source", att);
    }

    @Override
    public void visitOuterClass(final String owner, final String name,
            final String desc) {
        AttributesImpl att = new AttributesImpl();
        att.addAttribute("", "owner", "owner", "", owner);
        if (name != null) {
            att.addAttribute("", "name", "name", "", name);
        }
        if (desc != null) {
            att.addAttribute("", "desc", "desc", "", desc);
        }

        sa.addElement("outerclass", att);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc,
            final boolean visible) {
        return new SAXAnnotationAdapter(sa, "annotation", visible ? 1 : -1,
                null, desc);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef,
            TypePath typePath, String desc, boolean visible) {
        return new SAXAnnotationAdapter(sa, "typeAnnotation", visible ? 1 : -1,
                null, desc, typeRef, typePath);
    }

    @Override
    public void visit(final int version, final int access, final String name,
            final String signature, final String superName,
            final String[] interfaces) {
        StringBuffer sb = new StringBuffer();
        appendAccess(access | ACCESS_CLASS, sb);

        AttributesImpl att = new AttributesImpl();
        att.addAttribute("", "access", "access", "", sb.toString());
        if (name != null) {
            att.addAttribute("", "name", "name", "", name);
        }
        if (signature != null) {
            att.addAttribute("", "signature", "signature", "",
                    encode(signature));
        }
        if (superName != null) {
            att.addAttribute("", "parent", "parent", "", superName);
        }
        att.addAttribute("", "major", "major", "",
                Integer.toString(version & 0xFFFF));
        att.addAttribute("", "minor", "minor", "",
                Integer.toString(version >>> 16));
        sa.addStart("class", att);

        sa.addStart("interfaces", new AttributesImpl());
        if (interfaces != null && interfaces.length > 0) {
            for (int i = 0; i < interfaces.length; i++) {
                AttributesImpl att2 = new AttributesImpl();
                att2.addAttribute("", "name", "name", "", interfaces[i]);
                sa.addElement("interface", att2);
            }
        }
        sa.addEnd("interfaces");
    }

    @Override
    public FieldVisitor visitField(final int access, final String name,
            final String desc, final String signature, final Object value) {
        StringBuffer sb = new StringBuffer();
        appendAccess(access | ACCESS_FIELD, sb);

        AttributesImpl att = new AttributesImpl();
        att.addAttribute("", "access", "access", "", sb.toString());
        att.addAttribute("", "name", "name", "", name);
        att.addAttribute("", "desc", "desc", "", desc);
        if (signature != null) {
            att.addAttribute("", "signature", "signature", "",
                    encode(signature));
        }
        if (value != null) {
            att.addAttribute("", "value", "value", "", encode(value.toString()));
        }

        return new SAXFieldAdapter(sa, att);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name,
            final String desc, final String signature, final String[] exceptions) {
        StringBuffer sb = new StringBuffer();
        appendAccess(access, sb);

        AttributesImpl att = new AttributesImpl();
        att.addAttribute("", "access", "access", "", sb.toString());
        att.addAttribute("", "name", "name", "", name);
        att.addAttribute("", "desc", "desc", "", desc);
        if (signature != null) {
            att.addAttribute("", "signature", "signature", "", signature);
        }
        sa.addStart("method", att);

        sa.addStart("exceptions", new AttributesImpl());
        if (exceptions != null && exceptions.length > 0) {
            for (int i = 0; i < exceptions.length; i++) {
                AttributesImpl att2 = new AttributesImpl();
                att2.addAttribute("", "name", "name", "", exceptions[i]);
                sa.addElement("exception", att2);
            }
        }
        sa.addEnd("exceptions");

        return new SAXCodeAdapter(sa, access);
    }

    @Override
    public final void visitInnerClass(final String name,
            final String outerName, final String innerName, final int access) {
        StringBuffer sb = new StringBuffer();
        appendAccess(access | ACCESS_INNER, sb);

        AttributesImpl att = new AttributesImpl();
        att.addAttribute("", "access", "access", "", sb.toString());
        if (name != null) {
            att.addAttribute("", "name", "name", "", name);
        }
        if (outerName != null) {
            att.addAttribute("", "outerName", "outerName", "", outerName);
        }
        if (innerName != null) {
            att.addAttribute("", "innerName", "innerName", "", innerName);
        }
        sa.addElement("innerclass", att);
    }

    @Override
    public final void visitEnd() {
        sa.addEnd("class");
        if (!singleDocument) {
            sa.addDocumentEnd();
        }
    }

    static final String encode(final String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                sb.append("\\\\");
            } else if (c < 0x20 || c > 0x7f) {
                sb.append("\\u");
                if (c < 0x10) {
                    sb.append("000");
                } else if (c < 0x100) {
                    sb.append("00");
                } else if (c < 0x1000) {
                    sb.append('0');
                }
                sb.append(Integer.toString(c, 16));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    static void appendAccess(final int access, final StringBuffer sb) {
        if ((access & Opcodes.ACC_PUBLIC) != 0) {
            sb.append("public ");
        }
        if ((access & Opcodes.ACC_PRIVATE) != 0) {
            sb.append("private ");
        }
        if ((access & Opcodes.ACC_PROTECTED) != 0) {
            sb.append("protected ");
        }
        if ((access & Opcodes.ACC_FINAL) != 0) {
            sb.append("final ");
        }
        if ((access & Opcodes.ACC_STATIC) != 0) {
            sb.append("static ");
        }
        if ((access & Opcodes.ACC_SUPER) != 0) {
            if ((access & ACCESS_CLASS) == 0) {
                sb.append("synchronized ");
            } else {
                sb.append("super ");
            }
        }
        if ((access & Opcodes.ACC_VOLATILE) != 0) {
            if ((access & ACCESS_FIELD) == 0) {
                sb.append("bridge ");
            } else {
                sb.append("volatile ");
            }
        }
        if ((access & Opcodes.ACC_TRANSIENT) != 0) {
            if ((access & ACCESS_FIELD) == 0) {
                sb.append("varargs ");
            } else {
                sb.append("transient ");
            }
        }
        if ((access & Opcodes.ACC_NATIVE) != 0) {
            sb.append("native ");
        }
        if ((access & Opcodes.ACC_STRICT) != 0) {
            sb.append("strict ");
        }
        if ((access & Opcodes.ACC_INTERFACE) != 0) {
            sb.append("interface ");
        }
        if ((access & Opcodes.ACC_ABSTRACT) != 0) {
            sb.append("abstract ");
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            sb.append("synthetic ");
        }
        if ((access & Opcodes.ACC_ANNOTATION) != 0) {
            sb.append("annotation ");
        }
        if ((access & Opcodes.ACC_ENUM) != 0) {
            sb.append("enum ");
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            sb.append("deprecated ");
        }
        if ((access & Opcodes.ACC_MANDATED) != 0) {
            sb.append("mandated ");
        }
    }
}
