package the.bytecode.club.bytecodeviewer.decompilers;

import the.bytecode.club.bytecodeviewer.decompilers.impl.*;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
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

/**
 * All of the decompilers/disassemblers BCV uses
 *
 * @author Konloch
 */
public enum Decompiler
{
    //TODO WARNING: do not change the decompiler order, when adding a new decompiler just add it to the end
    // enum ordinal is used for settings serialization instead of the enum name
    NONE("None", "", null),
    PROCYON_DECOMPILER("Procyon Decompiler", "proycon", new ProcyonDecompiler()),
    CFR_DECOMPILER("CFR Decompiler", "cfr", new CFRDecompiler()),
    FERNFLOWER_DECOMPILER("FernFlower Decompiler", "fernflower", new FernFlowerDecompiler()),
    BYTECODE_DISASSEMBLER("Bytecode Disassembler", "bcvbd", new BytecodeDisassembler()),
    HEXCODE_VIEWER("Hexcode Viewer", "bcvhex", null),
    SMALI_DISASSEMBLER("Smali Disassembler", "smali", new SmaliDisassembler()),
    KRAKATAU_DECOMPILER("Krakatau Decompiler", "krakatau", new KrakatauDecompiler()),
    KRAKATAU_DISASSEMBLER("Krakatau Disassembler", "krakataud", new KrakatauDisassembler()),
    JD_DECOMPILER("JD-GUI Decompiler", "jdgui", new JDGUIDecompiler()),
    JADX_DECOMPILER("JADX Decompiler", "jadx", new JADXDecompiler()),
    ASM_TEXTIFY_DISASSEMBLER("ASM Disassembler", "asm", new ASMTextifierDisassembler()),
    ASMIFIER_DECOMPILER("ASMifier Generator", "asmifier", new ASMifierGenerator()),
    JAVAP_DISASSEMBLER("Javap Disassembler", "javap", new JavapDisassembler()),
    ;
    
    private final String decompilerName;
    private final String decompilerNameProgrammic;
    private final InternalDecompiler decompiler;
    
    Decompiler(String decompilerName, String decompilerNameProgrammic, InternalDecompiler decompiler)
    {
        this.decompilerName = decompilerName;
        this.decompilerNameProgrammic = decompilerNameProgrammic;
        this.decompiler = decompiler;
    }
    
    public String getDecompilerName()
    {
        return decompilerName;
    }
    
    public String getDecompilerNameProgrammic()
    {
        return decompilerNameProgrammic;
    }
    
    public InternalDecompiler getDecompiler()
    {
        return decompiler;
    }
}
