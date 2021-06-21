package the.bytecode.club.bytecodeviewer;

import javax.swing.JFrame;
import me.konloch.kontainer.io.DiskReader;
import me.konloch.kontainer.io.DiskWriter;

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

public class Settings {

    public static void saveSettings() {
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
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }

    public static void loadSettings() { //utilizes the Disk Reader's caching system.
        try {
            BytecodeViewer.viewer.rbr.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 1, true)));
            BytecodeViewer.viewer.rsy.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 2, false)));
            BytecodeViewer.viewer.din.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 3, false)));
            BytecodeViewer.viewer.dc4.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 4, false)));
            BytecodeViewer.viewer.das.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 5, false)));
            BytecodeViewer.viewer.hes.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 6, false)));
            BytecodeViewer.viewer.hdc.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 7, false)));
            BytecodeViewer.viewer.dgs.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 8, false)));
            BytecodeViewer.viewer.ner.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 9, false)));
            BytecodeViewer.viewer.den.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 10, false)));
            BytecodeViewer.viewer.rgn.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 11, false)));
            BytecodeViewer.viewer.bto.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 12, false)));
            BytecodeViewer.viewer.nns.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 13, false)));
            BytecodeViewer.viewer.uto.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 14, false)));
            BytecodeViewer.viewer.udv.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 15, false)));
            BytecodeViewer.viewer.rer.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 16, false)));
            BytecodeViewer.viewer.fdi.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 17, false)));
            BytecodeViewer.viewer.asc.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 18, false)));
            BytecodeViewer.viewer.decodeEnumSwitch.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 19, false)));
            BytecodeViewer.viewer.sugarEnums.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 20, false)));
            BytecodeViewer.viewer.decodeStringSwitch.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 21, false)));
            BytecodeViewer.viewer.arrayiter.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 22, false)));
            BytecodeViewer.viewer.collectioniter.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 23, false)));
            BytecodeViewer.viewer.innerClasses.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 24, false)));
            BytecodeViewer.viewer.removeBoilerPlate.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 25, false)));
            BytecodeViewer.viewer.removeInnerClassSynthetics.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 26, false)));
            BytecodeViewer.viewer.decodeLambdas.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 27, false)));
            BytecodeViewer.viewer.hideBridgeMethods.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 28, false)));
            BytecodeViewer.viewer.liftConstructorInit.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 29, false)));
            BytecodeViewer.viewer.removeDeadMethods.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 30, false)));
            BytecodeViewer.viewer.removeBadGenerics.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 31, false)));
            BytecodeViewer.viewer.sugarAsserts.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 32, false)));
            BytecodeViewer.viewer.sugarBoxing.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 33, false)));
            BytecodeViewer.viewer.showVersion.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 34, false)));
            BytecodeViewer.viewer.decodeFinally.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 35, false)));
            BytecodeViewer.viewer.tidyMonitors.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 36, false)));
            BytecodeViewer.viewer.lenient.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 37, false)));
            BytecodeViewer.viewer.dumpClassPath.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 38, false)));
            BytecodeViewer.viewer.comments.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 39, false)));
            BytecodeViewer.viewer.forceTopSort.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 40, false)));
            BytecodeViewer.viewer.forceTopSortAggress.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 41, false)));
            BytecodeViewer.viewer.stringBuffer.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 42, false)));
            BytecodeViewer.viewer.stringBuilder.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 43, false)));
            BytecodeViewer.viewer.silent.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 44, false)));
            BytecodeViewer.viewer.recover.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 45, false)));
            BytecodeViewer.viewer.eclipse.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 46, false)));
            BytecodeViewer.viewer.override.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 47, false)));
            BytecodeViewer.viewer.showInferrable.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 48, false)));
            BytecodeViewer.viewer.aexagg.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 49, false)));
            BytecodeViewer.viewer.forceCondPropagate.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 50, false)));
            BytecodeViewer.viewer.hideUTF.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 51, false)));
            BytecodeViewer.viewer.hideLongStrings.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 52, false)));
            BytecodeViewer.viewer.commentMonitor.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 53, false)));
            BytecodeViewer.viewer.allowCorrecting.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 54, false)));
            BytecodeViewer.viewer.labelledBlocks.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 55, false)));
            BytecodeViewer.viewer.j14ClassOBJ.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 56, false)));
            BytecodeViewer.viewer.hideLangImports.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 57, false)));
            BytecodeViewer.viewer.recoveryTypeClash.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 58, false)));
            BytecodeViewer.viewer.recoveryTypehInts.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 59, false)));
            BytecodeViewer.viewer.forceTurningIFs.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 60, false)));
            BytecodeViewer.viewer.forLoopAGGCapture.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 61, false)));
            BytecodeViewer.viewer.forceExceptionPrune.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 62, false)));
            BytecodeViewer.viewer.showDebugLineNumbers.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 63, false)));
            BytecodeViewer.viewer.simplifyMemberReferences.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 64, false)));
            BytecodeViewer.viewer.mergeVariables.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 65, false)));
            BytecodeViewer.viewer.unicodeOutputEnabled.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 66, false)));
            BytecodeViewer.viewer.retainPointlessSwitches.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 67, false)));
            BytecodeViewer.viewer.includeLineNumbersInBytecode.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 68, false)));
            BytecodeViewer.viewer.includeErrorDiagnostics.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 69, false)));
            BytecodeViewer.viewer.retainRedunantCasts.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 70, false)));
            BytecodeViewer.viewer.alwaysGenerateExceptionVars.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 71, false)));
            BytecodeViewer.viewer.showSyntheticMembers.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 72, false)));
            BytecodeViewer.viewer.forceExplicitTypeArguments.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 73, false)));
            BytecodeViewer.viewer.forceExplicitImports.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 74, false)));
            BytecodeViewer.viewer.flattenSwitchBlocks.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 75, false)));
            BytecodeViewer.viewer.excludeNestedTypes.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 76, false)));
            BytecodeViewer.viewer.appendBracketsToLabels.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 77, false)));
            BytecodeViewer.viewer.debugHelpers.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 78, false)));
            //79 is deprecated
            BytecodeViewer.viewer.updateCheck.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 80, false)));
            BytecodeViewer.viewer.viewPane1.setSelectedViewer(getInt(81));
            BytecodeViewer.viewer.viewPane2.setSelectedViewer(getInt(82));
            BytecodeViewer.viewer.viewPane3.setSelectedViewer(getInt(83));

            BytecodeViewer.viewer.refreshOnChange.setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 84, false)));

            boolean bool = Boolean.parseBoolean(DiskReader.loadString(settingsName, 85, false));
            if (bool) {
                BytecodeViewer.viewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
                BytecodeViewer.viewer.isMaximized = true;
            }
            //86 is deprecated
            //87 is deprecated
            Configuration.lastDirectory = DiskReader.loadString(settingsName, 88, false);
            Configuration.python = DiskReader.loadString(settingsName, 89, false);
            Configuration.rt = DiskReader.loadString(settingsName, 90, false);
            BytecodeViewer.viewer.viewPane1.getProcyon().getEditable().setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 91, false)));
            BytecodeViewer.viewer.viewPane1.getCFR().getEditable().setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 92, false)));
            BytecodeViewer.viewer.viewPane1.getFern().getEditable().setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 93, false)));
            BytecodeViewer.viewer.viewPane1.getKrakatau().getEditable().setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 94, false)));
            BytecodeViewer.viewer.viewPane1.getSmali().getEditable().setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 95, false)));
            BytecodeViewer.viewer.viewPane2.getProcyon().getEditable().setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 96, false)));
            BytecodeViewer.viewer.viewPane2.getCFR().getEditable().setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 97, false)));
            BytecodeViewer.viewer.viewPane2.getFern().getEditable().setSelected(Boolean.parseBoolean(DiskReader.loadString(settingsName, 98, false)));
            BytecodeViewer.viewer.viewPane2.getKrakatau().getEditable().setSelected(asBoolean(99));
            BytecodeViewer.viewer.viewPane2.getSmali().getEditable().setSelected(asBoolean(100));
            BytecodeViewer.viewer.viewPane3.getProcyon().getEditable().setSelected(asBoolean(101));
            BytecodeViewer.viewer.viewPane3.getCFR().getEditable().setSelected(asBoolean(102));
            BytecodeViewer.viewer.viewPane3.getFern().getEditable().setSelected(asBoolean(103));
            BytecodeViewer.viewer.viewPane3.getKrakatau().getEditable().setSelected(asBoolean(104));
            BytecodeViewer.viewer.viewPane3.getSmali().getEditable().setSelected(asBoolean(105));
            BytecodeViewer.viewer.decodeAPKResources.setSelected(asBoolean(106));
            Configuration.library = DiskReader.loadString(settingsName, 107, false);
            Configuration.pingback = asBoolean(108);
            BytecodeViewer.viewer.viewPane1.getJD().getEditable().setSelected(asBoolean(109));
            BytecodeViewer.viewer.viewPane2.getJD().getEditable().setSelected(asBoolean(110));
            BytecodeViewer.viewer.viewPane3.getJD().getEditable().setSelected(asBoolean(111));
            BytecodeViewer.viewer.fontSpinner.setValue(getInt(112));
            Configuration.deleteForeignLibraries = asBoolean(113);
            int apkDecompiler = getInt(114);
            
            if (apkDecompiler == 0)
                BytecodeViewer.viewer.apkConversionGroup.setSelected(BytecodeViewer.viewer.apkConversionDex.getModel(), true);
            else if (apkDecompiler == 1)
                BytecodeViewer.viewer.apkConversionGroup.setSelected(BytecodeViewer.viewer.apkConversionEnjarify.getModel(), true);
    
            Configuration.python3 = DiskReader.loadString(settingsName, 115, false);
            Configuration.javac = DiskReader.loadString(settingsName, 116, false);
            Configuration.java = DiskReader.loadString(settingsName, 117, false);
            BytecodeViewer.viewer.compileOnSave.setSelected(asBoolean(118));
            BytecodeViewer.viewer.autoCompileOnRefresh.setSelected(asBoolean(119));
            Configuration.warnForEditing = Boolean.parseBoolean(DiskReader.loadString(settingsName, 120, false));
            BytecodeViewer.viewer.showFileInTabTitle.setSelected(asBoolean(121));
            Configuration.displayParentInTab = BytecodeViewer.viewer.showFileInTabTitle.isSelected();
            BytecodeViewer.viewer.forcePureAsciiAsText.setSelected(asBoolean(122));
            BytecodeViewer.viewer.synchronizedViewing.setSelected(asBoolean(123));
            BytecodeViewer.viewer.showClassMethods.setSelected(asBoolean(124));
            BytecodeViewer.viewer.ren.setSelected(asBoolean(125));
            BytecodeViewer.viewer.viewPane1.getJADX().getEditable().setSelected(asBoolean(126));
        } catch (Exception e) {
            //ignore because errors are expected, first start up and outdated settings.
            //e.printStackTrace();
        }
    }
    
    public static boolean asBoolean(int lineNumber) throws Exception
    {
        return Boolean.parseBoolean(DiskReader.loadString(settingsName, lineNumber, false));
    }
    
    public static int getInt(int lineNumber) throws Exception
    {
        return Integer.parseInt(DiskReader.loadString(settingsName, lineNumber, false));
    }
}