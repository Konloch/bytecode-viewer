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

package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.SettingsSerializer;
import the.bytecode.club.bytecodeviewer.bootloader.BootState;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.components.DecompilerViewComponent;
import the.bytecode.club.bytecodeviewer.translation.TranslatedComponents;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJMenu;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJRadioButtonMenuItem;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import static the.bytecode.club.bytecodeviewer.gui.components.DecompilerViewComponent.DecompilerComponentType.*;

/**
 * @author Konloch
 * @since 6/21/2021
 */
public class DecompilerSelectionPane
{
    private final int paneID;
    private final JMenu menu;
    private final ButtonGroup group = new ButtonGroup();
    private final JRadioButtonMenuItem none = new TranslatedJRadioButtonMenuItem("None", TranslatedComponents.NONE);
    private final JRadioButtonMenuItem hexcodeViewer = new TranslatedJRadioButtonMenuItem("Hexcode", TranslatedComponents.HEXCODE);
    //decompilers
    private final DecompilerViewComponent fernFlowerDecompiler = new DecompilerViewComponent("FernFlower", JAVA, Decompiler.FERNFLOWER_DECOMPILER);
    private final DecompilerViewComponent procyonDecompiler = new DecompilerViewComponent("Procyon", JAVA, Decompiler.PROCYON_DECOMPILER);
    private final DecompilerViewComponent CFRDecompiler = new DecompilerViewComponent("CFR", JAVA, Decompiler.CFR_DECOMPILER);
    private final DecompilerViewComponent JADXDecompiler = new DecompilerViewComponent("JADX", JAVA, Decompiler.JADX_DECOMPILER);
    private final DecompilerViewComponent JDCoreDecompiler = new DecompilerViewComponent("JD-GUI", JAVA, Decompiler.JD_DECOMPILER);
    //disassemblers
    private final DecompilerViewComponent bytecodeViewer = new DecompilerViewComponent("Bytecode", BYTECODE_NON_EDITABLE, Decompiler.BYTECODE_DISASSEMBLER);
    private final DecompilerViewComponent javapDisassembler = new DecompilerViewComponent("Javap", BYTECODE_NON_EDITABLE, Decompiler.JAVAP_DISASSEMBLER);
    private final DecompilerViewComponent krakatauDecompiler = new DecompilerViewComponent("Krakatau", JAVA_AND_BYTECODE, Decompiler.KRAKATAU_DECOMPILER, Decompiler.KRAKATAU_DISASSEMBLER);
    private final DecompilerViewComponent smaliDisassembler = new DecompilerViewComponent("Smali", BYTECODE, Decompiler.SMALI_DISASSEMBLER);
    //code-gen / etc
    private final DecompilerViewComponent asmifierCodeGen = new DecompilerViewComponent("ASMifier", JAVA_AND_BYTECODE_NON_EDITABLE, Decompiler.ASMIFIER_CODE_GEN, Decompiler.ASM_DISASSEMBLER);

    //TODO when adding new decompilers insert the DecompilerViewComponent object into here
    // also in the group, then finally the build menu
    public List<DecompilerViewComponent> components = new ArrayList<>(Arrays.asList(
        procyonDecompiler, CFRDecompiler, JADXDecompiler, JDCoreDecompiler, fernFlowerDecompiler,
        krakatauDecompiler, smaliDisassembler, bytecodeViewer, asmifierCodeGen, javapDisassembler));

    public DecompilerSelectionPane(int paneID)
    {
        this.paneID = paneID;
        if (paneID == 1)
            this.menu = new TranslatedJMenu("Pane " + 1, TranslatedComponents.PANE_1);
        else if (paneID == 2)
            this.menu = new TranslatedJMenu("Pane " + 2, TranslatedComponents.PANE_2);
        else
            this.menu = new TranslatedJMenu("Pane " + paneID, TranslatedComponents.PANE_3);

        buildMenu();
    }

    /**
     * Sets the default decompilers for each pane
     */
    public void setDefault()
    {
        switch (paneID)
        {
            case 1:
                group.setSelected(fernFlowerDecompiler.getJava().getModel(), true);
                break;
            case 2:
                group.setSelected(bytecodeViewer.getBytecode().getModel(), true);
                break;
            case 3:
                group.setSelected(none.getModel(), true);
                break;
        }
    }

    /**
     * Builds the Decompiler View menu
     */
    public void buildMenu()
    {
        //build the radiobutton group
        group.add(none);
        group.add(hexcodeViewer);
        components.forEach(decompilerViewComponent -> decompilerViewComponent.addToGroup(group));

        //build the action commands
        none.setActionCommand(Decompiler.NONE.name());
        hexcodeViewer.setActionCommand(Decompiler.HEXCODE_VIEWER.name());

        for (DecompilerViewComponent component : components)
        {
            for (Decompiler decompiler : component.getDecompilers())
            {
                String cmd = decompiler.name();

                //TODO this is pretty janky and will break if a decompiler doesn't end with _DECOMPILER suffix
                if (cmd.endsWith("_DECOMPILER") || cmd.endsWith("_CODE_GEN"))
                    component.getJava().setActionCommand(cmd);
                else// if(cmd.endsWith("_DISASSEMBLER"))
                    component.getBytecode().setActionCommand(cmd);
            }
        }

        //auto-save on decompiler change
        Enumeration<AbstractButton> it = group.getElements();

        while (it.hasMoreElements())
        {
            AbstractButton button = it.nextElement();
            button.addActionListener((event) ->
            {
                if (Configuration.bootState != BootState.GUI_SHOWING)
                    return;

                SettingsSerializer.saveSettingsAsync();
            });
        }

        //build the menu
        menu.add(none);
        menu.add(new JSeparator());
        menu.add(procyonDecompiler.getMenu());
        menu.add(CFRDecompiler.getMenu());

        if (!Configuration.jadxGroupedWithSmali)
            menu.add(JADXDecompiler.getMenu());

        menu.add(JDCoreDecompiler.getMenu());
        menu.add(fernFlowerDecompiler.getMenu());
        menu.add(krakatauDecompiler.getMenu());
        menu.add(new JSeparator());

        if (Configuration.jadxGroupedWithSmali)
            menu.add(JADXDecompiler.getMenu());

        menu.add(smaliDisassembler.getMenu());
        menu.add(new JSeparator());
        menu.add(bytecodeViewer.getMenu());
        menu.add(javapDisassembler.getMenu());
        menu.add(asmifierCodeGen.getMenu());
        menu.add(new JSeparator());
        menu.add(hexcodeViewer);
    }

    public Decompiler getSelectedDecompiler()
    {
        return Decompiler.valueOf(group.getSelection().getActionCommand());
    }

    public void setSelectedDecompiler(Decompiler decompiler)
    {
        Enumeration<AbstractButton> it = group.getElements();

        while (it.hasMoreElements())
        {
            AbstractButton button = it.nextElement();

            if (button.getActionCommand().equals(decompiler.name()))
            {
                group.setSelected(button.getModel(), true);
                break;
            }
        }
    }

    public boolean isPaneEditable()
    {
        String cmd = group.getSelection().getActionCommand();

        for (DecompilerViewComponent component : components)
            for (Decompiler decompiler : component.getDecompilers())
                if (decompiler.name().equalsIgnoreCase(cmd))
                    return component.getEditable().isSelected();

        return false;
    }

    public void setPaneEditable(boolean value)
    {
        String cmd = group.getSelection().getActionCommand();

        for (DecompilerViewComponent component : components)
            for (Decompiler decompiler : component.getDecompilers())
                if (decompiler.name().equalsIgnoreCase(cmd))
                {
                    component.getEditable().setSelected(value);
                    return;
                }
    }

    public JMenu getMenu()
    {
        return menu;
    }
}
