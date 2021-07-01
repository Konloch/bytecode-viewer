package the.bytecode.club.bytecodeviewer;

import javax.swing.JFrame;

import me.konloch.kontainer.io.DiskReader;
import me.konloch.kontainer.io.DiskWriter;
import the.bytecode.club.bytecodeviewer.gui.theme.LAFTheme;
import the.bytecode.club.bytecodeviewer.gui.theme.RSTATheme;
import the.bytecode.club.bytecodeviewer.translation.Language;

import java.io.File;

import static the.bytecode.club.bytecodeviewer.Constants.*;

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

public class SettingsSerializer
{
    private static boolean settingsFileExists;
    
    public static void saveSettingsAsync()
    {
        Thread saveThread = new Thread(()-> saveSettings());
        saveThread.start();
    }
    
    public static synchronized void saveSettings() {
        try {
            DiskWriter.replaceFile(settingsName,
                    "BCV: " + VERSION, false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.rbr.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.rsy.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.din.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.dc4.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.das.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.hes.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.hdc.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.dgs.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.ner.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.den.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.rgn.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.bto.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.nns.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.uto.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.udv.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.rer.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.fdi.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.asc.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.decodeEnumSwitch.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.sugarEnums.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.decodeStringSwitch.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.arrayiter.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.collectioniter.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.innerClasses.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.removeBoilerPlate.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.removeInnerClassSynthetics.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.decodeLambdas.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.hideBridgeMethods.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.liftConstructorInit.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.removeDeadMethods.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.removeBadGenerics.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.sugarAsserts.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.sugarBoxing.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.showVersion.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.decodeFinally.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.tidyMonitors.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.lenient.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.dumpClassPath.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.comments.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.forceTopSort.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.forceTopSortAggress.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.stringBuffer.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.stringBuilder.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.silent.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.recover.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.eclipse.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.override.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.showInferrable.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.aexagg.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.forceCondPropagate.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.hideUTF.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.hideLongStrings.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.commentMonitor.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.allowCorrecting.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.labelledBlocks.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.j14ClassOBJ.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.hideLangImports.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.recoveryTypeClash.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.recoveryTypehInts.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.forceTurningIFs.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.forLoopAGGCapture.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.forceExceptionPrune.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.showDebugLineNumbers.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.simplifyMemberReferences.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.mergeVariables.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.unicodeOutputEnabled.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.retainPointlessSwitches.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.includeLineNumbersInBytecode.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.includeErrorDiagnostics.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.retainRedunantCasts.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.alwaysGenerateExceptionVars.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.showSyntheticMembers.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.forceExplicitTypeArguments.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.forceExplicitImports.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.flattenSwitchBlocks.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.excludeNestedTypes.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.appendBracketsToLabels.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.debugHelpers.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    "deprecated", false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.updateCheck.isSelected()), false);
    
            DiskWriter.writeNewLine(settingsName, String.valueOf(BytecodeViewer.viewer.viewPane1.getSelectedViewer()), false);
            DiskWriter.writeNewLine(settingsName, String.valueOf(BytecodeViewer.viewer.viewPane2.getSelectedViewer()), false);
            DiskWriter.writeNewLine(settingsName, String.valueOf(BytecodeViewer.viewer.viewPane3.getSelectedViewer()), false);
            
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.refreshOnChange.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.isMaximized), false);
            DiskWriter.writeNewLine(settingsName,
                    "deprecated", false);
            DiskWriter.writeNewLine(settingsName,
                    "deprecated", false);
            DiskWriter.writeNewLine(settingsName,
                    Configuration.lastDirectory, false);
            DiskWriter.writeNewLine(settingsName,
                    Configuration.python, false);
            DiskWriter.writeNewLine(settingsName,
                    Configuration.rt, false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane1.getProcyon().getEditable().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane1.getCFR().getEditable().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane1.getFern().getEditable().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane1.getKrakatau().getEditable().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane1.getSmali().getEditable().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane2.getProcyon().getJava().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane2.getCFR().getJava().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane2.getFern().getJava().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane2.getKrakatau().getJava().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane2.getSmali().getJava().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane3.getProcyon().getJava().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane3.getCFR().getJava().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane3.getFern().getJava().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane3.getKrakatau().getJava().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane3.getSmali().getJava().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.decodeAPKResources.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    Configuration.library, false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(Configuration.pingback), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane1.getJD().getEditable().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane2.getJD().getJava().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane3.getJD().getJava().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.fontSpinner.getValue()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(Configuration.deleteForeignLibraries), false);
            
            if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionDex.getModel()))
                DiskWriter.writeNewLine(settingsName, "0", false);
            else if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionEnjarify.getModel()))
                DiskWriter.writeNewLine(settingsName, "1", false);
            
            DiskWriter.writeNewLine(settingsName,
                    Configuration.python3, false);
            DiskWriter.writeNewLine(settingsName,
                    Configuration.javac, false);
            DiskWriter.writeNewLine(settingsName,
                    Configuration.java, false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.compileOnSave.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.autoCompileOnRefresh.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(Configuration.warnForEditing), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.showFileInTabTitle.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.forcePureAsciiAsText.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.synchronizedViewing.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.showClassMethods.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.ren.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.viewPane1.getJADX().getEditable().isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    Configuration.lafTheme.name(), false);
            DiskWriter.writeNewLine(settingsName,
                    Configuration.rstaTheme.name(), false);
            DiskWriter.writeNewLine(settingsName,
                    String.valueOf(BytecodeViewer.viewer.simplifyNameInTabTitle.isSelected()), false);
            DiskWriter.writeNewLine(settingsName,
                    Configuration.language.name(), false);
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }
    
    //utilizes the Disk Reader's caching system.
    public static void loadSettings()
    {
        if(!settingsFileExists)
            return;
        
        Settings.firstBoot = false;
        
        try
        {
            //parse the cached file from memory (from preload)
            BytecodeViewer.viewer.rbr.setSelected(asBoolean(1));
            BytecodeViewer.viewer.rsy.setSelected(asBoolean(2));
            BytecodeViewer.viewer.din.setSelected(asBoolean(3));
            BytecodeViewer.viewer.dc4.setSelected(asBoolean(4));
            BytecodeViewer.viewer.das.setSelected(asBoolean(5));
            BytecodeViewer.viewer.hes.setSelected(asBoolean(6));
            BytecodeViewer.viewer.hdc.setSelected(asBoolean(7));
            BytecodeViewer.viewer.dgs.setSelected(asBoolean(8));
            BytecodeViewer.viewer.ner.setSelected(asBoolean(9));
            BytecodeViewer.viewer.den.setSelected(asBoolean(10));
            BytecodeViewer.viewer.rgn.setSelected(asBoolean(11));
            BytecodeViewer.viewer.bto.setSelected(asBoolean(12));
            BytecodeViewer.viewer.nns.setSelected(asBoolean(13));
            BytecodeViewer.viewer.uto.setSelected(asBoolean(14));
            BytecodeViewer.viewer.udv.setSelected(asBoolean(15));
            BytecodeViewer.viewer.rer.setSelected(asBoolean(16));
            BytecodeViewer.viewer.fdi.setSelected(asBoolean(17));
            BytecodeViewer.viewer.asc.setSelected(asBoolean(18));
            BytecodeViewer.viewer.decodeEnumSwitch.setSelected(asBoolean(19));
            BytecodeViewer.viewer.sugarEnums.setSelected(asBoolean(20));
            BytecodeViewer.viewer.decodeStringSwitch.setSelected(asBoolean(21));
            BytecodeViewer.viewer.arrayiter.setSelected(asBoolean(22));
            BytecodeViewer.viewer.collectioniter.setSelected(asBoolean(23));
            BytecodeViewer.viewer.innerClasses.setSelected(asBoolean(24));
            BytecodeViewer.viewer.removeBoilerPlate.setSelected(asBoolean(25));
            BytecodeViewer.viewer.removeInnerClassSynthetics.setSelected(asBoolean(26));
            BytecodeViewer.viewer.decodeLambdas.setSelected(asBoolean(27));
            BytecodeViewer.viewer.hideBridgeMethods.setSelected(asBoolean(28));
            BytecodeViewer.viewer.liftConstructorInit.setSelected(asBoolean(29));
            BytecodeViewer.viewer.removeDeadMethods.setSelected(asBoolean(30));
            BytecodeViewer.viewer.removeBadGenerics.setSelected(asBoolean(31));
            BytecodeViewer.viewer.sugarAsserts.setSelected(asBoolean(32));
            BytecodeViewer.viewer.sugarBoxing.setSelected(asBoolean(33));
            BytecodeViewer.viewer.showVersion.setSelected(asBoolean(34));
            BytecodeViewer.viewer.decodeFinally.setSelected(asBoolean(35));
            BytecodeViewer.viewer.tidyMonitors.setSelected(asBoolean(36));
            BytecodeViewer.viewer.lenient.setSelected(asBoolean(37));
            BytecodeViewer.viewer.dumpClassPath.setSelected(asBoolean(38));
            BytecodeViewer.viewer.comments.setSelected(asBoolean(39));
            BytecodeViewer.viewer.forceTopSort.setSelected(asBoolean(40));
            BytecodeViewer.viewer.forceTopSortAggress.setSelected(asBoolean(41));
            BytecodeViewer.viewer.stringBuffer.setSelected(asBoolean(42));
            BytecodeViewer.viewer.stringBuilder.setSelected(asBoolean(43));
            BytecodeViewer.viewer.silent.setSelected(asBoolean(44));
            BytecodeViewer.viewer.recover.setSelected(asBoolean(45));
            BytecodeViewer.viewer.eclipse.setSelected(asBoolean(46));
            BytecodeViewer.viewer.override.setSelected(asBoolean(47));
            BytecodeViewer.viewer.showInferrable.setSelected(asBoolean(48));
            BytecodeViewer.viewer.aexagg.setSelected(asBoolean(49));
            BytecodeViewer.viewer.forceCondPropagate.setSelected(asBoolean(50));
            BytecodeViewer.viewer.hideUTF.setSelected(asBoolean(51));
            BytecodeViewer.viewer.hideLongStrings.setSelected(asBoolean(52));
            BytecodeViewer.viewer.commentMonitor.setSelected(asBoolean(53));
            BytecodeViewer.viewer.allowCorrecting.setSelected(asBoolean(54));
            BytecodeViewer.viewer.labelledBlocks.setSelected(asBoolean(55));
            BytecodeViewer.viewer.j14ClassOBJ.setSelected(asBoolean(56));
            BytecodeViewer.viewer.hideLangImports.setSelected(asBoolean(57));
            BytecodeViewer.viewer.recoveryTypeClash.setSelected(asBoolean(58));
            BytecodeViewer.viewer.recoveryTypehInts.setSelected(asBoolean(59));
            BytecodeViewer.viewer.forceTurningIFs.setSelected(asBoolean(60));
            BytecodeViewer.viewer.forLoopAGGCapture.setSelected(asBoolean(61));
            BytecodeViewer.viewer.forceExceptionPrune.setSelected(asBoolean(62));
            BytecodeViewer.viewer.showDebugLineNumbers.setSelected(asBoolean(63));
            BytecodeViewer.viewer.simplifyMemberReferences.setSelected(asBoolean(64));
            BytecodeViewer.viewer.mergeVariables.setSelected(asBoolean(65));
            BytecodeViewer.viewer.unicodeOutputEnabled.setSelected(asBoolean(66));
            BytecodeViewer.viewer.retainPointlessSwitches.setSelected(asBoolean(67));
            BytecodeViewer.viewer.includeLineNumbersInBytecode.setSelected(asBoolean(68));
            BytecodeViewer.viewer.includeErrorDiagnostics.setSelected(asBoolean(69));
            BytecodeViewer.viewer.retainRedunantCasts.setSelected(asBoolean(70));
            BytecodeViewer.viewer.alwaysGenerateExceptionVars.setSelected(asBoolean(71));
            BytecodeViewer.viewer.showSyntheticMembers.setSelected(asBoolean(72));
            BytecodeViewer.viewer.forceExplicitTypeArguments.setSelected(asBoolean(73));
            BytecodeViewer.viewer.forceExplicitImports.setSelected(asBoolean(74));
            BytecodeViewer.viewer.flattenSwitchBlocks.setSelected(asBoolean(75));
            BytecodeViewer.viewer.excludeNestedTypes.setSelected(asBoolean(76));
            BytecodeViewer.viewer.appendBracketsToLabels.setSelected(asBoolean(77));
            BytecodeViewer.viewer.debugHelpers.setSelected(asBoolean(78));
            //79 is deprecated
            BytecodeViewer.viewer.updateCheck.setSelected(asBoolean(80));
            BytecodeViewer.viewer.viewPane1.setSelectedViewer(asInt(81));
            BytecodeViewer.viewer.viewPane2.setSelectedViewer(asInt(82));
            BytecodeViewer.viewer.viewPane3.setSelectedViewer(asInt(83));

            BytecodeViewer.viewer.refreshOnChange.setSelected(asBoolean(84));

            boolean bool = Boolean.parseBoolean(asString(85));
            if (bool) {
                BytecodeViewer.viewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
                BytecodeViewer.viewer.isMaximized = true;
            }
            //86 is deprecated
            //87 is deprecated
            Configuration.lastDirectory = asString(88);
            Configuration.python = asString(89);
            Configuration.rt = asString(90);
            BytecodeViewer.viewer.viewPane1.getProcyon().getEditable().setSelected(asBoolean(91));
            BytecodeViewer.viewer.viewPane1.getCFR().getEditable().setSelected(asBoolean(92));
            BytecodeViewer.viewer.viewPane1.getFern().getEditable().setSelected(asBoolean(93));
            BytecodeViewer.viewer.viewPane1.getKrakatau().getEditable().setSelected(asBoolean(94));
            BytecodeViewer.viewer.viewPane1.getSmali().getEditable().setSelected(asBoolean(95));
            BytecodeViewer.viewer.viewPane2.getProcyon().getEditable().setSelected(asBoolean(96));
            BytecodeViewer.viewer.viewPane2.getCFR().getEditable().setSelected(asBoolean(97));
            BytecodeViewer.viewer.viewPane2.getFern().getEditable().setSelected(asBoolean(98));
            BytecodeViewer.viewer.viewPane2.getKrakatau().getEditable().setSelected(asBoolean(99));
            BytecodeViewer.viewer.viewPane2.getSmali().getEditable().setSelected(asBoolean(100));
            BytecodeViewer.viewer.viewPane3.getProcyon().getEditable().setSelected(asBoolean(101));
            BytecodeViewer.viewer.viewPane3.getCFR().getEditable().setSelected(asBoolean(102));
            BytecodeViewer.viewer.viewPane3.getFern().getEditable().setSelected(asBoolean(103));
            BytecodeViewer.viewer.viewPane3.getKrakatau().getEditable().setSelected(asBoolean(104));
            BytecodeViewer.viewer.viewPane3.getSmali().getEditable().setSelected(asBoolean(105));
            BytecodeViewer.viewer.decodeAPKResources.setSelected(asBoolean(106));
            Configuration.library = asString(107);
            Configuration.pingback = asBoolean(108);
            BytecodeViewer.viewer.viewPane1.getJD().getEditable().setSelected(asBoolean(109));
            BytecodeViewer.viewer.viewPane2.getJD().getEditable().setSelected(asBoolean(110));
            BytecodeViewer.viewer.viewPane3.getJD().getEditable().setSelected(asBoolean(111));
            BytecodeViewer.viewer.fontSpinner.setValue(asInt(112));
            Configuration.deleteForeignLibraries = asBoolean(113);
            
            //APK Decompiler
            switch(asInt(114))
            {
                case 0:
                    BytecodeViewer.viewer.apkConversionGroup.setSelected(BytecodeViewer.viewer.apkConversionDex.getModel(), true);
                    break;
                case 1:
                    BytecodeViewer.viewer.apkConversionGroup.setSelected(BytecodeViewer.viewer.apkConversionEnjarify.getModel(), true);
                    break;
            }
    
            Configuration.python3 = asString(115);
            Configuration.javac = asString(116);
            Configuration.java = asString(117);
            BytecodeViewer.viewer.compileOnSave.setSelected(asBoolean(118));
            BytecodeViewer.viewer.autoCompileOnRefresh.setSelected(asBoolean(119));
            Configuration.warnForEditing = asBoolean(120);
            BytecodeViewer.viewer.showFileInTabTitle.setSelected(asBoolean(121));
            Configuration.displayParentInTab = BytecodeViewer.viewer.showFileInTabTitle.isSelected();
            BytecodeViewer.viewer.forcePureAsciiAsText.setSelected(asBoolean(122));
            BytecodeViewer.viewer.synchronizedViewing.setSelected(asBoolean(123));
            BytecodeViewer.viewer.showClassMethods.setSelected(asBoolean(124));
            BytecodeViewer.viewer.ren.setSelected(asBoolean(125));
            BytecodeViewer.viewer.viewPane1.getJADX().getEditable().setSelected(asBoolean(126));
            //line 127 is used for theme on preload
            //line 128 is used for theme on preload
            BytecodeViewer.viewer.simplifyNameInTabTitle.setSelected(asBoolean(129));
            Configuration.simplifiedTabNames = BytecodeViewer.viewer.simplifyNameInTabTitle.isSelected();
            //line 130 is used for preload
            if(Configuration.language != Language.ENGLISH)
            {
                Language.ENGLISH.loadLanguage(); //load english first incase the translation file is missing anything
                Configuration.language.loadLanguage(); //load translation file and swap as needed
            }
            Settings.hasSetLanguageAsSystemLanguage = true;
        } catch (Exception e) {
            e.printStackTrace();
            //ignore because errors are expected, first start up and outdated settings.
            //e.printStackTrace();
        }
    }
    
    /**
     * Preload data used to configure the looks and components of the application
     */
    public static void preloadSettingsFile()
    {
        try
        {
            settingsFileExists = new File(settingsName).exists();
            
            if(!settingsFileExists)
                return;
            
            //precache the file
            DiskReader.loadString(settingsName, 0, true);
            
            //process the cached file
            Configuration.lafTheme = LAFTheme.valueOf(asString(127));
            Configuration.rstaTheme = RSTATheme.valueOf(asString(128));
            //line 129 is used normal loading
            Configuration.language = Language.valueOf(asString(130));
        }
        catch (Exception e)
        {
            //ignore because errors are expected, first start up and outdated settings.
            //e.printStackTrace();
        }
    }
    
    public static String asString(int lineNumber) throws Exception
    {
        return DiskReader.loadString(settingsName, lineNumber, false);
    }
    
    public static boolean asBoolean(int lineNumber) throws Exception
    {
        return Boolean.parseBoolean(DiskReader.loadString(settingsName, lineNumber, false));
    }
    
    public static int asInt(int lineNumber) throws Exception
    {
        return Integer.parseInt(DiskReader.loadString(settingsName, lineNumber, false));
    }
}