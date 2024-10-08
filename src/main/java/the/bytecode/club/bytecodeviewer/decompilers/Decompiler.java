/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

package the.bytecode.club.bytecodeviewer.decompilers;

import the.bytecode.club.bytecodeviewer.decompilers.impl.*;

/**
 * All the decompilers/disassemblers BCV uses
 *
 * @author Konloch
 */
public enum Decompiler
{
    //TODO WARNING: do not change the decompiler order, when adding a new decompiler just add it to the end.
    // Enum ordinal is used for settings serialization instead of the enum name.

    NONE(null),
    PROCYON_DECOMPILER(new ProcyonDecompiler()),                //java decompiler
    CFR_DECOMPILER(new CFRDecompiler()),                        //java decompiler
    FERNFLOWER_DECOMPILER(new FernFlowerDecompiler()),          //java decompiler

    BYTECODE_DISASSEMBLER(new BytecodeDisassembler()),          //bytecode disassembler
    HEXCODE_VIEWER(null),                                       //hexcode viewer

    SMALI_DISASSEMBLER(new SmaliDisassembler()),                //bytecode disassembler
    KRAKATAU_DECOMPILER(new KrakatauDecompiler()),              //java decompiler
    KRAKATAU_DISASSEMBLER(new KrakatauDisassembler()),          //bytecode disassembler
    JD_DECOMPILER(new JDGUIDecompiler()),                       //java decompiler
    JADX_DECOMPILER(new JADXDecompiler()),                      //java decompiler

    ASM_DISASSEMBLER(new ASMDisassembler()),                    //bytecode disassembler
    ASMIFIER_CODE_GEN(new ASMifierGenerator()),                 //bytecode disassembler / code gen
    JAVAP_DISASSEMBLER(new JavapDisassembler());                //bytecode disassembler

    private final AbstractDecompiler decompiler;

    Decompiler(AbstractDecompiler decompiler)
    {
        this.decompiler = decompiler;
    }

    public String getDecompilerName()
    {
        if(decompiler == null)
            return "None";

        return getDecompiler().getDecompilerName();
    }

    /**
     * Used for the compressed exports (Zip / Jar)
     */
    public String getDecompilerNameProgrammatic()
    {
        if(decompiler == null)
            return "";

        return getDecompiler().getDecompilerNameProgrammatic();
    }

    public AbstractDecompiler getDecompiler()
    {
        return decompiler;
    }
}
