package the.bytecode.club.bytecodeviewer.decompilers;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.decompilers.bytecode.ClassNodeDecompiler;
import the.bytecode.club.bytecodeviewer.gui.components.DecompilerViewComponent;

import javax.swing.*;
import java.util.HashMap;

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
    NONE(0, "None", null, (JRadioButtonMenuItem) null),
    PROCYON(1, "Procyon Decompiler", new ProcyonDecompiler(), new DecompilerViewComponent("Procyon")),
    CFR(2, "CFR Decompiler", new CFRDecompiler(), new DecompilerViewComponent("Procyon")),
    FERNFLOWER(3, "FernFlower Decompiler", new FernFlowerDecompiler(), new DecompilerViewComponent("Procyon")),
    BYTECODE(4, "Bytecode Disassembler", new ClassNodeDecompiler(), new JRadioButtonMenuItem("Bytecode")),
    HEXCODE(5, "Hexcode Viewer", null, new JRadioButtonMenuItem("Hexcode")),
    SMALI(6, "Smali Decompiler", new SmaliDisassembler(), new DecompilerViewComponent("Smali")),
    KRAKATAU(7, "Krakatau Decompiler", new KrakatauDecompiler(), BytecodeViewer.krakatau),
    KRAKATAU_BYTECODE(8, "Krakatau Disassembler", new KrakatauDisassembler(), BytecodeViewer.krakatau),
    JDGUI(9, "JD-GUI Decompiler", new JDGUIDecompiler(), new DecompilerViewComponent("Bytecode")),
    JADX(10, "JADX Decompiler", new JADXDecompiler(), new DecompilerViewComponent("JADX")),
    ASMTextify(11, "ASM Disassembler", new ASMTextifierDecompiler(), new DecompilerViewComponent("ASM Textify")),
    ;
    
    private final int decompilerIndex;
    private final String decompilerName;
    private final InternalDecompiler decompiler;
    private final DecompilerViewComponent decompilerSelectComponent;
    private final JRadioButtonMenuItem basicSelectComponent;
    
    public static final HashMap<Integer, Decompiler> decompilersByIndex = new HashMap<>();
    
    static
    {
        for(Decompiler d : values())
            decompilersByIndex.put(d.decompilerIndex, d);
    }
    
    Decompiler(int decompilerIndex, String decompilerName, InternalDecompiler decompiler, DecompilerViewComponent decompilerSelectComponent) {
        this.decompilerIndex = decompilerIndex;
        this.decompilerName = decompilerName;
        this.decompiler = decompiler;
        this.decompilerSelectComponent = decompilerSelectComponent;
        this.basicSelectComponent = null;
    }
    
    Decompiler(int decompilerIndex, String decompilerName, InternalDecompiler decompiler, JRadioButtonMenuItem basicSelectComponent)
    {
        this.decompilerIndex = decompilerIndex;
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
    
    public int getDecompilerIndex()
    {
        return decompilerIndex;
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