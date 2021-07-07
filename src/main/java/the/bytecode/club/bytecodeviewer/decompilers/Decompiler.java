package the.bytecode.club.bytecodeviewer.decompilers;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.decompilers.impl.*;
import the.bytecode.club.bytecodeviewer.gui.components.DecompilerViewComponent;

import javax.swing.*;

import static the.bytecode.club.bytecodeviewer.gui.components.DecompilerViewComponent.DecompilerComponentTypes.*;

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
    NONE("None", null, (JRadioButtonMenuItem) null),
    PROCYON_DECOMPILER("Procyon Decompiler", new ProcyonDecompiler(), new DecompilerViewComponent("Procyon", JAVA)),
    CFR_DECOMPILER("CFR Decompiler", new CFRDecompiler(), new DecompilerViewComponent("CFR", JAVA)),
    FERNFLOWER_DECOMPILER("FernFlower Decompiler", new FernFlowerDecompiler(), new DecompilerViewComponent("FernFlower", JAVA)),
    BYTECODE_DISASSEMBLER("Bytecode Disassembler", new BytecodeDisassembler(), new JRadioButtonMenuItem("Bytecode")),
    HEXCODE_VIEWER("Hexcode Viewer", null, new JRadioButtonMenuItem("Hexcode")),
    SMALI_DISASSEMBLER("Smali Disassembler", new SmaliDisassembler(), new DecompilerViewComponent("Smali", BYTECODE)),
    KRAKATAU_DECOMPILER("Krakatau Decompiler", new KrakatauDecompiler(), DecompilerViewComponent.KRAKATAU),
    KRAKATAU_DISASSEMBLER("Krakatau Disassembler", new KrakatauDisassembler(), DecompilerViewComponent.KRAKATAU),
    JD_DECOMPILER("JD-GUI Decompiler", new JDGUIDecompiler(), new DecompilerViewComponent("JD-GUI", JAVA)),
    JADX_DECOMPILER("JADX Decompiler", new JADXDecompiler(), new DecompilerViewComponent("JADX", JAVA)),
    ASM_TEXTIFY_DISASSEMBLER("ASM Disassembler", new ASMTextifierDecompiler(), new DecompilerViewComponent("ASM Textify", BYTECODE)),
    ;
    
    private final String decompilerName;
    private final InternalDecompiler decompiler;
    private final DecompilerViewComponent decompilerSelectComponent;
    private final JRadioButtonMenuItem basicSelectComponent;
    
    Decompiler(String decompilerName, InternalDecompiler decompiler, DecompilerViewComponent decompilerSelectComponent) {
        this.decompilerName = decompilerName;
        this.decompiler = decompiler;
        this.decompilerSelectComponent = decompilerSelectComponent;
        this.basicSelectComponent = null;
    }
    
    Decompiler(String decompilerName, InternalDecompiler decompiler, JRadioButtonMenuItem basicSelectComponent)
    {
        this.decompilerName = decompilerName;
        this.decompiler = decompiler;
        this.decompilerSelectComponent = null;
        this.basicSelectComponent = basicSelectComponent;
    }
    
    public void addDecompilerToGroup(ButtonGroup group)
    {
        if(decompilerSelectComponent != null)
            decompilerSelectComponent.addToGroup(group);
        else if(basicSelectComponent != null)
            group.add(basicSelectComponent);
    }
    
    public String getDecompilerName()
    {
        return decompilerName;
    }
    
    public InternalDecompiler getDecompiler()
    {
        return decompiler;
    }
    
    public DecompilerViewComponent getDecompilerSelectComponent()
    {
        return decompilerSelectComponent;
    }
    
    public JMenuItem getMenu()
    {
        if(decompilerSelectComponent != null)
            return decompilerSelectComponent.getMenu();
        
        return basicSelectComponent;
    }
}