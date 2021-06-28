package the.bytecode.club.bytecodeviewer.gui.plugins;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;
import the.bytecode.club.bytecodeviewer.malwarescanner.MalwareScanModule;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.MaliciousCodeScanner;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

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
 * This GUI automatically populates the scan options from the MalwareScanModule enum.
 *
 * @author Konloch
 */

public class MaliciousCodeScannerOptionsV2 extends JFrame
{
    private static final int SPACER_HEIGHT_BETWEEN_OPTIONS = 26;
    
    public static void open()
    {
        if (BytecodeViewer.getLoadedClasses().isEmpty())
        {
            BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
            return;
        }
        
        new MaliciousCodeScannerOptionsV2().setVisible(true);
    }
    
    public MaliciousCodeScannerOptionsV2()
    {
        this.setIconImages(Resources.iconList);
        setSize(new Dimension(250, 7+(MalwareScanModule.values().length * SPACER_HEIGHT_BETWEEN_OPTIONS)+90));
        setResizable(false);
        setTitle("Malicious Code Scanner Options");
        getContentPane().setLayout(null);
        ArrayList<MaliciousCodeOptions> checkBoxes = new ArrayList<>();
        
        int y = 7;
        for(MalwareScanModule module : MalwareScanModule.values())
        {
            final JCheckBox checkBox = new JCheckBox(module.getReadableName());
            checkBox.setSelected(module.isToggledByDefault()); //TODO
            checkBox.setBounds(6, y, 232, 23);
            getContentPane().add(checkBox);
            checkBoxes.add(new MaliciousCodeOptions(module, checkBox));
            
            y += SPACER_HEIGHT_BETWEEN_OPTIONS;
        }
    
        JButton btnNewButton = new JButton("Start Scanning");
        btnNewButton.addActionListener(arg0 -> {
            PluginManager.runPlugin(new MaliciousCodeScanner(checkBoxes));
            dispose();
        });

        btnNewButton.setBounds(6, y, 232, 23);
        getContentPane().add(btnNewButton);
        this.setLocationRelativeTo(null);
    }

    private static final long serialVersionUID = -2662514582647810868L;
    
    public static class MaliciousCodeOptions
    {
        private final MalwareScanModule module;
        private final JCheckBox checkBox;
    
        public MaliciousCodeOptions(MalwareScanModule module, JCheckBox checkBox) {
            this.module = module;
            this.checkBox = checkBox;
        }
    
        public JCheckBox getCheckBox()
        {
            return checkBox;
        }
    
        public MalwareScanModule getModule()
        {
            return module;
        }
    }
}