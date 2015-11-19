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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A {@link org.xml.sax.ContentHandler ContentHandler} that transforms XML
 * document into Java class file. This class can be feeded by any kind of SAX
 * 2.0 event producers, e.g. XML parser, XSLT or XPath engines, or custom code.
 * 
 * @see org.objectweb.asm.xml.SAXClassAdapter
 * @see org.objectweb.asm.xml.Processor
 * 
 * @author Eugene Kuleshov
 */
public class ASMContentHandler extends DefaultHandler implements Opcodes {

    /**
     * Stack of the intermediate processing contexts.
     */
    private final ArrayList<Object> stack = new ArrayList<Object>();

    /**
     * Complete name of the current element.
     */
    String match = "";

    /**
     * Current instance of the {@link ClassVisitor ClassVisitor} used to visit
     * classfile bytecode.
     */
    protected ClassVisitor cv;

    /**
     * Map of the active {@link Label Label} instances for current method.
     */
    protected Map<Object, Label> labels;

    private static final String BASE = "class";

    private final RuleSet RULES = new RuleSet();
    {
        RULES.add(BASE, new ClassRule());
        RULES.add(BASE + "/interfaces/interface", new InterfaceRule());
        RULES.add(BASE + "/interfaces", new InterfacesRule());
        RULES.add(BASE + "/outerclass", new OuterClassRule());
        RULES.add(BASE + "/innerclass", new InnerClassRule());
        RULES.add(BASE + "/source", new SourceRule());
        RULES.add(BASE + "/field", new FieldRule());

        RULES.add(BASE + "/method", new MethodRule());
        RULES.add(BASE + "/method/exceptions/exception", new ExceptionRule());
        RULES.add(BASE + "/method/exceptions", new ExceptionsRule());

        RULES.add(BASE + "/method/parameter", new MethodParameterRule());
        RULES.add(BASE + "/method/annotationDefault",
                new AnnotationDefaultRule());

        RULES.add(BASE + "/method/code/*", new OpcodesRule()); // opcodes

        RULES.add(BASE + "/method/code/frame", new FrameRule());
        RULES.add(BASE + "/method/code/frame/local", new FrameTypeRule());
        RULES.add(BASE + "/method/code/frame/stack", new FrameTypeRule());

        RULES.add(BASE + "/method/code/TABLESWITCH", new TableSwitchRule());
        RULES.add(BASE + "/method/code/TABLESWITCH/label",
                new TableSwitchLabelRule());
        RULES.add(BASE + "/method/code/LOOKUPSWITCH", new LookupSwitchRule());
        RULES.add(BASE + "/method/code/LOOKUPSWITCH/label",
                new LookupSwitchLabelRule());

        RULES.add(BASE + "/method/code/INVOKEDYNAMIC", new InvokeDynamicRule());
        RULES.add(BASE + "/method/code/INVOKEDYNAMIC/bsmArg",
                new InvokeDynamicBsmArgumentsRule());

        RULES.add(BASE + "/method/code/Label", new LabelRule());
        RULES.add(BASE + "/method/code/TryCatch", new TryCatchRule());
        RULES.add(BASE + "/method/code/LineNumber", new LineNumberRule());
        RULES.add(BASE + "/method/code/LocalVar", new LocalVarRule());
        RULES.add(BASE + "/method/code/Max", new MaxRule());

        RULES.add("*/annotation", new AnnotationRule());
        RULES.add("*/typeAnnotation", new TypeAnnotationRule());
        RULES.add("*/parameterAnnotation", new AnnotationParameterRule());
        RULES.add("*/insnAnnotation", new InsnAnnotationRule());
        RULES.add("*/tryCatchAnnotation", new TryCatchAnnotationRule());
        RULES.add("*/localVariableAnnotation",
                new LocalVariableAnnotationRule());
        RULES.add("*/annotationValue", new AnnotationValueRule());
        RULES.add("*/annotationValueAnnotation",
                new AnnotationValueAnnotationRule());
        RULES.add("*/annotationValueEnum", new AnnotationValueEnumRule());
        RULES.add("*/annotationValueArray", new AnnotationValueArrayRule());
    }

    private static interface OpcodeGroup {
        public static final int INSN = 0;
        public static final int INSN_INT = 1;
        public static final int INSN_VAR = 2;
        public static final int INSN_TYPE = 3;
        public static final int INSN_FIELD = 4;
        public static final int INSN_METHOD = 5;
        public static final int INSN_JUMP = 6;
        public static final int INSN_LDC = 7;
        public static final int INSN_IINC = 8;
        public static final int INSN_MULTIANEWARRAY = 9;
    }

    /**
     * Map of the opcode names to opcode and opcode group
     */
    static final HashMap<String, Opcode> OPCODES = new HashMap<String, Opcode>();
    static {
        addOpcode("NOP", NOP, OpcodeGroup.INSN);
        addOpcode("ACONST_NULL", ACONST_NULL, OpcodeGroup.INSN);
        addOpcode("ICONST_M1", ICONST_M1, OpcodeGroup.INSN);
        addOpcode("ICONST_0", ICONST_0, OpcodeGroup.INSN);
        addOpcode("ICONST_1", ICONST_1, OpcodeGroup.INSN);
        addOpcode("ICONST_2", ICONST_2, OpcodeGroup.INSN);
        addOpcode("ICONST_3", ICONST_3, OpcodeGroup.INSN);
        addOpcode("ICONST_4", ICONST_4, OpcodeGroup.INSN);
        addOpcode("ICONST_5", ICONST_5, OpcodeGroup.INSN);
        addOpcode("LCONST_0", LCONST_0, OpcodeGroup.INSN);
        addOpcode("LCONST_1", LCONST_1, OpcodeGroup.INSN);
        addOpcode("FCONST_0", FCONST_0, OpcodeGroup.INSN);
        addOpcode("FCONST_1", FCONST_1, OpcodeGroup.INSN);
        addOpcode("FCONST_2", FCONST_2, OpcodeGroup.INSN);
        addOpcode("DCONST_0", DCONST_0, OpcodeGroup.INSN);
        addOpcode("DCONST_1", DCONST_1, OpcodeGroup.INSN);
        addOpcode("BIPUSH", BIPUSH, OpcodeGroup.INSN_INT);
        addOpcode("SIPUSH", SIPUSH, OpcodeGroup.INSN_INT);
        addOpcode("LDC", LDC, OpcodeGroup.INSN_LDC);
        addOpcode("ILOAD", ILOAD, OpcodeGroup.INSN_VAR);
        addOpcode("LLOAD", LLOAD, OpcodeGroup.INSN_VAR);
        addOpcode("FLOAD", FLOAD, OpcodeGroup.INSN_VAR);
        addOpcode("DLOAD", DLOAD, OpcodeGroup.INSN_VAR);
        addOpcode("ALOAD", ALOAD, OpcodeGroup.INSN_VAR);
        addOpcode("IALOAD", IALOAD, OpcodeGroup.INSN);
        addOpcode("LALOAD", LALOAD, OpcodeGroup.INSN);
        addOpcode("FALOAD", FALOAD, OpcodeGroup.INSN);
        addOpcode("DALOAD", DALOAD, OpcodeGroup.INSN);
        addOpcode("AALOAD", AALOAD, OpcodeGroup.INSN);
        addOpcode("BALOAD", BALOAD, OpcodeGroup.INSN);
        addOpcode("CALOAD", CALOAD, OpcodeGroup.INSN);
        addOpcode("SALOAD", SALOAD, OpcodeGroup.INSN);
        addOpcode("ISTORE", ISTORE, OpcodeGroup.INSN_VAR);
        addOpcode("LSTORE", LSTORE, OpcodeGroup.INSN_VAR);
        addOpcode("FSTORE", FSTORE, OpcodeGroup.INSN_VAR);
        addOpcode("DSTORE", DSTORE, OpcodeGroup.INSN_VAR);
        addOpcode("ASTORE", ASTORE, OpcodeGroup.INSN_VAR);
        addOpcode("IASTORE", IASTORE, OpcodeGroup.INSN);
        addOpcode("LASTORE", LASTORE, OpcodeGroup.INSN);
        addOpcode("FASTORE", FASTORE, OpcodeGroup.INSN);
        addOpcode("DASTORE", DASTORE, OpcodeGroup.INSN);
        addOpcode("AASTORE", AASTORE, OpcodeGroup.INSN);
        addOpcode("BASTORE", BASTORE, OpcodeGroup.INSN);
        addOpcode("CASTORE", CASTORE, OpcodeGroup.INSN);
        addOpcode("SASTORE", SASTORE, OpcodeGroup.INSN);
        addOpcode("POP", POP, OpcodeGroup.INSN);
        addOpcode("POP2", POP2, OpcodeGroup.INSN);
        addOpcode("DUP", DUP, OpcodeGroup.INSN);
        addOpcode("DUP_X1", DUP_X1, OpcodeGroup.INSN);
        addOpcode("DUP_X2", DUP_X2, OpcodeGroup.INSN);
        addOpcode("DUP2", DUP2, OpcodeGroup.INSN);
        addOpcode("DUP2_X1", DUP2_X1, OpcodeGroup.INSN);
        addOpcode("DUP2_X2", DUP2_X2, OpcodeGroup.INSN);
        addOpcode("SWAP", SWAP, OpcodeGroup.INSN);
        addOpcode("IADD", IADD, OpcodeGroup.INSN);
        addOpcode("LADD", LADD, OpcodeGroup.INSN);
        addOpcode("FADD", FADD, OpcodeGroup.INSN);
        addOpcode("DADD", DADD, OpcodeGroup.INSN);
        addOpcode("ISUB", ISUB, OpcodeGroup.INSN);
        addOpcode("LSUB", LSUB, OpcodeGroup.INSN);
        addOpcode("FSUB", FSUB, OpcodeGroup.INSN);
        addOpcode("DSUB", DSUB, OpcodeGroup.INSN);
        addOpcode("IMUL", IMUL, OpcodeGroup.INSN);
        addOpcode("LMUL", LMUL, OpcodeGroup.INSN);
        addOpcode("FMUL", FMUL, OpcodeGroup.INSN);
        addOpcode("DMUL", DMUL, OpcodeGroup.INSN);
        addOpcode("IDIV", IDIV, OpcodeGroup.INSN);
        addOpcode("LDIV", LDIV, OpcodeGroup.INSN);
        addOpcode("FDIV", FDIV, OpcodeGroup.INSN);
        addOpcode("DDIV", DDIV, OpcodeGroup.INSN);
        addOpcode("IREM", IREM, OpcodeGroup.INSN);
        addOpcode("LREM", LREM, OpcodeGroup.INSN);
        addOpcode("FREM", FREM, OpcodeGroup.INSN);
        addOpcode("DREM", DREM, OpcodeGroup.INSN);
        addOpcode("INEG", INEG, OpcodeGroup.INSN);
        addOpcode("LNEG", LNEG, OpcodeGroup.INSN);
        addOpcode("FNEG", FNEG, OpcodeGroup.INSN);
        addOpcode("DNEG", DNEG, OpcodeGroup.INSN);
        addOpcode("ISHL", ISHL, OpcodeGroup.INSN);
        addOpcode("LSHL", LSHL, OpcodeGroup.INSN);
        addOpcode("ISHR", ISHR, OpcodeGroup.INSN);
        addOpcode("LSHR", LSHR, OpcodeGroup.INSN);
        addOpcode("IUSHR", IUSHR, OpcodeGroup.INSN);
        addOpcode("LUSHR", LUSHR, OpcodeGroup.INSN);
        addOpcode("IAND", IAND, OpcodeGroup.INSN);
        addOpcode("LAND", LAND, OpcodeGroup.INSN);
        addOpcode("IOR", IOR, OpcodeGroup.INSN);
        addOpcode("LOR", LOR, OpcodeGroup.INSN);
        addOpcode("IXOR", IXOR, OpcodeGroup.INSN);
        addOpcode("LXOR", LXOR, OpcodeGroup.INSN);
        addOpcode("IINC", IINC, OpcodeGroup.INSN_IINC);
        addOpcode("I2L", I2L, OpcodeGroup.INSN);
        addOpcode("I2F", I2F, OpcodeGroup.INSN);
        addOpcode("I2D", I2D, OpcodeGroup.INSN);
        addOpcode("L2I", L2I, OpcodeGroup.INSN);
        addOpcode("L2F", L2F, OpcodeGroup.INSN);
        addOpcode("L2D", L2D, OpcodeGroup.INSN);
        addOpcode("F2I", F2I, OpcodeGroup.INSN);
        addOpcode("F2L", F2L, OpcodeGroup.INSN);
        addOpcode("F2D", F2D, OpcodeGroup.INSN);
        addOpcode("D2I", D2I, OpcodeGroup.INSN);
        addOpcode("D2L", D2L, OpcodeGroup.INSN);
        addOpcode("D2F", D2F, OpcodeGroup.INSN);
        addOpcode("I2B", I2B, OpcodeGroup.INSN);
        addOpcode("I2C", I2C, OpcodeGroup.INSN);
        addOpcode("I2S", I2S, OpcodeGroup.INSN);
        addOpcode("LCMP", LCMP, OpcodeGroup.INSN);
        addOpcode("FCMPL", FCMPL, OpcodeGroup.INSN);
        addOpcode("FCMPG", FCMPG, OpcodeGroup.INSN);
        addOpcode("DCMPL", DCMPL, OpcodeGroup.INSN);
        addOpcode("DCMPG", DCMPG, OpcodeGroup.INSN);
        addOpcode("IFEQ", IFEQ, OpcodeGroup.INSN_JUMP);
        addOpcode("IFNE", IFNE, OpcodeGroup.INSN_JUMP);
        addOpcode("IFLT", IFLT, OpcodeGroup.INSN_JUMP);
        addOpcode("IFGE", IFGE, OpcodeGroup.INSN_JUMP);
        addOpcode("IFGT", IFGT, OpcodeGroup.INSN_JUMP);
        addOpcode("IFLE", IFLE, OpcodeGroup.INSN_JUMP);
        addOpcode("IF_ICMPEQ", IF_ICMPEQ, OpcodeGroup.INSN_JUMP);
        addOpcode("IF_ICMPNE", IF_ICMPNE, OpcodeGroup.INSN_JUMP);
        addOpcode("IF_ICMPLT", IF_ICMPLT, OpcodeGroup.INSN_JUMP);
        addOpcode("IF_ICMPGE", IF_ICMPGE, OpcodeGroup.INSN_JUMP);
        addOpcode("IF_ICMPGT", IF_ICMPGT, OpcodeGroup.INSN_JUMP);
        addOpcode("IF_ICMPLE", IF_ICMPLE, OpcodeGroup.INSN_JUMP);
        addOpcode("IF_ACMPEQ", IF_ACMPEQ, OpcodeGroup.INSN_JUMP);
        addOpcode("IF_ACMPNE", IF_ACMPNE, OpcodeGroup.INSN_JUMP);
        addOpcode("GOTO", GOTO, OpcodeGroup.INSN_JUMP);
        addOpcode("JSR", JSR, OpcodeGroup.INSN_JUMP);
        addOpcode("RET", RET, OpcodeGroup.INSN_VAR);
        addOpcode("IRETURN", IRETURN, OpcodeGroup.INSN);
        addOpcode("LRETURN", LRETURN, OpcodeGroup.INSN);
        addOpcode("FRETURN", FRETURN, OpcodeGroup.INSN);
        addOpcode("DRETURN", DRETURN, OpcodeGroup.INSN);
        addOpcode("ARETURN", ARETURN, OpcodeGroup.INSN);
        addOpcode("RETURN", RETURN, OpcodeGroup.INSN);
        addOpcode("GETSTATIC", GETSTATIC, OpcodeGroup.INSN_FIELD);
        addOpcode("PUTSTATIC", PUTSTATIC, OpcodeGroup.INSN_FIELD);
        addOpcode("GETFIELD", GETFIELD, OpcodeGroup.INSN_FIELD);
        addOpcode("PUTFIELD", PUTFIELD, OpcodeGroup.INSN_FIELD);
        addOpcode("INVOKEVIRTUAL", INVOKEVIRTUAL, OpcodeGroup.INSN_METHOD);
        addOpcode("INVOKESPECIAL", INVOKESPECIAL, OpcodeGroup.INSN_METHOD);
        addOpcode("INVOKESTATIC", INVOKESTATIC, OpcodeGroup.INSN_METHOD);
        addOpcode("INVOKEINTERFACE", INVOKEINTERFACE, OpcodeGroup.INSN_METHOD);
        addOpcode("NEW", NEW, OpcodeGroup.INSN_TYPE);
        addOpcode("NEWARRAY", NEWARRAY, OpcodeGroup.INSN_INT);
        addOpcode("ANEWARRAY", ANEWARRAY, OpcodeGroup.INSN_TYPE);
        addOpcode("ARRAYLENGTH", ARRAYLENGTH, OpcodeGroup.INSN);
        addOpcode("ATHROW", ATHROW, OpcodeGroup.INSN);
        addOpcode("CHECKCAST", CHECKCAST, OpcodeGroup.INSN_TYPE);
        addOpcode("INSTANCEOF", INSTANCEOF, OpcodeGroup.INSN_TYPE);
        addOpcode("MONITORENTER", MONITORENTER, OpcodeGroup.INSN);
        addOpcode("MONITOREXIT", MONITOREXIT, OpcodeGroup.INSN);
        addOpcode("MULTIANEWARRAY", MULTIANEWARRAY,
                OpcodeGroup.INSN_MULTIANEWARRAY);
        addOpcode("IFNULL", IFNULL, OpcodeGroup.INSN_JUMP);
        addOpcode("IFNONNULL", IFNONNULL, OpcodeGroup.INSN_JUMP);
    }

    private static void addOpcode(String operStr, int oper, int group) {
        OPCODES.put(operStr, new Opcode(oper, group));
    }

    static final HashMap<String, Integer> TYPES = new HashMap<String, Integer>();
    static {
        String[] types = SAXCodeAdapter.TYPES;
        for (int i = 0; i < types.length; i++) {
            TYPES.put(types[i], i);
        }
    }

    /**
     * Constructs a new {@link ASMContentHandler ASMContentHandler} object.
     * 
     * @param cv
     *            class visitor that will be called to reconstruct the classfile
     *            using the XML stream.
     */
    public ASMContentHandler(final ClassVisitor cv) {
        this.cv = cv;
    }

    /**
     * Process notification of the start of an XML element being reached.
     * 
     * @param ns
     *            - The Namespace URI, or the empty string if the element has no
     *            Namespace URI or if Namespace processing is not being
     *            performed.
     * @param lName
     *            - The local name (without prefix), or the empty string if
     *            Namespace processing is not being performed.
     * @param qName
     *            - The qualified name (with prefix), or the empty string if
     *            qualified names are not available.
     * @param list
     *            - The attributes attached to the element. If there are no
     *            attributes, it shall be an empty Attributes object.
     * @exception SAXException
     *                if a parsing error is to be reported
     */
    @Override
    public final void startElement(final String ns, final String lName,
            final String qName, final Attributes list) throws SAXException {
        // the actual element name is either in lName or qName, depending
        // on whether the parser is namespace aware
        String name = lName == null || lName.length() == 0 ? qName : lName;

        // Compute the current matching rule
        StringBuffer sb = new StringBuffer(match);
        if (match.length() > 0) {
            sb.append('/');
        }
        sb.append(name);
        match = sb.toString();

        // Fire "begin" events for all relevant rules
        Rule r = (Rule) RULES.match(match);
        if (r != null) {
            r.begin(name, list);
        }
    }

    /**
     * Process notification of the end of an XML element being reached.
     * 
     * @param ns
     *            - The Namespace URI, or the empty string if the element has no
     *            Namespace URI or if Namespace processing is not being
     *            performed.
     * @param lName
     *            - The local name (without prefix), or the empty string if
     *            Namespace processing is not being performed.
     * @param qName
     *            - The qualified XML 1.0 name (with prefix), or the empty
     *            string if qualified names are not available.
     * 
     * @exception SAXException
     *                if a parsing error is to be reported
     */
    @Override
    public final void endElement(final String ns, final String lName,
            final String qName) throws SAXException {
        // the actual element name is either in lName or qName, depending
        // on whether the parser is namespace aware
        String name = lName == null || lName.length() == 0 ? qName : lName;

        // Fire "end" events for all relevant rules in reverse order
        Rule r = (Rule) RULES.match(match);
        if (r != null) {
            r.end(name);
        }

        // Recover the previous match expression
        int slash = match.lastIndexOf('/');
        if (slash >= 0) {
            match = match.substring(0, slash);
        } else {
            match = "";
        }
    }

    /**
     * Return the top object on the stack without removing it. If there are no
     * objects on the stack, return <code>null</code>.
     * 
     * @return the top object on the stack without removing it.
     */
    final Object peek() {
        int size = stack.size();
        return size == 0 ? null : stack.get(size - 1);
    }

    /**
     * Pop the top object off of the stack, and return it. If there are no
     * objects on the stack, return <code>null</code>.
     * 
     * @return the top object off of the stack.
     */
    final Object pop() {
        int size = stack.size();
        return size == 0 ? null : stack.remove(size - 1);
    }

    /**
     * Push a new object onto the top of the object stack.
     * 
     * @param object
     *            The new object
     */
    final void push(final Object object) {
        stack.add(object);
    }

    static final class RuleSet {

        private final HashMap<String, Object> rules = new HashMap<String, Object>();

        private final ArrayList<String> lpatterns = new ArrayList<String>();

        private final ArrayList<String> rpatterns = new ArrayList<String>();

        public void add(final String path, final Object rule) {
            String pattern = path;
            if (path.startsWith("*/")) {
                pattern = path.substring(1);
                lpatterns.add(pattern);
            } else if (path.endsWith("/*")) {
                pattern = path.substring(0, path.length() - 1);
                rpatterns.add(pattern);
            }
            rules.put(pattern, rule);
        }

        public Object match(final String path) {
            if (rules.containsKey(path)) {
                return rules.get(path);
            }

            int n = path.lastIndexOf('/');
            for (Iterator<String> it = lpatterns.iterator(); it.hasNext();) {
                String pattern = it.next();
                if (path.substring(n).endsWith(pattern)) {
                    return rules.get(pattern);
                }
            }

            for (Iterator<String> it = rpatterns.iterator(); it.hasNext();) {
                String pattern = it.next();
                if (path.startsWith(pattern)) {
                    return rules.get(pattern);
                }
            }

            return null;
        }
    }

    /**
     * Rule
     */
    protected abstract class Rule {

        public void begin(final String name, final Attributes attrs)
                throws SAXException {
        }

        public void end(final String name) {
        }

        protected final Object getValue(final String desc, final String val)
                throws SAXException {
            Object value = null;
            if (val != null) {
                if ("Ljava/lang/String;".equals(desc)) {
                    value = decode(val);
                } else if ("Ljava/lang/Integer;".equals(desc)
                        || "I".equals(desc) || "S".equals(desc)
                        || "B".equals(desc) || "C".equals(desc)
                        || "Z".equals(desc)) {
                    value = new Integer(val);

                } else if ("Ljava/lang/Short;".equals(desc)) {
                    value = new Short(val);

                } else if ("Ljava/lang/Byte;".equals(desc)) {
                    value = new Byte(val);

                } else if ("Ljava/lang/Character;".equals(desc)) {
                    value = new Character(decode(val).charAt(0));

                } else if ("Ljava/lang/Boolean;".equals(desc)) {
                    value = Boolean.valueOf(val);

                } else if ("Ljava/lang/Long;".equals(desc) || "J".equals(desc)) {
                    value = new Long(val);
                } else if ("Ljava/lang/Float;".equals(desc) || "F".equals(desc)) {
                    value = new Float(val);
                } else if ("Ljava/lang/Double;".equals(desc)
                        || "D".equals(desc)) {
                    value = new Double(val);
                } else if (Type.getDescriptor(Type.class).equals(desc)) {
                    value = Type.getType(val);

                } else if (Type.getDescriptor(Handle.class).equals(desc)) {
                    value = decodeHandle(val);

                } else {
                    // TODO use of default toString().
                    throw new SAXException("Invalid value:" + val + " desc:"
                            + desc + " ctx:" + this);
                }
            }
            return value;
        }

        Handle decodeHandle(final String val) throws SAXException {
            try {
                int dotIndex = val.indexOf('.');
                int descIndex = val.indexOf('(', dotIndex + 1);
                int tagIndex = val.lastIndexOf('(');

                int tag = Integer.parseInt(val.substring(tagIndex + 1,
                        val.length() - 1));
                String owner = val.substring(0, dotIndex);
                String name = val.substring(dotIndex + 1, descIndex);
                String desc = val.substring(descIndex, tagIndex - 1);
                return new Handle(tag, owner, name, desc);

            } catch (RuntimeException e) {
                throw new SAXException("Malformed handle " + val, e);
            }
        }

        private final String decode(final String val) throws SAXException {
            StringBuffer sb = new StringBuffer(val.length());
            try {
                int n = 0;
                while (n < val.length()) {
                    char c = val.charAt(n);
                    if (c == '\\') {
                        n++;
                        c = val.charAt(n);
                        if (c == '\\') {
                            sb.append('\\');
                        } else {
                            n++; // skip 'u'
                            sb.append((char) Integer.parseInt(
                                    val.substring(n, n + 4), 16));
                            n += 3;
                        }
                    } else {
                        sb.append(c);
                    }
                    n++;
                }

            } catch (RuntimeException ex) {
                throw new SAXException(ex);
            }
            return sb.toString();
        }

        protected final Label getLabel(final Object label) {
            Label lbl = labels.get(label);
            if (lbl == null) {
                lbl = new Label();
                labels.put(label, lbl);
            }
            return lbl;
        }

        // TODO verify move to stack
        protected final MethodVisitor getCodeVisitor() {
            return (MethodVisitor) peek();
        }

        protected final int getAccess(final String s) {
            int access = 0;
            if (s.indexOf("public") != -1) {
                access |= ACC_PUBLIC;
            }
            if (s.indexOf("private") != -1) {
                access |= ACC_PRIVATE;
            }
            if (s.indexOf("protected") != -1) {
                access |= ACC_PROTECTED;
            }
            if (s.indexOf("static") != -1) {
                access |= ACC_STATIC;
            }
            if (s.indexOf("final") != -1) {
                access |= ACC_FINAL;
            }
            if (s.indexOf("super") != -1) {
                access |= ACC_SUPER;
            }
            if (s.indexOf("synchronized") != -1) {
                access |= ACC_SYNCHRONIZED;
            }
            if (s.indexOf("volatile") != -1) {
                access |= ACC_VOLATILE;
            }
            if (s.indexOf("bridge") != -1) {
                access |= ACC_BRIDGE;
            }
            if (s.indexOf("varargs") != -1) {
                access |= ACC_VARARGS;
            }
            if (s.indexOf("transient") != -1) {
                access |= ACC_TRANSIENT;
            }
            if (s.indexOf("native") != -1) {
                access |= ACC_NATIVE;
            }
            if (s.indexOf("interface") != -1) {
                access |= ACC_INTERFACE;
            }
            if (s.indexOf("abstract") != -1) {
                access |= ACC_ABSTRACT;
            }
            if (s.indexOf("strict") != -1) {
                access |= ACC_STRICT;
            }
            if (s.indexOf("synthetic") != -1) {
                access |= ACC_SYNTHETIC;
            }
            if (s.indexOf("annotation") != -1) {
                access |= ACC_ANNOTATION;
            }
            if (s.indexOf("enum") != -1) {
                access |= ACC_ENUM;
            }
            if (s.indexOf("deprecated") != -1) {
                access |= ACC_DEPRECATED;
            }
            if (s.indexOf("mandated") != -1) {
                access |= ACC_MANDATED;
            }
            return access;
        }
    }

    /**
     * ClassRule
     */
    final class ClassRule extends Rule {

        @Override
        public final void begin(final String name, final Attributes attrs) {
            int major = Integer.parseInt(attrs.getValue("major"));
            int minor = Integer.parseInt(attrs.getValue("minor"));
            HashMap<String, Object> vals = new HashMap<String, Object>();
            vals.put("version", minor << 16 | major);
            vals.put("access", attrs.getValue("access"));
            vals.put("name", attrs.getValue("name"));
            vals.put("parent", attrs.getValue("parent"));
            vals.put("source", attrs.getValue("source"));
            vals.put("signature", attrs.getValue("signature"));
            vals.put("interfaces", new ArrayList<String>());
            push(vals);
            // values will be extracted in InterfacesRule.end();
        }
    }

    final class SourceRule extends Rule {

        @Override
        public void begin(final String name, final Attributes attrs) {
            String file = attrs.getValue("file");
            String debug = attrs.getValue("debug");
            cv.visitSource(file, debug);
        }
    }

    /**
     * InterfaceRule
     */
    final class InterfaceRule extends Rule {

        @Override
        @SuppressWarnings("unchecked")
        public final void begin(final String name, final Attributes attrs) {
            ((ArrayList<String>) ((HashMap<?, ?>) peek()).get("interfaces"))
                    .add(attrs.getValue("name"));
        }
    }

    /**
     * InterfacesRule
     */
    final class InterfacesRule extends Rule {

        @Override
        public final void end(final String element) {
            HashMap<?, ?> vals = (HashMap<?, ?>) pop();
            int version = ((Integer) vals.get("version")).intValue();
            int access = getAccess((String) vals.get("access"));
            String name = (String) vals.get("name");
            String signature = (String) vals.get("signature");
            String parent = (String) vals.get("parent");
            ArrayList<?> infs = (ArrayList<?>) vals.get("interfaces");
            String[] interfaces = infs.toArray(new String[infs.size()]);
            cv.visit(version, access, name, signature, parent, interfaces);
            push(cv);
        }
    }

    /**
     * OuterClassRule
     */
    final class OuterClassRule extends Rule {

        @Override
        public final void begin(final String element, final Attributes attrs) {
            String owner = attrs.getValue("owner");
            String name = attrs.getValue("name");
            String desc = attrs.getValue("desc");
            cv.visitOuterClass(owner, name, desc);
        }
    }

    /**
     * InnerClassRule
     */
    final class InnerClassRule extends Rule {

        @Override
        public final void begin(final String element, final Attributes attrs) {
            int access = getAccess(attrs.getValue("access"));
            String name = attrs.getValue("name");
            String outerName = attrs.getValue("outerName");
            String innerName = attrs.getValue("innerName");
            cv.visitInnerClass(name, outerName, innerName, access);
        }
    }

    /**
     * FieldRule
     */
    final class FieldRule extends Rule {

        @Override
        public final void begin(final String element, final Attributes attrs)
                throws SAXException {
            int access = getAccess(attrs.getValue("access"));
            String name = attrs.getValue("name");
            String signature = attrs.getValue("signature");
            String desc = attrs.getValue("desc");
            Object value = getValue(desc, attrs.getValue("value"));
            push(cv.visitField(access, name, desc, signature, value));
        }

        @Override
        public void end(final String name) {
            ((FieldVisitor) pop()).visitEnd();
        }
    }

    /**
     * MethodRule
     */
    final class MethodRule extends Rule {

        @Override
        public final void begin(final String name, final Attributes attrs) {
            labels = new HashMap<Object, Label>();
            HashMap<String, Object> vals = new HashMap<String, Object>();
            vals.put("access", attrs.getValue("access"));
            vals.put("name", attrs.getValue("name"));
            vals.put("desc", attrs.getValue("desc"));
            vals.put("signature", attrs.getValue("signature"));
            vals.put("exceptions", new ArrayList<String>());
            push(vals);
            // values will be extracted in ExceptionsRule.end();
        }

        @Override
        public final void end(final String name) {
            ((MethodVisitor) pop()).visitEnd();
            labels = null;
        }
    }

    /**
     * ExceptionRule
     */
    final class ExceptionRule extends Rule {

        @Override
        @SuppressWarnings("unchecked")
        public final void begin(final String name, final Attributes attrs) {
            ((ArrayList<String>) ((HashMap<?, ?>) peek()).get("exceptions"))
                    .add(attrs.getValue("name"));
        }
    }

    /**
     * ExceptionsRule
     */
    final class ExceptionsRule extends Rule {

        @Override
        public final void end(final String element) {
            HashMap<?, ?> vals = (HashMap<?, ?>) pop();
            int access = getAccess((String) vals.get("access"));
            String name = (String) vals.get("name");
            String desc = (String) vals.get("desc");
            String signature = (String) vals.get("signature");
            ArrayList<?> excs = (ArrayList<?>) vals.get("exceptions");
            String[] exceptions = excs.toArray(new String[excs.size()]);

            push(cv.visitMethod(access, name, desc, signature, exceptions));
        }
    }

    /**
     * MethodParameterRule
     */
    final class MethodParameterRule extends Rule {
        @Override
        public void begin(final String nm, final Attributes attrs) {
            String name = attrs.getValue("name");
            int access = getAccess(attrs.getValue("access"));
            getCodeVisitor().visitParameter(name, access);
        }
    }

    /**
     * TableSwitchRule
     */
    final class TableSwitchRule extends Rule {

        @Override
        public final void begin(final String name, final Attributes attrs) {
            HashMap<String, Object> vals = new HashMap<String, Object>();
            vals.put("min", attrs.getValue("min"));
            vals.put("max", attrs.getValue("max"));
            vals.put("dflt", attrs.getValue("dflt"));
            vals.put("labels", new ArrayList<String>());
            push(vals);
        }

        @Override
        public final void end(final String name) {
            HashMap<?, ?> vals = (HashMap<?, ?>) pop();
            int min = Integer.parseInt((String) vals.get("min"));
            int max = Integer.parseInt((String) vals.get("max"));
            Label dflt = getLabel(vals.get("dflt"));
            ArrayList<?> lbls = (ArrayList<?>) vals.get("labels");
            Label[] labels = lbls.toArray(new Label[lbls.size()]);
            getCodeVisitor().visitTableSwitchInsn(min, max, dflt, labels);
        }
    }

    /**
     * TableSwitchLabelRule
     */
    final class TableSwitchLabelRule extends Rule {

        @Override
        @SuppressWarnings("unchecked")
        public final void begin(final String name, final Attributes attrs) {
            ((ArrayList<Label>) ((HashMap<?, ?>) peek()).get("labels"))
                    .add(getLabel(attrs.getValue("name")));
        }
    }

    /**
     * LookupSwitchRule
     */
    final class LookupSwitchRule extends Rule {

        @Override
        public final void begin(final String name, final Attributes attrs) {
            HashMap<String, Object> vals = new HashMap<String, Object>();
            vals.put("dflt", attrs.getValue("dflt"));
            vals.put("labels", new ArrayList<Label>());
            vals.put("keys", new ArrayList<String>());
            push(vals);
        }

        @Override
        public final void end(final String name) {
            HashMap<?, ?> vals = (HashMap<?, ?>) pop();
            Label dflt = getLabel(vals.get("dflt"));
            @SuppressWarnings("unchecked")
            ArrayList<String> keyList = (ArrayList<String>) vals.get("keys");
            ArrayList<?> lbls = (ArrayList<?>) vals.get("labels");
            Label[] labels = lbls.toArray(new Label[lbls.size()]);
            int[] keys = new int[keyList.size()];
            for (int i = 0; i < keys.length; i++) {
                keys[i] = Integer.parseInt(keyList.get(i));
            }
            getCodeVisitor().visitLookupSwitchInsn(dflt, keys, labels);
        }
    }

    /**
     * LookupSwitchLabelRule
     */
    final class LookupSwitchLabelRule extends Rule {

        @Override
        @SuppressWarnings("unchecked")
        public final void begin(final String name, final Attributes attrs) {
            HashMap<?, ?> vals = (HashMap<?, ?>) peek();
            ((ArrayList<Label>) vals.get("labels")).add(getLabel(attrs
                    .getValue("name")));
            ((ArrayList<String>) vals.get("keys")).add(attrs.getValue("key"));
        }
    }

    /**
     * FrameRule
     */
    final class FrameRule extends Rule {

        @Override
        public void begin(final String name, final Attributes attrs) {
            HashMap<String, Object> typeLists = new HashMap<String, Object>();
            typeLists.put("local", new ArrayList<Object>());
            typeLists.put("stack", new ArrayList<Object>());
            push(attrs.getValue("type"));
            push(attrs.getValue("count") == null ? "0" : attrs
                    .getValue("count"));
            push(typeLists);
        }

        @Override
        public void end(final String name) {
            HashMap<?, ?> typeLists = (HashMap<?, ?>) pop();
            ArrayList<?> locals = (ArrayList<?>) typeLists.get("local");
            int nLocal = locals.size();
            Object[] local = locals.toArray();
            ArrayList<?> stacks = (ArrayList<?>) typeLists.get("stack");
            int nStack = stacks.size();
            Object[] stack = stacks.toArray();
            String count = (String) pop();
            String type = (String) pop();
            if ("NEW".equals(type)) {
                getCodeVisitor()
                        .visitFrame(F_NEW, nLocal, local, nStack, stack);
            } else if ("FULL".equals(type)) {
                getCodeVisitor().visitFrame(F_FULL, nLocal, local, nStack,
                        stack);
            } else if ("APPEND".equals(type)) {
                getCodeVisitor().visitFrame(F_APPEND, nLocal, local, 0, null);
            } else if ("CHOP".equals(type)) {
                getCodeVisitor().visitFrame(F_CHOP, Integer.parseInt(count),
                        null, 0, null);
            } else if ("SAME".equals(type)) {
                getCodeVisitor().visitFrame(F_SAME, 0, null, 0, null);
            } else if ("SAME1".equals(type)) {
                getCodeVisitor().visitFrame(F_SAME1, 0, null, nStack, stack);
            }
        }
    }

    final class FrameTypeRule extends Rule {

        @Override
        public void begin(final String name, final Attributes attrs) {
            @SuppressWarnings("unchecked")
            ArrayList<Object> types = (ArrayList<Object>) ((HashMap<?, ?>) peek())
                    .get(name);
            String type = attrs.getValue("type");
            if ("uninitialized".equals(type)) {
                types.add(getLabel(attrs.getValue("label")));
            } else {
                Integer t = TYPES.get(type);
                if (t == null) {
                    types.add(type);
                } else {
                    types.add(t);
                }
            }
        }
    }

    /**
     * LabelRule
     */
    final class LabelRule extends Rule {

        @Override
        public final void begin(final String name, final Attributes attrs) {
            getCodeVisitor().visitLabel(getLabel(attrs.getValue("name")));
        }
    }

    /**
     * TryCatchRule
     */
    final class TryCatchRule extends Rule {

        @Override
        public final void begin(final String name, final Attributes attrs) {
            Label start = getLabel(attrs.getValue("start"));
            Label end = getLabel(attrs.getValue("end"));
            Label handler = getLabel(attrs.getValue("handler"));
            String type = attrs.getValue("type");
            getCodeVisitor().visitTryCatchBlock(start, end, handler, type);
        }
    }

    /**
     * LineNumberRule
     */
    final class LineNumberRule extends Rule {

        @Override
        public final void begin(final String name, final Attributes attrs) {
            int line = Integer.parseInt(attrs.getValue("line"));
            Label start = getLabel(attrs.getValue("start"));
            getCodeVisitor().visitLineNumber(line, start);
        }
    }

    /**
     * LocalVarRule
     */
    final class LocalVarRule extends Rule {

        @Override
        public final void begin(final String element, final Attributes attrs) {
            String name = attrs.getValue("name");
            String desc = attrs.getValue("desc");
            String signature = attrs.getValue("signature");
            Label start = getLabel(attrs.getValue("start"));
            Label end = getLabel(attrs.getValue("end"));
            int var = Integer.parseInt(attrs.getValue("var"));
            getCodeVisitor().visitLocalVariable(name, desc, signature, start,
                    end, var);
        }
    }

    /**
     * InvokeDynamicRule
     */
    final class InvokeDynamicRule extends Rule {
        @Override
        public final void begin(final String element, final Attributes attrs)
                throws SAXException {
            push(attrs.getValue("name"));
            push(attrs.getValue("desc"));
            push(decodeHandle(attrs.getValue("bsm")));
            push(new ArrayList<Object>());
        }

        @Override
        public final void end(final String element) {
            ArrayList<?> bsmArgs = (ArrayList<?>) pop();
            Handle bsm = (Handle) pop();
            String desc = (String) pop();
            String name = (String) pop();
            getCodeVisitor().visitInvokeDynamicInsn(name, desc, bsm,
                    bsmArgs.toArray());
        }
    }

    /**
     * InvokeDynamicBsmArgumentsRule
     */
    final class InvokeDynamicBsmArgumentsRule extends Rule {
        @Override
        public final void begin(final String element, final Attributes attrs)
                throws SAXException {
            @SuppressWarnings("unchecked")
            ArrayList<Object> bsmArgs = (ArrayList<Object>) peek();
            bsmArgs.add(getValue(attrs.getValue("desc"), attrs.getValue("cst")));
        }
    }

    /**
     * OpcodesRule
     */
    final class OpcodesRule extends Rule {

        // public boolean match( String match, String element) {
        // return match.startsWith( path) && OPCODES.containsKey( element);
        // }

        @Override
        public final void begin(final String element, final Attributes attrs)
                throws SAXException {
            Opcode o = OPCODES.get(element);
            if (o == null) {
                throw new SAXException("Invalid element: " + element + " at "
                        + match);
            }

            switch (o.type) {
            case OpcodeGroup.INSN:
                getCodeVisitor().visitInsn(o.opcode);
                break;

            case OpcodeGroup.INSN_FIELD:
                getCodeVisitor().visitFieldInsn(o.opcode,
                        attrs.getValue("owner"), attrs.getValue("name"),
                        attrs.getValue("desc"));
                break;

            case OpcodeGroup.INSN_INT:
                getCodeVisitor().visitIntInsn(o.opcode,
                        Integer.parseInt(attrs.getValue("value")));
                break;

            case OpcodeGroup.INSN_JUMP:
                getCodeVisitor().visitJumpInsn(o.opcode,
                        getLabel(attrs.getValue("label")));
                break;

            case OpcodeGroup.INSN_METHOD:
                getCodeVisitor().visitMethodInsn(o.opcode,
                        attrs.getValue("owner"), attrs.getValue("name"),
                        attrs.getValue("desc"),
                        attrs.getValue("itf").equals("true"));
                break;

            case OpcodeGroup.INSN_TYPE:
                getCodeVisitor()
                        .visitTypeInsn(o.opcode, attrs.getValue("desc"));
                break;

            case OpcodeGroup.INSN_VAR:
                getCodeVisitor().visitVarInsn(o.opcode,
                        Integer.parseInt(attrs.getValue("var")));
                break;

            case OpcodeGroup.INSN_IINC:
                getCodeVisitor().visitIincInsn(
                        Integer.parseInt(attrs.getValue("var")),
                        Integer.parseInt(attrs.getValue("inc")));
                break;

            case OpcodeGroup.INSN_LDC:
                getCodeVisitor()
                        .visitLdcInsn(
                                getValue(attrs.getValue("desc"),
                                        attrs.getValue("cst")));
                break;

            case OpcodeGroup.INSN_MULTIANEWARRAY:
                getCodeVisitor().visitMultiANewArrayInsn(
                        attrs.getValue("desc"),
                        Integer.parseInt(attrs.getValue("dims")));
                break;

            default:
                throw new Error("Internal error");

            }
        }
    }

    /**
     * MaxRule
     */
    final class MaxRule extends Rule {

        @Override
        public final void begin(final String element, final Attributes attrs) {
            int maxStack = Integer.parseInt(attrs.getValue("maxStack"));
            int maxLocals = Integer.parseInt(attrs.getValue("maxLocals"));
            getCodeVisitor().visitMaxs(maxStack, maxLocals);
        }
    }

    final class AnnotationRule extends Rule {

        @Override
        public void begin(final String name, final Attributes attrs) {
            String desc = attrs.getValue("desc");
            boolean visible = Boolean.valueOf(attrs.getValue("visible"))
                    .booleanValue();

            Object v = peek();
            if (v instanceof ClassVisitor) {
                push(((ClassVisitor) v).visitAnnotation(desc, visible));
            } else if (v instanceof FieldVisitor) {
                push(((FieldVisitor) v).visitAnnotation(desc, visible));
            } else if (v instanceof MethodVisitor) {
                push(((MethodVisitor) v).visitAnnotation(desc, visible));
            }
        }

        @Override
        public void end(final String name) {
            AnnotationVisitor av = (AnnotationVisitor) pop();
            if (av != null) {
                av.visitEnd();
            }
        }
    }

    final class TypeAnnotationRule extends Rule {

        @Override
        public void begin(final String name, final Attributes attrs) {
            String desc = attrs.getValue("desc");
            boolean visible = Boolean.valueOf(attrs.getValue("visible"))
                    .booleanValue();
            int typeRef = Integer.parseInt(attrs.getValue("typeRef"));
            TypePath typePath = TypePath.fromString(attrs.getValue("typePath"));

            Object v = peek();
            if (v instanceof ClassVisitor) {
                push(((ClassVisitor) v).visitTypeAnnotation(typeRef, typePath,
                        desc, visible));
            } else if (v instanceof FieldVisitor) {
                push(((FieldVisitor) v).visitTypeAnnotation(typeRef, typePath,
                        desc, visible));
            } else if (v instanceof MethodVisitor) {
                push(((MethodVisitor) v).visitTypeAnnotation(typeRef, typePath,
                        desc, visible));
            }
        }

        @Override
        public void end(final String name) {
            AnnotationVisitor av = (AnnotationVisitor) pop();
            if (av != null) {
                av.visitEnd();
            }
        }
    }

    final class AnnotationParameterRule extends Rule {

        @Override
        public void begin(final String name, final Attributes attrs) {
            int parameter = Integer.parseInt(attrs.getValue("parameter"));
            String desc = attrs.getValue("desc");
            boolean visible = Boolean.valueOf(attrs.getValue("visible"))
                    .booleanValue();

            push(((MethodVisitor) peek()).visitParameterAnnotation(parameter,
                    desc, visible));
        }

        @Override
        public void end(final String name) {
            AnnotationVisitor av = (AnnotationVisitor) pop();
            if (av != null) {
                av.visitEnd();
            }
        }
    }

    final class InsnAnnotationRule extends Rule {

        @Override
        public void begin(final String name, final Attributes attrs) {
            String desc = attrs.getValue("desc");
            boolean visible = Boolean.valueOf(attrs.getValue("visible"))
                    .booleanValue();
            int typeRef = Integer.parseInt(attrs.getValue("typeRef"));
            TypePath typePath = TypePath.fromString(attrs.getValue("typePath"));
            push(((MethodVisitor) peek()).visitInsnAnnotation(typeRef,
                    typePath, desc, visible));
        }

        @Override
        public void end(final String name) {
            AnnotationVisitor av = (AnnotationVisitor) pop();
            if (av != null) {
                av.visitEnd();
            }
        }
    }

    final class TryCatchAnnotationRule extends Rule {

        @Override
        public void begin(final String name, final Attributes attrs) {
            String desc = attrs.getValue("desc");
            boolean visible = Boolean.valueOf(attrs.getValue("visible"))
                    .booleanValue();
            int typeRef = Integer.parseInt(attrs.getValue("typeRef"));
            TypePath typePath = TypePath.fromString(attrs.getValue("typePath"));
            push(((MethodVisitor) peek()).visitTryCatchAnnotation(typeRef,
                    typePath, desc, visible));
        }

        @Override
        public void end(final String name) {
            AnnotationVisitor av = (AnnotationVisitor) pop();
            if (av != null) {
                av.visitEnd();
            }
        }
    }

    final class LocalVariableAnnotationRule extends Rule {

        @Override
        public void begin(final String name, final Attributes attrs) {
            String desc = attrs.getValue("desc");
            boolean visible = Boolean.valueOf(attrs.getValue("visible"))
                    .booleanValue();
            int typeRef = Integer.parseInt(attrs.getValue("typeRef"));
            TypePath typePath = TypePath.fromString(attrs.getValue("typePath"));
            String[] s = attrs.getValue("start").split(" ");
            Label[] start = new Label[s.length];
            for (int i = 0; i < start.length; ++i) {
                start[i] = getLabel(s[i]);
            }
            String[] e = attrs.getValue("end").split(" ");
            Label[] end = new Label[e.length];
            for (int i = 0; i < end.length; ++i) {
                end[i] = getLabel(e[i]);
            }
            String[] v = attrs.getValue("index").split(" ");
            int[] index = new int[v.length];
            for (int i = 0; i < index.length; ++i) {
                index[i] = Integer.parseInt(v[i]);
            }
            push(((MethodVisitor) peek()).visitLocalVariableAnnotation(typeRef,
                    typePath, start, end, index, desc, visible));
        }

        @Override
        public void end(final String name) {
            AnnotationVisitor av = (AnnotationVisitor) pop();
            if (av != null) {
                av.visitEnd();
            }
        }
    }

    final class AnnotationValueRule extends Rule {

        @Override
        public void begin(final String nm, final Attributes attrs)
                throws SAXException {
            AnnotationVisitor av = (AnnotationVisitor) peek();
            if (av != null) {
                av.visit(
                        attrs.getValue("name"),
                        getValue(attrs.getValue("desc"),
                                attrs.getValue("value")));
            }
        }
    }

    final class AnnotationValueEnumRule extends Rule {

        @Override
        public void begin(final String nm, final Attributes attrs) {
            AnnotationVisitor av = (AnnotationVisitor) peek();
            if (av != null) {
                av.visitEnum(attrs.getValue("name"), attrs.getValue("desc"),
                        attrs.getValue("value"));
            }
        }
    }

    final class AnnotationValueAnnotationRule extends Rule {

        @Override
        public void begin(final String nm, final Attributes attrs) {
            AnnotationVisitor av = (AnnotationVisitor) peek();
            push(av == null ? null : av.visitAnnotation(attrs.getValue("name"),
                    attrs.getValue("desc")));
        }

        @Override
        public void end(final String name) {
            AnnotationVisitor av = (AnnotationVisitor) pop();
            if (av != null) {
                av.visitEnd();
            }
        }
    }

    final class AnnotationValueArrayRule extends Rule {

        @Override
        public void begin(final String nm, final Attributes attrs) {
            AnnotationVisitor av = (AnnotationVisitor) peek();
            push(av == null ? null : av.visitArray(attrs.getValue("name")));
        }

        @Override
        public void end(final String name) {
            AnnotationVisitor av = (AnnotationVisitor) pop();
            if (av != null) {
                av.visitEnd();
            }
        }
    }

    final class AnnotationDefaultRule extends Rule {

        @Override
        public void begin(final String nm, final Attributes attrs) {
            MethodVisitor av = (MethodVisitor) peek();
            push(av == null ? null : av.visitAnnotationDefault());
        }

        @Override
        public void end(final String name) {
            AnnotationVisitor av = (AnnotationVisitor) pop();
            if (av != null) {
                av.visitEnd();
            }
        }
    }

    /**
     * Opcode
     */
    static final class Opcode {

        public final int opcode;

        public final int type;

        Opcode(final int opcode, final int type) {
            this.opcode = opcode;
            this.type = type;
        }
    }
}
