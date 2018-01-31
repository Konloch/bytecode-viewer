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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.xml.sax.helpers.AttributesImpl;

/**
 * SAXAnnotationAdapter
 * 
 * @author Eugene Kuleshov
 */
public final class SAXAnnotationAdapter extends AnnotationVisitor {

    SAXAdapter sa;

    private final String elementName;

    public SAXAnnotationAdapter(final SAXAdapter sa, final String elementName,
            final int visible, final String name, final String desc) {
        this(Opcodes.ASM5, sa, elementName, visible, desc, name, -1, -1, null,
                null, null, null);
    }

    public SAXAnnotationAdapter(final SAXAdapter sa, final String elementName,
            final int visible, final int parameter, final String desc) {
        this(Opcodes.ASM5, sa, elementName, visible, desc, null, parameter, -1,
                null, null, null, null);
    }

    public SAXAnnotationAdapter(final SAXAdapter sa, final String elementName,
            final int visible, final String name, final String desc,
            final int typeRef, final TypePath typePath) {
        this(Opcodes.ASM5, sa, elementName, visible, desc, name, -1, typeRef,
                typePath, null, null, null);
    }

    public SAXAnnotationAdapter(final SAXAdapter sa, final String elementName,
            final int visible, final String name, final String desc,
            int typeRef, TypePath typePath, final String[] start,
            final String[] end, final int[] index) {
        this(Opcodes.ASM5, sa, elementName, visible, desc, name, -1, typeRef,
                typePath, start, end, index);
    }

    protected SAXAnnotationAdapter(final int api, final SAXAdapter sa,
            final String elementName, final int visible, final String desc,
            final String name, final int parameter) {
        this(api, sa, elementName, visible, desc, name, parameter, -1, null,
                null, null, null);
    }

    protected SAXAnnotationAdapter(final int api, final SAXAdapter sa,
            final String elementName, final int visible, final String desc,
            final String name, final int parameter, final int typeRef,
            final TypePath typePath, final String[] start, final String[] end,
            final int[] index) {
        super(api);
        this.sa = sa;
        this.elementName = elementName;

        AttributesImpl att = new AttributesImpl();
        if (name != null) {
            att.addAttribute("", "name", "name", "", name);
        }
        if (visible != 0) {
            att.addAttribute("", "visible", "visible", "", visible > 0 ? "true"
                    : "false");
        }
        if (parameter != -1) {
            att.addAttribute("", "parameter", "parameter", "",
                    Integer.toString(parameter));
        }
        if (desc != null) {
            att.addAttribute("", "desc", "desc", "", desc);
        }
        if (typeRef != -1) {
            att.addAttribute("", "typeRef", "typeRef", "",
                    Integer.toString(typeRef));
        }
        if (typePath != null) {
            att.addAttribute("", "typePath", "typePath", "",
                    typePath.toString());
        }
        if (start != null) {
            StringBuffer value = new StringBuffer(start[0]);
            for (int i = 1; i < start.length; ++i) {
                value.append(" ").append(start[i]);
            }
            att.addAttribute("", "start", "start", "", value.toString());
        }
        if (end != null) {
            StringBuffer value = new StringBuffer(end[0]);
            for (int i = 1; i < end.length; ++i) {
                value.append(" ").append(end[i]);
            }
            att.addAttribute("", "end", "end", "", value.toString());
        }
        if (index != null) {
            StringBuffer value = new StringBuffer();
            value.append(index[0]);
            for (int i = 1; i < index.length; ++i) {
                value.append(" ").append(index[i]);
            }
            att.addAttribute("", "index", "index", "", value.toString());
        }

        sa.addStart(elementName, att);
    }

    @Override
    public void visit(final String name, final Object value) {
        Class<?> c = value.getClass();
        if (c.isArray()) {
            AnnotationVisitor av = visitArray(name);
            if (value instanceof byte[]) {
                byte[] b = (byte[]) value;
                for (int i = 0; i < b.length; i++) {
                    av.visit(null, b[i]);
                }

            } else if (value instanceof char[]) {
                char[] b = (char[]) value;
                for (int i = 0; i < b.length; i++) {
                    av.visit(null, b[i]);
                }

            } else if (value instanceof short[]) {
                short[] b = (short[]) value;
                for (int i = 0; i < b.length; i++) {
                    av.visit(null, b[i]);
                }

            } else if (value instanceof boolean[]) {
                boolean[] b = (boolean[]) value;
                for (int i = 0; i < b.length; i++) {
                    av.visit(null, Boolean.valueOf(b[i]));
                }

            } else if (value instanceof int[]) {
                int[] b = (int[]) value;
                for (int i = 0; i < b.length; i++) {
                    av.visit(null, b[i]);
                }

            } else if (value instanceof long[]) {
                long[] b = (long[]) value;
                for (int i = 0; i < b.length; i++) {
                    av.visit(null, b[i]);
                }

            } else if (value instanceof float[]) {
                float[] b = (float[]) value;
                for (int i = 0; i < b.length; i++) {
                    av.visit(null, b[i]);
                }

            } else if (value instanceof double[]) {
                double[] b = (double[]) value;
                for (int i = 0; i < b.length; i++) {
                    av.visit(null, b[i]);
                }

            }
            av.visitEnd();
        } else {
            addValueElement("annotationValue", name, Type.getDescriptor(c),
                    value.toString());
        }
    }

    @Override
    public void visitEnum(final String name, final String desc,
            final String value) {
        addValueElement("annotationValueEnum", name, desc, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String name,
            final String desc) {
        return new SAXAnnotationAdapter(sa, "annotationValueAnnotation", 0,
                name, desc);
    }

    @Override
    public AnnotationVisitor visitArray(final String name) {
        return new SAXAnnotationAdapter(sa, "annotationValueArray", 0, name,
                null);
    }

    @Override
    public void visitEnd() {
        sa.addEnd(elementName);
    }

    private void addValueElement(final String element, final String name,
            final String desc, final String value) {
        AttributesImpl att = new AttributesImpl();
        if (name != null) {
            att.addAttribute("", "name", "name", "", name);
        }
        if (desc != null) {
            att.addAttribute("", "desc", "desc", "", desc);
        }
        if (value != null) {
            att.addAttribute("", "value", "value", "",
                    SAXClassAdapter.encode(value));
        }

        sa.addElement(element, att);
    }
}
