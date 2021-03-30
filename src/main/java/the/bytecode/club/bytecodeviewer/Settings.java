package the.bytecode.club.bytecodeviewer;

import javax.swing.JFrame;

import me.konloch.kontainer.io.DiskReader;
import me.konloch.kontainer.io.DiskWriter;

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
 * Used to handle loading/saving the GUI (options).
 *
 * @author Konloch
 */

public class Settings {

    public static void saveSettings() {
        try {
            DiskWriter.replaceFile(BytecodeViewer.settingsName, "BCV: " + BytecodeViewer.VERSION, false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.rbr.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.rsy.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.din.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.dc4.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.das.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.hes.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.hdc.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.dgs.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.ner.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.den.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.rgn.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.bto.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.nns.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.uto.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.udv.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.rer.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.fdi.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.asc.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.decodeenumswitch.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.sugarenums.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.decodestringswitch.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.arrayiter.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.collectioniter.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.innerclasses.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.removeboilerplate.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.removeinnerclasssynthetics.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.decodelambdas.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.hidebridgemethods.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.liftconstructorinit.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.removedeadmethods.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.removebadgenerics.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.sugarasserts.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.sugarboxing.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.showversion.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.decodefinally.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.tidymonitors.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.lenient.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.dumpclasspath.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.comments.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.forcetopsort.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.forcetopsortaggress.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.stringbuffer.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.stringbuilder.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.silent.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.recover.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.eclipse.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.override.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.showinferrable.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.aexagg.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.forcecondpropagate.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.hideutf.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.hidelongstrings.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.commentmonitor.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.allowcorrecting.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.labelledblocks.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.j14classobj.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.hidelangimports.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.recoverytypeclash.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.recoverytypehints.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.forceturningifs.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.forloopaggcapture.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.forceexceptionprune.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.chckbxmntmShowDebugLine.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.chckbxmntmSimplifyMemberReferences.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.mnMergeVariables.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.chckbxmntmNewCheckItem_1.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.chckbxmntmNewCheckItem_2.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.chckbxmntmNewCheckItem_3.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.chckbxmntmNewCheckItem_4.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.chckbxmntmNewCheckItem_5.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.chckbxmntmNewCheckItem_6.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.chckbxmntmNewCheckItem_7.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.chckbxmntmNewCheckItem_8.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.chckbxmntmNewCheckItem_9.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.chckbxmntmNewCheckItem_10.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.chckbxmntmNewCheckItem_11.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.chckbxmntmAppendBrackets.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.debugHelpers.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, "deprecated", false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.chckbxmntmNewCheckItem_12.isSelected()), false);
            if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1None.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "0", false);
            else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1Proc.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "1", false);
            else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1CFR.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "2", false);
            else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1Fern.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "3", false);
            else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1Bytecode.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "4", false);
            else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1Hexcode.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "5", false);
            else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1Smali.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "6", false);
            else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1Krakatau.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "7", false);
            else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1KrakatauBytecode.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "8", false);
            else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.panel1JDGUI.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "9", false);
            else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.jadxJ1.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "10", false);
            else if (BytecodeViewer.viewer.panelGroup1.isSelected(BytecodeViewer.viewer.asmText1.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "11", false);

            if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2None.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "0", false);
            else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2Proc.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "1", false);
            else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2CFR.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "2", false);
            else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2Fern.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "3", false);
            else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2Bytecode.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "4", false);
            else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2Hexcode.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "5", false);
            else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2Smali.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "6", false);
            else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2Krakatau.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "7", false);
            else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2KrakatauBytecode.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "8", false);
            else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.panel2JDGUI.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "9", false);
            else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.jadxJ2.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "10", false);
            else if (BytecodeViewer.viewer.panelGroup2.isSelected(BytecodeViewer.viewer.asmText2.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "11", false);

            if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3None.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "0", false);
            else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3Proc.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "1", false);
            else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3CFR.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "2", false);
            else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3Fern.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "3", false);
            else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3Bytecode.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "4", false);
            else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3Hexcode.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "5", false);
            else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3Smali.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "6", false);
            else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3Krakatau.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "7", false);
            else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3KrakatauBytecode.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "8", false);
            else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.panel3JDGUI.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "9", false);
            else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.jadxJ3.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "10", false);
            else if (BytecodeViewer.viewer.panelGroup3.isSelected(BytecodeViewer.viewer.asmText3.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "11", false);

            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.refreshOnChange.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.isMaximized), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, "deprecated", false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, "deprecated", false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, BytecodeViewer.lastDirectory, false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, BytecodeViewer.python, false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, BytecodeViewer.rt, false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel1Proc_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel1CFR_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel1Fern_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel1Krakatau_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel1Smali_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel2Proc_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel2CFR_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel2Fern_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel2Krakatau_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel2Smali_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel3Proc_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel3CFR_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel3Fern_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel3Krakatau_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel3Smali_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.decodeAPKResources.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, BytecodeViewer.library, false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.pingback), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel1JDGUI_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel2JDGUI_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.panel3JDGUI_E.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.fontSpinner.getValue()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.deleteForeignLibraries), false);
            if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionDex.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "0", false);
            else if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionEnjarify.getModel()))
                DiskWriter.writeNewLine(BytecodeViewer.settingsName, "1", false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, BytecodeViewer.python3, false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, BytecodeViewer.javac, false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, BytecodeViewer.java, false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.compileOnSave.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.autoCompileOnRefresh.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.warnForEditing), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.showFileInTabTitle.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.forcePureAsciiAsText.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.synchronizedViewing.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.showClassMethods.isSelected()), false);
            DiskWriter.writeNewLine(BytecodeViewer.settingsName, String.valueOf(BytecodeViewer.viewer.ren.isSelected()), false);
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }

    public static void loadSettings() { //utilizes the Disk Reader's caching system.
        try {
            BytecodeViewer.viewer.rbr.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 1, true)));
            BytecodeViewer.viewer.rsy.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 2, false)));
            BytecodeViewer.viewer.din.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 3, false)));
            BytecodeViewer.viewer.dc4.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 4, false)));
            BytecodeViewer.viewer.das.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 5, false)));
            BytecodeViewer.viewer.hes.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 6, false)));
            BytecodeViewer.viewer.hdc.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 7, false)));
            BytecodeViewer.viewer.dgs.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 8, false)));
            BytecodeViewer.viewer.ner.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 9, false)));
            BytecodeViewer.viewer.den.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 10, false)));
            BytecodeViewer.viewer.rgn.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 11, false)));
            BytecodeViewer.viewer.bto.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 12, false)));
            BytecodeViewer.viewer.nns.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 13, false)));
            BytecodeViewer.viewer.uto.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 14, false)));
            BytecodeViewer.viewer.udv.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 15, false)));
            BytecodeViewer.viewer.rer.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 16, false)));
            BytecodeViewer.viewer.fdi.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 17, false)));
            BytecodeViewer.viewer.asc.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 18, false)));
            BytecodeViewer.viewer.decodeenumswitch.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 19, false)));
            BytecodeViewer.viewer.sugarenums.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 20, false)));
            BytecodeViewer.viewer.decodestringswitch.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 21, false)));
            BytecodeViewer.viewer.arrayiter.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 22, false)));
            BytecodeViewer.viewer.collectioniter.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 23, false)));
            BytecodeViewer.viewer.innerclasses.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 24, false)));
            BytecodeViewer.viewer.removeboilerplate.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 25, false)));
            BytecodeViewer.viewer.removeinnerclasssynthetics.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 26, false)));
            BytecodeViewer.viewer.decodelambdas.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 27, false)));
            BytecodeViewer.viewer.hidebridgemethods.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 28, false)));
            BytecodeViewer.viewer.liftconstructorinit.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 29, false)));
            BytecodeViewer.viewer.removedeadmethods.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 30, false)));
            BytecodeViewer.viewer.removebadgenerics.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 31, false)));
            BytecodeViewer.viewer.sugarasserts.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 32, false)));
            BytecodeViewer.viewer.sugarboxing.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 33, false)));
            BytecodeViewer.viewer.showversion.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 34, false)));
            BytecodeViewer.viewer.decodefinally.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 35, false)));
            BytecodeViewer.viewer.tidymonitors.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 36, false)));
            BytecodeViewer.viewer.lenient.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 37, false)));
            BytecodeViewer.viewer.dumpclasspath.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 38, false)));
            BytecodeViewer.viewer.comments.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 39, false)));
            BytecodeViewer.viewer.forcetopsort.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 40, false)));
            BytecodeViewer.viewer.forcetopsortaggress.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 41, false)));
            BytecodeViewer.viewer.stringbuffer.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 42, false)));
            BytecodeViewer.viewer.stringbuilder.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 43, false)));
            BytecodeViewer.viewer.silent.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 44, false)));
            BytecodeViewer.viewer.recover.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 45, false)));
            BytecodeViewer.viewer.eclipse.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 46, false)));
            BytecodeViewer.viewer.override.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 47, false)));
            BytecodeViewer.viewer.showinferrable.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 48, false)));
            BytecodeViewer.viewer.aexagg.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 49, false)));
            BytecodeViewer.viewer.forcecondpropagate.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 50, false)));
            BytecodeViewer.viewer.hideutf.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 51, false)));
            BytecodeViewer.viewer.hidelongstrings.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 52, false)));
            BytecodeViewer.viewer.commentmonitor.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 53, false)));
            BytecodeViewer.viewer.allowcorrecting.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 54, false)));
            BytecodeViewer.viewer.labelledblocks.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 55, false)));
            BytecodeViewer.viewer.j14classobj.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 56, false)));
            BytecodeViewer.viewer.hidelangimports.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 57, false)));
            BytecodeViewer.viewer.recoverytypeclash.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 58, false)));
            BytecodeViewer.viewer.recoverytypehints.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 59, false)));
            BytecodeViewer.viewer.forceturningifs.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 60, false)));
            BytecodeViewer.viewer.forloopaggcapture.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 61, false)));
            BytecodeViewer.viewer.forceexceptionprune.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 62, false)));
            BytecodeViewer.viewer.chckbxmntmShowDebugLine.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 63, false)));
            BytecodeViewer.viewer.chckbxmntmSimplifyMemberReferences.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 64, false)));
            BytecodeViewer.viewer.mnMergeVariables.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 65, false)));
            BytecodeViewer.viewer.chckbxmntmNewCheckItem_1.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 66, false)));
            BytecodeViewer.viewer.chckbxmntmNewCheckItem_2.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 67, false)));
            BytecodeViewer.viewer.chckbxmntmNewCheckItem_3.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 68, false)));
            BytecodeViewer.viewer.chckbxmntmNewCheckItem_4.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 69, false)));
            BytecodeViewer.viewer.chckbxmntmNewCheckItem_5.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 70, false)));
            BytecodeViewer.viewer.chckbxmntmNewCheckItem_6.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 71, false)));
            BytecodeViewer.viewer.chckbxmntmNewCheckItem_7.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 72, false)));
            BytecodeViewer.viewer.chckbxmntmNewCheckItem_8.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 73, false)));
            BytecodeViewer.viewer.chckbxmntmNewCheckItem_9.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 74, false)));
            BytecodeViewer.viewer.chckbxmntmNewCheckItem_10.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 75, false)));
            BytecodeViewer.viewer.chckbxmntmNewCheckItem_11.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 76, false)));
            BytecodeViewer.viewer.chckbxmntmAppendBrackets.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 77, false)));
            BytecodeViewer.viewer.debugHelpers.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 78, false)));
            //79 is deprecated
            BytecodeViewer.viewer.chckbxmntmNewCheckItem_12.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 80, false)));
            int decompiler = Integer.parseInt(DiskReader.loadString(BytecodeViewer.settingsName, 81, false));
            if (decompiler == 0)
                BytecodeViewer.viewer.panelGroup1.setSelected(BytecodeViewer.viewer.panel1None.getModel(), true);
            else if (decompiler == 1)
                BytecodeViewer.viewer.panelGroup1.setSelected(BytecodeViewer.viewer.panel1Proc.getModel(), true);
            else if (decompiler == 2)
                BytecodeViewer.viewer.panelGroup1.setSelected(BytecodeViewer.viewer.panel1CFR.getModel(), true);
            else if (decompiler == 3)
                BytecodeViewer.viewer.panelGroup1.setSelected(BytecodeViewer.viewer.panel1Fern.getModel(), true);
            else if (decompiler == 4)
                BytecodeViewer.viewer.panelGroup1.setSelected(BytecodeViewer.viewer.panel1Bytecode.getModel(), true);
            else if (decompiler == 5)
                BytecodeViewer.viewer.panelGroup1.setSelected(BytecodeViewer.viewer.panel1Hexcode.getModel(), true);
            else if (decompiler == 6)
                BytecodeViewer.viewer.panelGroup1.setSelected(BytecodeViewer.viewer.panel1Smali.getModel(), true);
            else if (decompiler == 7)
                BytecodeViewer.viewer.panelGroup1.setSelected(BytecodeViewer.viewer.panel1Krakatau.getModel(), true);
            else if (decompiler == 8)
                BytecodeViewer.viewer.panelGroup1.setSelected(BytecodeViewer.viewer.panel1KrakatauBytecode.getModel(), true);
            else if (decompiler == 9)
                BytecodeViewer.viewer.panelGroup1.setSelected(BytecodeViewer.viewer.panel1JDGUI.getModel(), true);
            else if (decompiler == 10)
                BytecodeViewer.viewer.panelGroup1.setSelected(BytecodeViewer.viewer.jadxJ1.getModel(), true);
            else if (decompiler == 11)
                BytecodeViewer.viewer.panelGroup1.setSelected(BytecodeViewer.viewer.asmText1.getModel(), true);

            decompiler = Integer.parseInt(DiskReader.loadString(BytecodeViewer.settingsName, 82, false));
            if (decompiler == 0)
                BytecodeViewer.viewer.panelGroup2.setSelected(BytecodeViewer.viewer.panel2None.getModel(), true);
            else if (decompiler == 1)
                BytecodeViewer.viewer.panelGroup2.setSelected(BytecodeViewer.viewer.panel2Proc.getModel(), true);
            else if (decompiler == 2)
                BytecodeViewer.viewer.panelGroup2.setSelected(BytecodeViewer.viewer.panel2CFR.getModel(), true);
            else if (decompiler == 3)
                BytecodeViewer.viewer.panelGroup2.setSelected(BytecodeViewer.viewer.panel2Fern.getModel(), true);
            else if (decompiler == 4)
                BytecodeViewer.viewer.panelGroup2.setSelected(BytecodeViewer.viewer.panel2Bytecode.getModel(), true);
            else if (decompiler == 5)
                BytecodeViewer.viewer.panelGroup2.setSelected(BytecodeViewer.viewer.panel2Hexcode.getModel(), true);
            else if (decompiler == 6)
                BytecodeViewer.viewer.panelGroup2.setSelected(BytecodeViewer.viewer.panel2Smali.getModel(), true);
            else if (decompiler == 7)
                BytecodeViewer.viewer.panelGroup2.setSelected(BytecodeViewer.viewer.panel2Krakatau.getModel(), true);
            else if (decompiler == 8)
                BytecodeViewer.viewer.panelGroup2.setSelected(BytecodeViewer.viewer.panel2KrakatauBytecode.getModel(), true);
            else if (decompiler == 9)
                BytecodeViewer.viewer.panelGroup2.setSelected(BytecodeViewer.viewer.panel2JDGUI.getModel(), true);
            else if (decompiler == 10)
                BytecodeViewer.viewer.panelGroup2.setSelected(BytecodeViewer.viewer.jadxJ2.getModel(), true);
            else if (decompiler == 11)
                BytecodeViewer.viewer.panelGroup2.setSelected(BytecodeViewer.viewer.asmText2.getModel(), true);

            decompiler = Integer.parseInt(DiskReader.loadString(BytecodeViewer.settingsName, 83, false));
            if (decompiler == 0)
                BytecodeViewer.viewer.panelGroup3.setSelected(BytecodeViewer.viewer.panel3None.getModel(), true);
            else if (decompiler == 1)
                BytecodeViewer.viewer.panelGroup3.setSelected(BytecodeViewer.viewer.panel3Proc.getModel(), true);
            else if (decompiler == 2)
                BytecodeViewer.viewer.panelGroup3.setSelected(BytecodeViewer.viewer.panel3CFR.getModel(), true);
            else if (decompiler == 3)
                BytecodeViewer.viewer.panelGroup3.setSelected(BytecodeViewer.viewer.panel3Fern.getModel(), true);
            else if (decompiler == 4)
                BytecodeViewer.viewer.panelGroup3.setSelected(BytecodeViewer.viewer.panel3Bytecode.getModel(), true);
            else if (decompiler == 5)
                BytecodeViewer.viewer.panelGroup3.setSelected(BytecodeViewer.viewer.panel3Hexcode.getModel(), true);
            else if (decompiler == 6)
                BytecodeViewer.viewer.panelGroup3.setSelected(BytecodeViewer.viewer.panel3Smali.getModel(), true);
            else if (decompiler == 7)
                BytecodeViewer.viewer.panelGroup3.setSelected(BytecodeViewer.viewer.panel3Krakatau.getModel(), true);
            else if (decompiler == 8)
                BytecodeViewer.viewer.panelGroup3.setSelected(BytecodeViewer.viewer.panel3KrakatauBytecode.getModel(), true);
            else if (decompiler == 9)
                BytecodeViewer.viewer.panelGroup3.setSelected(BytecodeViewer.viewer.panel3JDGUI.getModel(), true);
            else if (decompiler == 10)
                BytecodeViewer.viewer.panelGroup3.setSelected(BytecodeViewer.viewer.jadxJ3.getModel(), true);
            else if (decompiler == 11)
                BytecodeViewer.viewer.panelGroup3.setSelected(BytecodeViewer.viewer.asmText3.getModel(), true);

            BytecodeViewer.viewer.refreshOnChange.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 84, false)));

            boolean bool = Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 85, false));
            if (bool) {
                BytecodeViewer.viewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
                BytecodeViewer.viewer.isMaximized = true;
            }
            //86 is deprecated
            //87 is deprecated
            BytecodeViewer.lastDirectory = DiskReader.loadString(BytecodeViewer.settingsName, 88, false);
            BytecodeViewer.python = DiskReader.loadString(BytecodeViewer.settingsName, 89, false);
            BytecodeViewer.rt = DiskReader.loadString(BytecodeViewer.settingsName, 90, false);
            BytecodeViewer.viewer.panel1Proc_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 91, false)));
            BytecodeViewer.viewer.panel1CFR_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 92, false)));
            BytecodeViewer.viewer.panel1Fern_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 93, false)));
            BytecodeViewer.viewer.panel1Krakatau_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 94, false)));
            BytecodeViewer.viewer.panel1Smali_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 95, false)));
            BytecodeViewer.viewer.panel2Proc_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 96, false)));
            BytecodeViewer.viewer.panel2CFR_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 97, false)));
            BytecodeViewer.viewer.panel2Fern_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 98, false)));
            BytecodeViewer.viewer.panel2Krakatau_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 99, false)));
            BytecodeViewer.viewer.panel2Smali_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 100, false)));
            BytecodeViewer.viewer.panel3Proc_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 101, false)));
            BytecodeViewer.viewer.panel3CFR_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 101, false)));
            BytecodeViewer.viewer.panel3Fern_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 103, false)));
            BytecodeViewer.viewer.panel3Krakatau_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 104, false)));
            BytecodeViewer.viewer.panel3Smali_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 105, false)));
            BytecodeViewer.viewer.decodeAPKResources.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 106, false)));
            BytecodeViewer.library = DiskReader.loadString(BytecodeViewer.settingsName, 107, false);
            BytecodeViewer.pingback = Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 108, false));
            BytecodeViewer.viewer.panel1JDGUI_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 109, false)));
            BytecodeViewer.viewer.panel2JDGUI_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 110, false)));
            BytecodeViewer.viewer.panel3JDGUI_E.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 111, false)));
            BytecodeViewer.viewer.fontSpinner.setValue(Integer.parseInt(DiskReader.loadString(BytecodeViewer.settingsName, 112, false)));
            BytecodeViewer.deleteForeignLibraries = Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 113, false));
            decompiler = Integer.parseInt(DiskReader.loadString(BytecodeViewer.settingsName, 114, false));
            if (decompiler == 0)
                BytecodeViewer.viewer.apkConversionGroup.setSelected(BytecodeViewer.viewer.apkConversionDex.getModel(), true);
            else if (decompiler == 1)
                BytecodeViewer.viewer.apkConversionGroup.setSelected(BytecodeViewer.viewer.apkConversionEnjarify.getModel(), true);
            BytecodeViewer.python3 = DiskReader.loadString(BytecodeViewer.settingsName, 115, false);
            BytecodeViewer.javac = DiskReader.loadString(BytecodeViewer.settingsName, 116, false);
            BytecodeViewer.java = DiskReader.loadString(BytecodeViewer.settingsName, 117, false);
            BytecodeViewer.viewer.compileOnSave.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 118, false)));
            BytecodeViewer.viewer.autoCompileOnRefresh.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 119, false)));
            BytecodeViewer.warnForEditing = Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 120, false));
            BytecodeViewer.viewer.showFileInTabTitle.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 121, false)));
            BytecodeViewer.displayParentInTab = BytecodeViewer.viewer.showFileInTabTitle.isSelected();
            BytecodeViewer.viewer.forcePureAsciiAsText.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 122, false)));
            BytecodeViewer.viewer.synchronizedViewing.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 123, false)));
            BytecodeViewer.viewer.showClassMethods.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 124, false)));
            BytecodeViewer.viewer.ren.setSelected(Boolean.parseBoolean(DiskReader.loadString(BytecodeViewer.settingsName, 125, false)));
        } catch (Exception e) {
            //ignore because errors are expected, first start up and outdated settings.
            //e.printStackTrace();
        }
    }
}