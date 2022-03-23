package the.bytecode.club.bytecodeviewer;

import java.io.File;
import javax.swing.JFrame;
import me.konloch.kontainer.io.DiskReader;
import me.konloch.kontainer.io.DiskWriter;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.gui.theme.LAFTheme;
import the.bytecode.club.bytecodeviewer.gui.theme.RSTATheme;
import the.bytecode.club.bytecodeviewer.translation.Language;

import static the.bytecode.club.bytecodeviewer.Constants.VERSION;
import static the.bytecode.club.bytecodeviewer.Constants.settingsName;

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
        Thread saveThread = new Thread(SettingsSerializer::saveSettings, "Save Settings");
        saveThread.start();
    }
    
    public static synchronized void saveSettings()
    {
        try
        {
            DiskWriter.replaceFile(settingsName, "BCV: " + VERSION, false);
            save(BytecodeViewer.viewer.rbr.isSelected());
            save(BytecodeViewer.viewer.rsy.isSelected());
            save(BytecodeViewer.viewer.din.isSelected());
            save(BytecodeViewer.viewer.dc4.isSelected());
            save(BytecodeViewer.viewer.das.isSelected());
            save(BytecodeViewer.viewer.hes.isSelected());
            save(BytecodeViewer.viewer.hdc.isSelected());
            save(BytecodeViewer.viewer.dgs.isSelected());
            save(BytecodeViewer.viewer.ner.isSelected());
            save(BytecodeViewer.viewer.den.isSelected());
            save(BytecodeViewer.viewer.rgn.isSelected());
            save(BytecodeViewer.viewer.bto.isSelected());
            save(BytecodeViewer.viewer.nns.isSelected());
            save(BytecodeViewer.viewer.uto.isSelected());
            save(BytecodeViewer.viewer.udv.isSelected());
            save(BytecodeViewer.viewer.rer.isSelected());
            save(BytecodeViewer.viewer.fdi.isSelected());
            save(BytecodeViewer.viewer.asc.isSelected());
            save(BytecodeViewer.viewer.decodeEnumSwitch.isSelected());
            save(BytecodeViewer.viewer.sugarEnums.isSelected());
            save(BytecodeViewer.viewer.decodeStringSwitch.isSelected());
            save(BytecodeViewer.viewer.arrayiter.isSelected());
            save(BytecodeViewer.viewer.collectioniter.isSelected());
            save(BytecodeViewer.viewer.innerClasses.isSelected());
            save(BytecodeViewer.viewer.removeBoilerPlate.isSelected());
            save(BytecodeViewer.viewer.removeInnerClassSynthetics.isSelected());
            save(BytecodeViewer.viewer.decodeLambdas.isSelected());
            save(BytecodeViewer.viewer.hideBridgeMethods.isSelected());
            save(BytecodeViewer.viewer.liftConstructorInit.isSelected());
            save(BytecodeViewer.viewer.removeDeadMethods.isSelected());
            save(BytecodeViewer.viewer.removeBadGenerics.isSelected());
            save(BytecodeViewer.viewer.sugarAsserts.isSelected());
            save(BytecodeViewer.viewer.sugarBoxing.isSelected());
            save(BytecodeViewer.viewer.showVersion.isSelected());
            save(BytecodeViewer.viewer.decodeFinally.isSelected());
            save(BytecodeViewer.viewer.tidyMonitors.isSelected());
            save(BytecodeViewer.viewer.lenient.isSelected());
            save(BytecodeViewer.viewer.dumpClassPath.isSelected());
            save(BytecodeViewer.viewer.comments.isSelected());
            save(BytecodeViewer.viewer.forceTopSort.isSelected());
            save(BytecodeViewer.viewer.forceTopSortAggress.isSelected());
            save(BytecodeViewer.viewer.stringBuffer.isSelected());
            save(BytecodeViewer.viewer.stringBuilder.isSelected());
            save(BytecodeViewer.viewer.silent.isSelected());
            save(BytecodeViewer.viewer.recover.isSelected());
            save(BytecodeViewer.viewer.eclipse.isSelected());
            save(BytecodeViewer.viewer.override.isSelected());
            save(BytecodeViewer.viewer.showInferrable.isSelected());
            save(BytecodeViewer.viewer.aexagg.isSelected());
            save(BytecodeViewer.viewer.forceCondPropagate.isSelected());
            save(BytecodeViewer.viewer.hideUTF.isSelected());
            save(BytecodeViewer.viewer.hideLongStrings.isSelected());
            save(BytecodeViewer.viewer.commentMonitor.isSelected());
            save(BytecodeViewer.viewer.allowCorrecting.isSelected());
            save(BytecodeViewer.viewer.labelledBlocks.isSelected());
            save(BytecodeViewer.viewer.j14ClassOBJ.isSelected());
            save(BytecodeViewer.viewer.hideLangImports.isSelected());
            save(BytecodeViewer.viewer.recoveryTypeClash.isSelected());
            save(BytecodeViewer.viewer.recoveryTypehInts.isSelected());
            save(BytecodeViewer.viewer.forceTurningIFs.isSelected());
            save(BytecodeViewer.viewer.forLoopAGGCapture.isSelected());
            save(BytecodeViewer.viewer.forceExceptionPrune.isSelected());
            save(BytecodeViewer.viewer.showDebugLineNumbers.isSelected());
            save(BytecodeViewer.viewer.simplifyMemberReferences.isSelected());
            save(BytecodeViewer.viewer.mergeVariables.isSelected());
            save(BytecodeViewer.viewer.unicodeOutputEnabled.isSelected());
            save(BytecodeViewer.viewer.retainPointlessSwitches.isSelected());
            save(BytecodeViewer.viewer.includeLineNumbersInBytecode.isSelected());
            save(BytecodeViewer.viewer.includeErrorDiagnostics.isSelected());
            save(BytecodeViewer.viewer.retainRedunantCasts.isSelected());
            save(BytecodeViewer.viewer.alwaysGenerateExceptionVars.isSelected());
            save(BytecodeViewer.viewer.showSyntheticMembers.isSelected());
            save(BytecodeViewer.viewer.forceExplicitTypeArguments.isSelected());
            save(BytecodeViewer.viewer.forceExplicitImports.isSelected());
            save(BytecodeViewer.viewer.flattenSwitchBlocks.isSelected());
            save(BytecodeViewer.viewer.excludeNestedTypes.isSelected());
            save(BytecodeViewer.viewer.appendBracketsToLabels.isSelected());
            save(BytecodeViewer.viewer.debugHelpers.isSelected());
            save("deprecated");
            save(BytecodeViewer.viewer.updateCheck.isSelected());
            save(BytecodeViewer.viewer.viewPane1.getSelectedDecompiler().ordinal());
            save(BytecodeViewer.viewer.viewPane2.getSelectedDecompiler().ordinal());
            save(BytecodeViewer.viewer.viewPane3.getSelectedDecompiler().ordinal());
            save(BytecodeViewer.viewer.refreshOnChange.isSelected());
            save(BytecodeViewer.viewer.isMaximized);
            save("deprecated");
            save("deprecated");
            save(Configuration.lastOpenDirectory);
            save(Configuration.python2);
            save(Configuration.rt);
            save("deprecated");
            save("deprecated");
            save("deprecated");
            save("deprecated");
            save("deprecated");
            save("deprecated");
            save("deprecated");
            save("deprecated");
            save("deprecated");
            save("deprecated");
            save("deprecated");
            save("deprecated");
            save("deprecated");
            save("deprecated");
            save("deprecated");
            save(BytecodeViewer.viewer.decodeAPKResources.isSelected());
            save(Configuration.library);
            save(Configuration.pingback);
            save("deprecated");
            save("deprecated");
            save("deprecated");
            save(BytecodeViewer.viewer.getFontSize());
            save(Configuration.deleteForeignLibraries);
    
            if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionDex.getModel()))
                DiskWriter.writeNewLine(settingsName, "0");
            else if (BytecodeViewer.viewer.apkConversionGroup.isSelected(BytecodeViewer.viewer.apkConversionEnjarify.getModel()))
                DiskWriter.writeNewLine(settingsName, "1");
    
            save(Configuration.python3);
            save(Configuration.javac);
            save(Configuration.java);
            save(BytecodeViewer.viewer.compileOnSave.isSelected());
            save(BytecodeViewer.viewer.autoCompileOnRefresh.isSelected());
            save(Configuration.warnForEditing);
            save(BytecodeViewer.viewer.showFileInTabTitle.isSelected());
            save(BytecodeViewer.viewer.forcePureAsciiAsText.isSelected());
            save(BytecodeViewer.viewer.synchronizedViewing.isSelected());
            save(BytecodeViewer.viewer.showClassMethods.isSelected());
            save(BytecodeViewer.viewer.ren.isSelected());
            save("deprecated");
            
            save(Configuration.lafTheme.name());
            save(Configuration.rstaTheme.name());
            save(BytecodeViewer.viewer.simplifyNameInTabTitle.isSelected());
            save(Configuration.language.name());
            
            save(BytecodeViewer.viewer.viewPane1.isPaneEditable());
            save(BytecodeViewer.viewer.viewPane2.isPaneEditable());
            save(BytecodeViewer.viewer.viewPane3.isPaneEditable());
            
            save(Configuration.javaTools);
            save("deprecated");
            save("deprecated");
            save(Configuration.lastSaveDirectory);
            save(Configuration.lastPluginDirectory);
            save(Configuration.python2Extra);
            save(Configuration.python3Extra);
            save(BytecodeViewer.viewer.getMinSdkVersion());
            save(BytecodeViewer.viewer.printLineNumbers.isSelected());
        } catch (Exception e) {
            BytecodeViewer.handleException(e);
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
        catch (IndexOutOfBoundsException e)
        {
            //ignore because errors are expected, first start up and outdated settings.
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
            BytecodeViewer.viewer.viewPane1.setSelectedDecompiler(Decompiler.values()[asInt(81)]);
            BytecodeViewer.viewer.viewPane2.setSelectedDecompiler(Decompiler.values()[asInt(82)]);
            BytecodeViewer.viewer.viewPane3.setSelectedDecompiler(Decompiler.values()[asInt(83)]);

            BytecodeViewer.viewer.refreshOnChange.setSelected(asBoolean(84));

            boolean bool = Boolean.parseBoolean(asString(85));
            if (bool) {
                BytecodeViewer.viewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
                BytecodeViewer.viewer.isMaximized = true;
            }
            //86 is deprecated
            //87 is deprecated
            Configuration.lastOpenDirectory = asString(88);
            Configuration.python2 = asString(89);
            Configuration.rt = asString(90);
            
            BytecodeViewer.viewer.decodeAPKResources.setSelected(asBoolean(106));
            Configuration.library = asString(107);
            Configuration.pingback = asBoolean(108);
            
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
            //line 126 is deprecated
            //line 127 is used for theme on preload
            //line 128 is used for theme on preload
            BytecodeViewer.viewer.simplifyNameInTabTitle.setSelected(asBoolean(129));
            Configuration.simplifiedTabNames = BytecodeViewer.viewer.simplifyNameInTabTitle.isSelected();
            
            //line 130 is used for preload
            if(Configuration.language != Language.ENGLISH)
                Configuration.language.setLanguageTranslations(); //load language translations
            Settings.hasSetLanguageAsSystemLanguage = true;
            
            BytecodeViewer.viewer.viewPane1.setPaneEditable(asBoolean(131));
            BytecodeViewer.viewer.viewPane2.setPaneEditable(asBoolean(132));
            BytecodeViewer.viewer.viewPane3.setPaneEditable(asBoolean(133));
            
            Configuration.javaTools = asString(134);
            //ignore 135
            //ignore 136
            Configuration.lastSaveDirectory = asString(137);
            Configuration.lastPluginDirectory = asString(138);
            Configuration.python2Extra = asBoolean(139);
            Configuration.python3Extra = asBoolean(140);
            BytecodeViewer.viewer.minSdkVersionSpinner.setValue(asInt(141));
            BytecodeViewer.viewer.printLineNumbers.setSelected(asBoolean(142));
        }
        catch (IndexOutOfBoundsException e)
        {
            //ignore because errors are expected, first start up and outdated settings.
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void save(Object o)
    {
        DiskWriter.writeNewLine(settingsName, String.valueOf(o), false);
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
