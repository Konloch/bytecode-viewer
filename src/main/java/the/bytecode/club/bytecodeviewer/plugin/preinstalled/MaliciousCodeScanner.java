package the.bytecode.club.bytecodeviewer.plugin.preinstalled;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.api.PluginConsole;
import the.bytecode.club.bytecodeviewer.malwarescanner.MalwareScan;
import the.bytecode.club.bytecodeviewer.malwarescanner.MalwareScanModule;
import the.bytecode.club.bytecodeviewer.malwarescanner.util.MaliciousCodeOptions;

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
 * The Malicious Code Scanner plugin. All of the core components have been moved to the malwarescanner package.
 *
 * This tool is used to help aid reverse engineers in identifying malicious code.
 *
 * @author Konloch
 * @author WaterWolf
 * @since 10/02/2011
 */

public class MaliciousCodeScanner extends Plugin
{
    public final List<MaliciousCodeOptions> options;

    public MaliciousCodeScanner(List<MaliciousCodeOptions> options)
    {
        this.options = options;
    }

    @Override
    public void execute(List<ClassNode> classNodeList)
    {
        PluginConsole frame = new PluginConsole("Malicious Code Scanner");
        StringBuilder sb = new StringBuilder();
        
        Set<String> scanOptions = new HashSet<>();
        
        for(MaliciousCodeOptions option : options)
            if(option.getCheckBox().isSelected())
                scanOptions.add(option.getModule().name());
        
        //create a new code scan object with all of the scan options
        MalwareScan scan = new MalwareScan(classNodeList, sb, scanOptions);
        
        //scan the modules one by one
        MalwareScanModule.performScan(scan);

        frame.appendText(sb.toString());
        frame.setVisible(true);
    }
}
