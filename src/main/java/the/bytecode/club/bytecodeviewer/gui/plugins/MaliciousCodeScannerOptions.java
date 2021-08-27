package the.bytecode.club.bytecodeviewer.gui.plugins;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.malwarescanner.MalwareScanModule;
import the.bytecode.club.bytecodeviewer.malwarescanner.util.MaliciousCodeOptions;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.plugin.preinstalled.MaliciousCodeScanner;
import the.bytecode.club.bytecodeviewer.resources.IconResources;

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

public class MaliciousCodeScannerOptions extends JFrame
{
    private static final int SPACER_HEIGHT_BETWEEN_OPTIONS = 26;
    
    public static void open()
    {
        if (BytecodeViewer.promptIfNoLoadedClasses())
            return;
        
        new MaliciousCodeScannerOptions().setVisible(true);
    }
    
    public MaliciousCodeScannerOptions()
    {
        this.setIconImages(IconResources.iconList);
        setSize(new Dimension(250, 7 + (MalwareScanModule.values().length * SPACER_HEIGHT_BETWEEN_OPTIONS) + 90));
        setResizable(false);
        setTitle("Malicious Code Scanner Options");
        getContentPane().setLayout(null);
        List<MaliciousCodeOptions> checkBoxes = new ArrayList<>();
        
        int y = 7;
        for(MalwareScanModule module : MalwareScanModule.values())
        {
            final JCheckBox checkBox = new JCheckBox(module.getOptionText());
            checkBox.setSelected(module.isToggledByDefault());
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
}
