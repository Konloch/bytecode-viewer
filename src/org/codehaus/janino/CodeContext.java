
/*
 * Janino - An embedded Java[TM] compiler
 *
 * Copyright (c) 2001-2010, Arno Unkrig
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.codehaus.janino;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.janino.util.ClassFile;

/**
 * The context of the compilation of a function (constructor or method). Manages generation of
 * byte code, the exception table, generation of line number tables, allocation of local variables,
 * determining of stack size and local variable table size and flow analysis.
 */
@SuppressWarnings({ "rawtypes", "unchecked" }) public
class CodeContext {
    private static final boolean DEBUG = false;

    private static final int     INITIAL_SIZE   = 128;
    private static final byte    UNEXAMINED     = -1;
    private static final byte    INVALID_OFFSET = -2;
    private static final int     MAX_STACK_SIZE = 254;

    private final ClassFile classFile;
    private final String    functionName;

    private short                           maxStack;
    private short                           maxLocals;
    private byte[]                          code;
    private final Offset                    beginning;
    private final Inserter                  end;
    private Inserter                        currentInserter;
    private final List<ExceptionTableEntry> exceptionTableEntries;

    /** All the local variables that are allocated in any block in this {@link CodeContext}. */
    private final List<Java.LocalVariableSlot> allLocalVars = new ArrayList();

    /**
     * List of List of Java.LocalVariableSlot objects. Each List of Java.LocalVariableSlot is
     * the local variables allocated for a block. They are pushed and poped onto the list together
     * to make allocation of the next local variable slot easy.
     */
    private final List<List<Java.LocalVariableSlot>> scopedVars = new ArrayList();

    private short                   nextLocalVariableSlot;
    private final List<Relocatable> relocatables = new ArrayList();

    /** Creates an empty "Code" attribute. */
    public
    CodeContext(ClassFile classFile, String functionName) {
        this.classFile    = classFile;
        this.functionName = functionName;

        this.maxStack              = 0;
        this.maxLocals             = 0;
        this.code                  = new byte[CodeContext.INITIAL_SIZE];
        this.beginning             = new Offset();
        this.end                   = new Inserter();
        this.currentInserter       = this.end;
        this.exceptionTableEntries = new ArrayList();

        this.beginning.offset = 0;
        this.end.offset       = 0;
        this.beginning.next   = this.end;
        this.end.prev         = this.beginning;
    }

    /** The {@link ClassFile} this context is related to. */
    public ClassFile
    getClassFile() { return this.classFile; }


    /**
     * Allocate space for a local variable of the given size (1 or 2)
     * on the local variable array.
     *
     * As a side effect, the "max_locals" field of the "Code" attribute
     * is updated.
     *
     * The only way to deallocate local variables is to
     * {@link #saveLocalVariables()} and later {@link
     * #restoreLocalVariables()}.
     *
     * @param size The number of slots to allocate (1 or 2)
     * @return The slot index of the allocated variable
     */
    public short
    allocateLocalVariable(short size) { return this.allocateLocalVariable(size, null, null).getSlotIndex(); }

    /**
     * Allocate space for a local variable of the given size (1 or 2)
     * on the local variable array.
     *
     * As a side effect, the "max_locals" field of the "Code" attribute
     * is updated.
     *
     * The only way to deallocate local variables is to
     * {@link #saveLocalVariables()} and later {@link
     * #restoreLocalVariables()}.
     * @param size Number of slots to use (1 or 2)
     * @param name The variable name, if it's null, the variable won't be written to the localvariabletable
     * @param type The variable type. if the name isn't null, the type is needed to write to the localvariabletable
     */
    public Java.LocalVariableSlot
    allocateLocalVariable(short size, String name, IClass type) {
        List<Java.LocalVariableSlot> currentVars = null;

        if (this.scopedVars.size() == 0) {
            throw new Error("saveLocalVariables must be called first");
        } else {
            currentVars = (List<Java.LocalVariableSlot>) this.scopedVars.get(this.scopedVars.size() - 1);
        }

        Java.LocalVariableSlot slot = new Java.LocalVariableSlot(name, this.nextLocalVariableSlot, type);

        if (slot.getName() != null) {
            slot.setStart(this.newOffset());
        }

        this.nextLocalVariableSlot += size;
        currentVars.add(slot);
        this.allLocalVars.add(slot);

        if (this.nextLocalVariableSlot > this.maxLocals) {
            this.maxLocals = this.nextLocalVariableSlot;
        }

        return slot;
    }

    /** Remembers the current size of the local variables array. */
    public List<Java.LocalVariableSlot>
    saveLocalVariables() {

        // Push empty list on the stack to hold a new block's local vars.
        List<Java.LocalVariableSlot> l = new ArrayList();
        this.scopedVars.add(l);

        return l;
    }

    /**
     * Restore the previous size of the local variables array. This MUST to be called for every call
     * to saveLocalVariables as it closes the variable extent for all the active local variables in
     * the current block.
     */
    public void
    restoreLocalVariables() {

        // Pop the list containing the current block's local vars.
        List<Java.LocalVariableSlot> slots = (
            (List<Java.LocalVariableSlot>) this.scopedVars.remove(this.scopedVars.size() - 1)
        );
        for (Java.LocalVariableSlot slot : slots) {
            if (slot.getName() != null) {
                slot.setEnd(this.newOffset());
            }
        }
    }

    /**
     *
     * @param dos
     * @param lineNumberTableAttributeNameIndex 0 == don't generate a "LineNumberTable" attribute
     * @throws IOException
     */
    protected void
    storeCodeAttributeBody(
        DataOutputStream dos,
        short            lineNumberTableAttributeNameIndex,
        short            localVariableTableAttributeNameIndex
    ) throws IOException {
        dos.writeShort(this.maxStack);                                               // max_stack
        dos.writeShort(this.maxLocals);                                              // max_locals
        dos.writeInt(this.end.offset);                                               // code_length
        dos.write(this.code, 0, this.end.offset);                                    // code
        dos.writeShort(this.exceptionTableEntries.size());                           // exception_table_length
        for (ExceptionTableEntry exceptionTableEntry : this.exceptionTableEntries) { // exception_table
            dos.writeShort(exceptionTableEntry.startPC.offset);
            dos.writeShort(exceptionTableEntry.endPC.offset);
            dos.writeShort(exceptionTableEntry.handlerPC.offset);
            dos.writeShort(exceptionTableEntry.catchType);
        }

        List<ClassFile.AttributeInfo> attributes = new ArrayList();

        // Add "LineNumberTable" attribute.
        if (lineNumberTableAttributeNameIndex != 0) {
            List<ClassFile.LineNumberTableAttribute.Entry> lnt = new ArrayList();
            for (Offset o = this.beginning; o != null; o = o.next) {
                if (o instanceof LineNumberOffset) {
                    lnt.add(new ClassFile.LineNumberTableAttribute.Entry(o.offset, ((LineNumberOffset) o).lineNumber));
                }
            }
            ClassFile.LineNumberTableAttribute.Entry[] lnte = (ClassFile.LineNumberTableAttribute.Entry[]) lnt.toArray(
                new ClassFile.LineNumberTableAttribute.Entry[lnt.size()]
            );
            attributes.add(new ClassFile.LineNumberTableAttribute(
                lineNumberTableAttributeNameIndex, // attributeNameIndex
                lnte                               // lineNumberTableEntries
            ));
        }

        // Add "LocalVariableTable" attribute.
        if (localVariableTableAttributeNameIndex != 0) {
            ClassFile.AttributeInfo ai = this.storeLocalVariableTable(dos, localVariableTableAttributeNameIndex);

            if (ai != null) attributes.add(ai);
        }

        dos.writeShort(attributes.size());                     // attributes_count
        for (ClassFile.AttributeInfo attribute : attributes) { // attributes;
            attribute.store(dos);
        }
    }

    /**
     * @return A {@link org.codehaus.janino.util.ClassFile.LocalVariableTableAttribute} for this {@link CodeContext}
     */
    protected ClassFile.AttributeInfo
    storeLocalVariableTable(DataOutputStream dos, short localVariableTableAttributeNameIndex) {
        ClassFile                                               cf        = this.getClassFile();
        final List<ClassFile.LocalVariableTableAttribute.Entry> entryList = new ArrayList();

        for (Java.LocalVariableSlot slot : this.getAllLocalVars()) {

            if (slot.getName() != null) {
                String typeName    = slot.getType().getDescriptor();
                short  classSlot   = cf.addConstantUtf8Info(typeName);
                short  varNameSlot = cf.addConstantUtf8Info(slot.getName());

//                System.out.println("slot: " + slot + ", typeSlot: " + classSlot + ", varSlot: " + varNameSlot);

                ClassFile.LocalVariableTableAttribute.Entry entry = new ClassFile.LocalVariableTableAttribute.Entry(
                    (short) slot.getStart().offset,
                    (short) (slot.getEnd().offset - slot.getStart().offset),
                    varNameSlot,
                    classSlot,
                    slot.getSlotIndex()
                );
                entryList.add(entry);
            }
        }

        if (entryList.size() > 0) {
            Object entries = entryList.toArray(new ClassFile.LocalVariableTableAttribute.Entry[entryList.size()]);

            return new ClassFile.LocalVariableTableAttribute(
                localVariableTableAttributeNameIndex,
                (ClassFile.LocalVariableTableAttribute.Entry[]) entries
            );
        } else {
            return null;
        }
    }

    /**
     * Checks the code for consistency; updates the "maxStack" member.
     *
     * Notice: On inconsistencies, a "RuntimeException" is thrown (KLUDGE).
     */
    public void
    flowAnalysis(String functionName) {
        if (CodeContext.DEBUG) {
            System.err.println("flowAnalysis(" + functionName + ")");
        }

        short[] stackSizes = new short[this.end.offset];
        Arrays.fill(stackSizes, CodeContext.UNEXAMINED);

        // Analyze flow from offset zero.
        this.flowAnalysis(
            functionName,
            this.code,       // code
            this.end.offset, // codeSize
            0,               // offset
            (short) 0,       // stackSize
            stackSizes       // stackSizes
        );

        // Analyze flow from exception handler entry points.
        int analyzedExceptionHandlers = 0;
        while (analyzedExceptionHandlers != this.exceptionTableEntries.size()) {
            for (ExceptionTableEntry exceptionTableEntry : this.exceptionTableEntries) {
                if (stackSizes[exceptionTableEntry.startPC.offset] != CodeContext.UNEXAMINED) {
                    this.flowAnalysis(
                        functionName,
                        this.code,                                                    // code
                        this.end.offset,                                              // codeSize
                        exceptionTableEntry.handlerPC.offset,                         // offset
                        (short) (stackSizes[exceptionTableEntry.startPC.offset] + 1), // stackSize
                        stackSizes                                                    // stackSizes
                    );
                    ++analyzedExceptionHandlers;
                }
            }
        }

        // Check results and determine maximum stack size.
        this.maxStack = 0;
        for (int i = 0; i < stackSizes.length; ++i) {
            short ss = stackSizes[i];
            if (ss == CodeContext.UNEXAMINED) {
                if (CodeContext.DEBUG) {
                    System.out.println(functionName + ": Unexamined code at offset " + i);
                    return;
                } else {
                    throw new JaninoRuntimeException(functionName + ": Unexamined code at offset " + i);
                }
            }
            if (ss > this.maxStack) this.maxStack = ss;
        }
    }

    private void
    flowAnalysis(
        String  functionName,
        byte[]  code,      // Bytecode
        int     codeSize,  // Size
        int     offset,    // Current PC
        short   stackSize, // Stack size on entry
        short[] stackSizes // Stack sizes in code
    ) {
        for (;;) {
            if (CodeContext.DEBUG) System.out.println("Offset = " + offset + ", stack size = " + stackSize);

            // Check current bytecode offset.
            if (offset < 0 || offset >= codeSize) {
                throw new JaninoRuntimeException(functionName + ": Offset out of range");
            }

            // Have we hit an area that has already been analyzed?
            int css = stackSizes[offset];
            if (css == stackSize) return; // OK.
            if (css == CodeContext.INVALID_OFFSET) throw new JaninoRuntimeException(functionName + ": Invalid offset");
            if (css != CodeContext.UNEXAMINED) {
                if (CodeContext.DEBUG) {
                    System.err.println(
                        functionName
                        + ": Operand stack inconsistent at offset "
                        + offset
                        + ": Previous size "
                        + css
                        + ", now "
                        + stackSize
                    );
                    return;
                } else {
                    throw new JaninoRuntimeException(
                        functionName
                        + ": Operand stack inconsistent at offset "
                        + offset
                        + ": Previous size "
                        + css
                        + ", now "
                        + stackSize
                    );
                }
            }
            stackSizes[offset] = stackSize;

            // Analyze current opcode.
            byte  opcode        = code[offset];
            int   operandOffset = offset + 1;
            short props;
            if (opcode == Opcode.WIDE) {
                opcode = code[operandOffset++];
                props  = Opcode.WIDE_OPCODE_PROPERTIES[0xff & opcode];
            } else {
                props = Opcode.OPCODE_PROPERTIES[0xff & opcode];
            }
            if (props == Opcode.INVALID_OPCODE) {
                throw new JaninoRuntimeException(
                    functionName
                    + ": Invalid opcode "
                    + (0xff & opcode)
                    + " at offset "
                    + offset
                );
            }

            switch (props & Opcode.SD_MASK) {

            case Opcode.SD_M4:
            case Opcode.SD_M3:
            case Opcode.SD_M2:
            case Opcode.SD_M1:
            case Opcode.SD_P0:
            case Opcode.SD_P1:
            case Opcode.SD_P2:
                stackSize += (props & Opcode.SD_MASK) - Opcode.SD_P0;
                break;

            case Opcode.SD_0:
                stackSize = 0;
                break;

            case Opcode.SD_GETFIELD:
                --stackSize;
                /* FALL THROUGH */
            case Opcode.SD_GETSTATIC:
                stackSize += this.determineFieldSize((short) (
                    CodeContext.extract16BitValue(0, operandOffset, code)
                ));
                break;

            case Opcode.SD_PUTFIELD:
                --stackSize;
                /* FALL THROUGH */
            case Opcode.SD_PUTSTATIC:
                stackSize -= this.determineFieldSize((short) (
                    CodeContext.extract16BitValue(0, operandOffset, code)
                ));
                break;

            case Opcode.SD_INVOKEVIRTUAL:
            case Opcode.SD_INVOKESPECIAL:
            case Opcode.SD_INVOKEINTERFACE:
                --stackSize;
                /* FALL THROUGH */
            case Opcode.SD_INVOKESTATIC:
                stackSize -= this.determineArgumentsSize((short) (
                    CodeContext.extract16BitValue(0, operandOffset, code)
                ));
                break;

            case Opcode.SD_MULTIANEWARRAY:
                stackSize -= code[operandOffset + 2] - 1;
                break;

            default:
                throw new JaninoRuntimeException(functionName + ": Invalid stack delta");
            }

            if (stackSize < 0) {
                String msg = (
                    this.classFile.getThisClassName()
                    + '.'
                    + functionName
                    + ": Operand stack underrun at offset "
                    + offset
                );
                if (CodeContext.DEBUG) {
                    System.err.println(msg);
                    return;
                } else {
                    throw new JaninoRuntimeException(msg);
                }
            }

            if (stackSize > CodeContext.MAX_STACK_SIZE) {
                String msg = (
                    this.classFile.getThisClassName()
                    + '.'
                    + functionName
                    + ": Operand stack overflow at offset "
                    + offset
                );
                if (CodeContext.DEBUG) {
                    System.err.println(msg);
                    return;
                } else {
                    throw new JaninoRuntimeException(msg);
                }
            }

            switch (props & Opcode.OP1_MASK) {

            case 0:
                ;
                break;

            case Opcode.OP1_SB:
            case Opcode.OP1_UB:
            case Opcode.OP1_CP1:
            case Opcode.OP1_LV1:
                ++operandOffset;
                break;

            case Opcode.OP1_SS:
            case Opcode.OP1_CP2:
            case Opcode.OP1_LV2:
                operandOffset += 2;
                break;

            case Opcode.OP1_BO2:
                if (CodeContext.DEBUG) {
                    System.out.println("Offset = " + offset);
                    System.out.println("Operand offset = " + operandOffset);
                    System.out.println(code[operandOffset]);
                    System.out.println(code[operandOffset + 1]);
                }
                this.flowAnalysis(
                    functionName,
                    code, codeSize,
                    CodeContext.extract16BitValue(offset, operandOffset, code),
                    stackSize,
                    stackSizes
                );
                operandOffset += 2;
                break;

            case Opcode.OP1_JSR:
                if (CodeContext.DEBUG) {
                    System.out.println("Offset = " + offset);
                    System.out.println("Operand offset = " + operandOffset);
                    System.out.println(code[operandOffset]);
                    System.out.println(code[operandOffset + 1]);
                }
                int targetOffset = CodeContext.extract16BitValue(offset, operandOffset, code);
                operandOffset += 2;
                if (stackSizes[targetOffset] == CodeContext.UNEXAMINED) {
                    this.flowAnalysis(
                        functionName,
                        code, codeSize,
                        targetOffset,
                        (short) (stackSize + 1),
                        stackSizes
                    );
                }
                break;

            case Opcode.OP1_BO4:
                this.flowAnalysis(
                    functionName,
                    code, codeSize,
                    CodeContext.extract32BitValue(offset, operandOffset, code),
                    stackSize, stackSizes
                );
                operandOffset += 4;
                break;

            case Opcode.OP1_LOOKUPSWITCH:
                while ((operandOffset & 3) != 0) ++operandOffset;
                this.flowAnalysis(
                    functionName,
                    code, codeSize,
                    CodeContext.extract32BitValue(offset, operandOffset, code),
                    stackSize, stackSizes
                );
                operandOffset += 4;

                int npairs = CodeContext.extract32BitValue(0, operandOffset, code);
                operandOffset += 4;

                for (int i = 0; i < npairs; ++i) {
                    operandOffset += 4; //skip match value
                    this.flowAnalysis(
                        functionName,
                        code, codeSize,
                        CodeContext.extract32BitValue(offset, operandOffset, code),
                        stackSize, stackSizes
                    );
                    operandOffset += 4; //advance over offset
                }
                break;

            case Opcode.OP1_TABLESWITCH:
                while ((operandOffset & 3) != 0) ++operandOffset;
                this.flowAnalysis(
                    functionName,
                    code, codeSize,
                    CodeContext.extract32BitValue(offset, operandOffset, code),
                    stackSize, stackSizes
                );
                operandOffset += 4;
                int low = CodeContext.extract32BitValue(offset, operandOffset, code);
                operandOffset += 4;
                int hi = CodeContext.extract32BitValue(offset, operandOffset, code);
                operandOffset += 4;
                for (int i = low; i <= hi; ++i) {
                    this.flowAnalysis(
                        functionName,
                        code, codeSize,
                        CodeContext.extract32BitValue(offset, operandOffset, code),
                        stackSize, stackSizes
                    );
                    operandOffset += 4;
                }
                break;

            default:
                throw new JaninoRuntimeException(functionName + ": Invalid OP1");
            }

            switch (props & Opcode.OP2_MASK) {

            case 0:
                ;
                break;

            case Opcode.OP2_SB:
                ++operandOffset;
                break;

            case Opcode.OP2_SS:
                operandOffset += 2;
                break;

            default:
                throw new JaninoRuntimeException(functionName + ": Invalid OP2");
            }

            switch (props & Opcode.OP3_MASK) {

            case 0:
                ;
                break;

            case Opcode.OP3_SB:
                ++operandOffset;
                break;

            default:
                throw new JaninoRuntimeException(functionName + ": Invalid OP3");
            }

            Arrays.fill(stackSizes, offset + 1, operandOffset, CodeContext.INVALID_OFFSET);

            if ((props & Opcode.NO_FALLTHROUGH) != 0) return;
            offset = operandOffset;
        }
    }

    /**
     * Extract a 16 bit value at offset in code and add bias to it
     *
     * @param bias   An int to skew the final result by (useful for calculating relative offsets)
     * @param offset The position in the code array to extract the bytes from
     * @param code   The array of bytes
     * @return       An integer that treats the two bytes at position offset as an UNSIGNED SHORT
     */
    private static int
    extract16BitValue(int bias, int offset, byte[] code) {
        int res = bias + (
            ((code[offset]) << 8)
            + (code[offset + 1] & 0xff)
        );
        if (CodeContext.DEBUG) {
            System.out.println("extract16BitValue(bias, offset) = (" + bias + ", " + offset + ")");
            System.out.println("bytes = {" + code[offset] + ", " + code[offset + 1] + "}");
            System.out.println("result = " + res);
        }
        return res;
    }

    /**
     * Extract a 32 bit value at offset in code and add bias to it
     *
     * @param bias   An int to skew the final result by (useful for calculating relative offsets)
     * @param offset The position in the code array to extract the bytes from
     * @param code   The array of bytes
     * @return       The 4 bytes at position offset + bias
     */
    private static int
    extract32BitValue(int bias, int offset, byte[] code) {
        int res = bias + (
            (code[offset] << 24)
            + ((0xff & code[offset + 1]) << 16)
            + ((0xff & code[offset + 2]) << 8)
            + (0xff & code[offset + 3])
        );
        if (CodeContext.DEBUG) {
            System.out.println("extract32BitValue(bias, offset) = (" + bias + ", " + offset + ")");
            System.out.println(
                ""
                + "bytes = {"
                + code[offset]
                + ", "
                + code[offset + 1]
                + ", "
                + code[offset + 2]
                + ", "
                + code[offset + 3]
                + "}"
            );
            System.out.println("result = " + res);
        }
        return res;
    }

    /** Fixes up all of the offsets and relocate() all relocatables. */
    public void
    fixUpAndRelocate() {

        // We do this in a loop to allow relocatables to adjust the size
        // of things in the byte stream.  It is extremely unlikely, but possible
        // that a late relocatable will grow the size of the bytecode, and require
        // an earlier relocatable to switch from 32K mode to 64K mode branching
        do {
            this.fixUp();
        } while (!this.relocate());
    }

    /** Fixes up all offsets. */
    private void
    fixUp() {
        for (Offset o = this.beginning; o != this.end; o = o.next) {
            if (o instanceof FixUp) ((FixUp) o).fixUp();
        }
    }

    /**
     * Relocate all relocatables and aggregate their response into a single one
     * @return true if all of them relocated successfully
     *         false if any of them needed to change size
     */
    private boolean
    relocate() {
        boolean finished = true;
        for (Relocatable relocatable : this.relocatables) {

            // Do not terminate earlier so that everything gets a chance to grow in the first pass changes the common
            // case for this to be O(n) instead of O(n**2).
            finished &= relocatable.relocate();
        }
        return finished;
    }

    /** Analyses the descriptor of the Fieldref and return its size. */
    private int
    determineFieldSize(short idx) {
        ClassFile.ConstantFieldrefInfo cfi = (
            (ClassFile.ConstantFieldrefInfo) this.classFile.getConstantPoolInfo(idx)
        );
        return Descriptor.size(cfi.getNameAndType(this.classFile).getDescriptor(this.classFile));
    }

    /**
     * Analyse the descriptor of the Methodref and return the sum of the
     * arguments' sizes minus the return value's size.
     */
    private int
    determineArgumentsSize(short idx) {
        ClassFile.ConstantPoolInfo        cpi = this.classFile.getConstantPoolInfo(idx);
        ClassFile.ConstantNameAndTypeInfo nat = (
            cpi instanceof ClassFile.ConstantInterfaceMethodrefInfo
            ? ((ClassFile.ConstantInterfaceMethodrefInfo) cpi).getNameAndType(this.classFile)
            : ((ClassFile.ConstantMethodrefInfo)          cpi).getNameAndType(this.classFile)
        );
        String desc = nat.getDescriptor(this.classFile);

        if (desc.charAt(0) != '(') throw new JaninoRuntimeException("Method descriptor does not start with \"(\"");
        int i   = 1;
        int res = 0;
        for (;;) {
            switch (desc.charAt(i++)) {
            case ')':
                return res - Descriptor.size(desc.substring(i));
            case 'B': case 'C': case 'F': case 'I': case 'S': case 'Z':
                res += 1;
                break;
            case 'D': case 'J':
                res += 2;
                break;
            case '[':
                res += 1;
                while (desc.charAt(i) == '[') ++i;
                if ("BCFISZDJ".indexOf(desc.charAt(i)) != -1) { ++i; break; }
                if (desc.charAt(i) != 'L') throw new JaninoRuntimeException("Invalid char after \"[\"");
                ++i;
                while (desc.charAt(i++) != ';');
                break;
            case 'L':
                res += 1;
                while (desc.charAt(i++) != ';');
                break;
            default:
                throw new JaninoRuntimeException("Invalid method descriptor");
            }
        }
    }

    /**
     * Inserts a sequence of bytes at the current insertion position. Creates
     * {@link LineNumberOffset}s as necessary.
     *
     * @param lineNumber The line number that corresponds to the byte code, or -1
     * @param b
     */
    public void
    write(short lineNumber, byte[] b) {
        if (b.length == 0) return;

        int ico = this.currentInserter.offset;
        this.makeSpace(lineNumber, b.length);
        System.arraycopy(b, 0, this.code, ico, b.length);
    }

    /**
     * Inserts a byte at the current insertion position. Creates
     * {@link LineNumberOffset}s as necessary.
     * <p>
     * This method is an optimization to avoid allocating small byte[] and ease
     * GC load.
     *
     * @param lineNumber The line number that corresponds to the byte code, or -1
     * @param b1
     */
    public void
    write(short lineNumber, byte b1) {
        int ico = this.currentInserter.offset;
        this.makeSpace(lineNumber, 1);
        this.code[ico] = b1;
    }

    /**
     * Inserts bytes at the current insertion position. Creates
     * {@link LineNumberOffset}s as necessary.
     * <p>
     * This method is an optimization to avoid allocating small byte[] and ease
     * GC load.
     *
     * @param lineNumber The line number that corresponds to the byte code, or -1
     * @param b1
     * @param b2
     */
    public void
    write(short lineNumber, byte b1, byte b2) {
        int ico = this.currentInserter.offset;
        this.makeSpace(lineNumber, 2);
        this.code[ico++] = b1;
        this.code[ico]   = b2;
    }

    /**
     * Inserts bytes at the current insertion position. Creates
     * {@link LineNumberOffset}s as necessary.
     * <p>
     * This method is an optimization to avoid allocating small byte[] and ease
     * GC load.
     *
     * @param lineNumber The line number that corresponds to the byte code, or -1
     * @param b1
     * @param b2
     * @param b3
     */
    public void
    write(short lineNumber, byte b1, byte b2, byte b3) {
        int ico = this.currentInserter.offset;
        this.makeSpace(lineNumber, 3);
        this.code[ico++] = b1;
        this.code[ico++] = b2;
        this.code[ico]   = b3;
    }

    /**
     * Inserts bytes at the current insertion position. Creates
     * {@link LineNumberOffset}s as necessary.
     * <p>
     * This method is an optimization to avoid allocating small byte[] and ease
     * GC load.
     *
     * @param lineNumber The line number that corresponds to the byte code, or -1
     * @param b1
     * @param b2
     * @param b3
     * @param b4
     */
    public void
    write(short lineNumber, byte b1, byte b2, byte b3, byte b4) {
        int ico = this.currentInserter.offset;
        this.makeSpace(lineNumber, 4);
        this.code[ico++] = b1;
        this.code[ico++] = b2;
        this.code[ico++] = b3;
        this.code[ico]   = b4;
    }

    /**
     * Add space for {@code size} bytes at current offset. Creates {@link LineNumberOffset}s as necessary.
     *
     * @param lineNumber The line number that corresponds to the byte code, or -1
     * @param size       The size in bytes to inject
     */
    public void
    makeSpace(short lineNumber, int size) {
        if (size == 0) return;

        INSERT_LINE_NUMBER_OFFSET:
        if (lineNumber != -1) {
            Offset o;
            for (o = this.currentInserter.prev; o != this.beginning; o = o.prev) {
                if (o instanceof LineNumberOffset) {
                    if (((LineNumberOffset) o).lineNumber == lineNumber) break INSERT_LINE_NUMBER_OFFSET;
                    break;
                }
            }
            LineNumberOffset lno = new LineNumberOffset(this.currentInserter.offset, lineNumber);
            lno.prev = this.currentInserter.prev;
            lno.next = this.currentInserter;

            this.currentInserter.prev.next = lno;
            this.currentInserter.prev      = lno;
        }

        int ico = this.currentInserter.offset;
        if (this.end.offset + size <= this.code.length) {
            // Optimization to avoid a trivial method call in the common case
            if (ico != this.end.offset) {
                System.arraycopy(this.code, ico, this.code, ico + size, this.end.offset - ico);
            }
        } else {
            byte[] oldCode = this.code;
            //double size to avoid horrible performance, but don't grow over our limit
            int newSize = Math.max(Math.min(oldCode.length * 2, 0xffff), oldCode.length + size);
            if (newSize > 0xffff) {
                throw new JaninoRuntimeException(
                    "Code of method \""
                    + this.functionName
                    + "\" of class \""
                    + this.classFile.getThisClassName()
                    + "\" grows beyond 64 KB"
                );
            }
            this.code = new byte[newSize];
            System.arraycopy(oldCode, 0, this.code, 0, ico);
            System.arraycopy(oldCode, ico, this.code, ico + size, this.end.offset - ico);
        }
        Arrays.fill(this.code, ico, ico + size, (byte) 0);
        for (Offset o = this.currentInserter; o != null; o = o.next) o.offset += size;
    }

    /** @param lineNumber The line number that corresponds to the byte code, or -1 */
    public void
    writeShort(short lineNumber, int v) { this.write(lineNumber, (byte) (v >> 8), (byte) v); }

    /** @param lineNumber The line number that corresponds to the byte code, or -1 */
    public void
    writeBranch(short lineNumber, int opcode, final Offset dst) {
        this.relocatables.add(new Branch(opcode, dst));
        this.write(lineNumber, (byte) opcode, (byte) -1, (byte) -1);
    }

    private
    class Branch extends Relocatable {

        public
        Branch(int opcode, Offset destination) {
            this.opcode      = opcode;
            this.source      = CodeContext.this.newInserter();
            this.destination = destination;
            if (opcode == Opcode.JSR_W || opcode == Opcode.GOTO_W) {
                //no need to expand wide opcodes
                this.expanded = true;
            } else {
                this.expanded = false;
            }
        }

        @Override public boolean
        relocate() {
            if (this.destination.offset == Offset.UNSET) {
                throw new JaninoRuntimeException("Cannot relocate branch to unset destination offset");
            }
            int offset = this.destination.offset - this.source.offset;

            if (!this.expanded && (offset > Short.MAX_VALUE || offset < Short.MIN_VALUE)) {
                //we want to insert the data without skewing our source position,
                //so we will cache it and then restore it later.
                final int pos = this.source.offset;
                CodeContext.this.pushInserter(this.source);
                {
                    // promotion to a wide instruction only requires 2 extra bytes
                    // everything else requires a new GOTO_W instruction after a negated if
                    CodeContext.this.makeSpace(
                        (short) -1,
                        this.opcode == Opcode.GOTO ? 2 : this.opcode == Opcode.JSR ? 2 : 5
                    );
                }
                CodeContext.this.popInserter();
                this.source.offset = pos;
                this.expanded      = true;
                return false;
            }

            final byte[] ba;
            if (!this.expanded) {
                //we fit in a 16-bit jump
                ba = new byte[] { (byte) this.opcode, (byte) (offset >> 8), (byte) offset };
            } else {
                if (this.opcode == Opcode.GOTO || this.opcode == Opcode.JSR) {
                    ba = new byte[] {
                        (byte) (this.opcode + 33), // GOTO => GOTO_W; JSR => JSR_W
                        (byte) (offset >> 24),
                        (byte) (offset >> 16),
                        (byte) (offset >> 8),
                        (byte) offset
                    };
                } else
                {
                    //exclude the if-statement from jump target
                    //if jumping backwards this will increase the jump to go over it
                    //if jumping forwards this will decrease the jump by it
                    offset -= 3;

                    //  [if cond offset]
                    //expands to
                    //  [if !cond skip_goto]
                    //  [GOTO_W offset]
                    ba = new byte[] {
                        CodeContext.invertBranchOpcode((byte) this.opcode),
                        0,
                        8, // Jump from this instruction past the GOTO_W
                        Opcode.GOTO_W,
                        (byte) (offset >> 24),
                        (byte) (offset >> 16),
                        (byte) (offset >> 8),
                        (byte) offset
                    };
                }
            }
            System.arraycopy(ba, 0, CodeContext.this.code, this.source.offset, ba.length);
            return true;
        }

        private boolean        expanded; //marks whether this has been expanded to account for a wide branch
        private final int      opcode;
        private final Inserter source;
        private final Offset   destination;
    }

    /** E.g. {@link Opcode#IFLT} ("less than") inverts to {@link Opcode#IFGE} ("greater than or equal to"). */
    private static byte
    invertBranchOpcode(byte branchOpcode) {
        return ((Byte) CodeContext.BRANCH_OPCODE_INVERSION.get(new Byte(branchOpcode))).byteValue();
    }

    private static final Map<Byte /*branch-opcode*/, Byte /*inverted-branch-opcode*/>
    BRANCH_OPCODE_INVERSION = CodeContext.createBranchOpcodeInversion();
    private static Map<Byte, Byte>
    createBranchOpcodeInversion() {
        Map<Byte, Byte> m = new HashMap();
        m.put(new Byte(Opcode.IF_ACMPEQ), new Byte(Opcode.IF_ACMPNE));
        m.put(new Byte(Opcode.IF_ACMPNE), new Byte(Opcode.IF_ACMPEQ));
        m.put(new Byte(Opcode.IF_ICMPEQ), new Byte(Opcode.IF_ICMPNE));
        m.put(new Byte(Opcode.IF_ICMPNE), new Byte(Opcode.IF_ICMPEQ));
        m.put(new Byte(Opcode.IF_ICMPGE), new Byte(Opcode.IF_ICMPLT));
        m.put(new Byte(Opcode.IF_ICMPLT), new Byte(Opcode.IF_ICMPGE));
        m.put(new Byte(Opcode.IF_ICMPGT), new Byte(Opcode.IF_ICMPLE));
        m.put(new Byte(Opcode.IF_ICMPLE), new Byte(Opcode.IF_ICMPGT));
        m.put(new Byte(Opcode.IFEQ),      new Byte(Opcode.IFNE));
        m.put(new Byte(Opcode.IFNE),      new Byte(Opcode.IFEQ));
        m.put(new Byte(Opcode.IFGE),      new Byte(Opcode.IFLT));
        m.put(new Byte(Opcode.IFLT),      new Byte(Opcode.IFGE));
        m.put(new Byte(Opcode.IFGT),      new Byte(Opcode.IFLE));
        m.put(new Byte(Opcode.IFLE),      new Byte(Opcode.IFGT));
        m.put(new Byte(Opcode.IFNULL),    new Byte(Opcode.IFNONNULL));
        m.put(new Byte(Opcode.IFNONNULL), new Byte(Opcode.IFNULL));
        return Collections.unmodifiableMap(m);
    }

    /** Writes a four-byte offset (as it is used in TABLESWITCH and LOOKUPSWITCH) into this code context. */
    public void
    writeOffset(short lineNumber, Offset src, final Offset dst) {
        this.relocatables.add(new OffsetBranch(this.newOffset(), src, dst));
        this.write(lineNumber, (byte) -1, (byte) -1, (byte) -1, (byte) -1);
    }

    private
    class OffsetBranch extends Relocatable {

        public
        OffsetBranch(Offset where, Offset source, Offset destination) {
            this.where       = where;
            this.source      = source;
            this.destination = destination;
        }

        @Override public boolean
        relocate() {
            if (this.source.offset == Offset.UNSET || this.destination.offset == Offset.UNSET) {
                throw new JaninoRuntimeException("Cannot relocate offset branch to unset destination offset");
            }
            int    offset = this.destination.offset - this.source.offset;
            byte[] ba     = new byte[] {
                (byte) (offset >> 24),
                (byte) (offset >> 16),
                (byte) (offset >> 8),
                (byte) offset
            };
            System.arraycopy(ba, 0, CodeContext.this.code, this.where.offset, 4);
            return true;
        }
        private final Offset where, source, destination;
    }

    /** Creates and inserts an {@link CodeContext.Offset} at the current inserter's current position. */
    public Offset
    newOffset() {
        Offset o = new Offset();
        o.set();
        return o;
    }

    /**
     * Allocate an {@link Inserter}, set it to the current offset, and
     * insert it before the current offset.
     *
     * In clear text, this means that you can continue writing to the
     * "Code" attribute, then {@link #pushInserter(CodeContext.Inserter)} the
     * {@link Inserter}, then write again (which inserts bytes into the
     * "Code" attribute at the previously remembered position), and then
     * {@link #popInserter()}.
     */
    public Inserter
    newInserter() { Inserter i = new Inserter(); i.set(); return i; }

    /** @return The current inserter */
    public Inserter
    currentInserter() { return this.currentInserter; }

    /** Remember the current {@link Inserter}, then replace it with the new one. */
    public void
    pushInserter(Inserter ins) {
        if (ins.nextInserter != null) throw new JaninoRuntimeException("An Inserter can only be pushed once at a time");
        ins.nextInserter     = this.currentInserter;
        this.currentInserter = ins;
    }

    /**
     * Replace the current {@link Inserter} with the remembered one (see
     * {@link #pushInserter(CodeContext.Inserter)}).
     */
    public void
    popInserter() {
        Inserter ni = this.currentInserter.nextInserter;
        if (ni == null) throw new JaninoRuntimeException("Code inserter stack underflow");
        this.currentInserter.nextInserter = null; // Mark it as "unpushed".
        this.currentInserter              = ni;
    }

    /**
     * A class that represents an offset within a "Code" attribute.
     *
     * The concept of an "offset" is that if one writes into the middle of
     * a "Code" attribute, all offsets behind the insertion point are
     * automatically shifted.
     */
    public
    class Offset {

        /** The offset in the code attribute that this object represents. */
        int offset = Offset.UNSET;

        /** Links to preceding and succeding offsets. */
        Offset prev, next;

        /**
         * Special value for {@link #offset} which indicates that this {@link Offset} has not yet been {@link #set()}
         */
        static final int UNSET = -1;

        /**
         * Sets this "Offset" to the offset of the current inserter; inserts this "Offset" before the current inserter.
         */
        public void
        set() {
            if (this.offset != Offset.UNSET) throw new JaninoRuntimeException("Cannot \"set()\" Offset more than once");

            this.offset = CodeContext.this.currentInserter.offset;

            this.prev      = CodeContext.this.currentInserter.prev;
            this.next      = CodeContext.this.currentInserter;
            this.prev.next = this;
            this.next.prev = this;
        }

        /** @return The {@link CodeContext} that this {@link Offset} belongs to */
        public final CodeContext getCodeContext() { return CodeContext.this; }

        @Override public String
        toString() { return CodeContext.this.classFile.getThisClassName() + ": " + this.offset; }
    }

    /**
     * Add another entry to the "exception_table" of this code attribute (see JVMS 4.7.3).
     *
     * @param catchTypeFd null == "finally" clause
     */
    public void
    addExceptionTableEntry(Offset startPc, Offset endPc, Offset handlerPc, String catchTypeFd) {
        this.exceptionTableEntries.add(new ExceptionTableEntry(
            startPc,
            endPc,
            handlerPc,
            catchTypeFd == null ? (short) 0 : this.classFile.addConstantClassInfo(catchTypeFd)
        ));
    }

    /** Representation of an entry in the "exception_table" of a "Code" attribute (see JVMS 4.7.3). */
    private static
    class ExceptionTableEntry {
        ExceptionTableEntry(Offset startPc, Offset endPc, Offset handlerPc, short  catchType) {
            this.startPC   = startPc;
            this.endPC     = endPc;
            this.handlerPC = handlerPc;
            this.catchType = catchType;
        }
        final Offset startPC, endPC, handlerPC;
        final short  catchType; // 0 == "finally" clause
    }

    /** A class that implements an insertion point into a "Code" attribute. */
    public
    class Inserter extends Offset {
        private Inserter nextInserter; // null == not in "currentInserter" stack
    }

    /** An {@link Offset} who#s sole purpose is to later create a 'LneNumberTable' attribute. */
    public
    class LineNumberOffset extends Offset {
        private final int lineNumber;

        public
        LineNumberOffset(int offset, int lineNumber) {
            this.lineNumber = lineNumber;
            this.offset     = offset;
        }
    }

    private abstract
    class Relocatable {

        /**
         * Relocate this object.
         * @return true if the relocation succeeded in place
         *         false if the relocation grew the number of bytes required
         */
        public abstract boolean relocate();
    }

    /**
     * A throw-in interface that marks {@link CodeContext.Offset}s
     * as "fix-ups": During the execution of
     * {@link CodeContext#fixUp}, all "fix-ups" are invoked and
     * can do last touches to the code attribute.
     * <p>
     * This is currently used for inserting the "padding bytes" into the
     * TABLESWITCH and LOOKUPSWITCH instructions.
     */
    public
    interface FixUp {

        /** @see FixUp */
        void fixUp();
    }

    /** @return All the local variables that are allocated in any block in this {@link CodeContext} */
    public List<Java.LocalVariableSlot>
    getAllLocalVars() { return this.allLocalVars; }

    /**
     * Removes all code between {@code from} and {@code to}. Also removes any {@link CodeContext.Relocatable}s existing
     * in that range.
     */
    public void
    removeCode(Offset from, Offset to) {

        if (from == to) return;

        int size = to.offset - from.offset;
        assert size >= 0;

        if (size == 0) return; // Short circuit.

        // Shift down the bytecode past 'to'.
        System.arraycopy(this.code, to.offset, this.code, from.offset, this.end.offset - to.offset);

        // Invalidate all offsets between 'from' and 'to'.
        // Remove all relocatables that originate between 'from' and 'to'.
        Set<Offset> invalidOffsets = new HashSet<Offset>();
        {
            Offset o = from.next;

            for (; o != to;) {
                if (o == null) {
                    System.currentTimeMillis();
                }
                invalidOffsets.add(o);

                // Invalidate the offset for fast failure.
                o.offset    = -77;
                o.prev      = null;
                o           = o.next;
                o.prev.next = null;
            }

            for (;; o = o.next) {
                o.offset -= size;
                if (o == this.end) break;
            }
        }

        // Invalidate all relocatables which originate or target a removed offset.
        for (Iterator<Relocatable> it = this.relocatables.iterator(); it.hasNext();) {
            Relocatable r = (Relocatable) it.next();

            if (r instanceof Branch) {
                Branch b = (Branch) r;

                if (invalidOffsets.contains(b.source)) {
                    it.remove();
                } else {
                    assert !invalidOffsets.contains(b.destination);
                }
            }

            if (r instanceof OffsetBranch) {
                OffsetBranch ob = (OffsetBranch) r;

                if (invalidOffsets.contains(ob.source)) {
                    it.remove();
                } else {
                    assert !invalidOffsets.contains(ob.destination);
                }
            }
        }

        for (Iterator<ExceptionTableEntry> it = this.exceptionTableEntries.iterator(); it.hasNext();) {
            ExceptionTableEntry ete = (ExceptionTableEntry) it.next();

            // Start, end and handler must either ALL lie IN the range to remove or ALL lie outside.

            if (invalidOffsets.contains(ete.startPC)) {
                assert invalidOffsets.contains(ete.endPC);
                assert invalidOffsets.contains(ete.handlerPC);
                it.remove();
            } else {
                assert !invalidOffsets.contains(ete.endPC);
                assert !invalidOffsets.contains(ete.handlerPC);
            }
        }

        from.next = to;
        to.prev   = from;
    }
}
