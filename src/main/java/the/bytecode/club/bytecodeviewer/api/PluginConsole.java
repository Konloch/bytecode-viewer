package the.bytecode.club.bytecodeviewer.api;

import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.SystemConsole;
import the.bytecode.club.bytecodeviewer.plugin.PluginManager;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

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
 * A simple console GUI.
 *
 * @author Konloch
 */

public class PluginConsole extends SystemConsole
{
    //window showing is disabled to allow this frame to be added as a tab
    private boolean showWindow;
    private boolean added;
    
    public PluginConsole(String pluginName)
    {
        super(Configuration.pluginConsoleAsNewTab ? (pluginName + " Output")
                : (TranslatedStrings.PLUGIN_CONSOLE_TITLE + " - " + pluginName));
    }
    
    @Override
    public void setVisible(boolean visible)
    {
        if(!added && visible)
        {
            added = true;
            PluginManager.addConsole(this);
        }
        
        //do nothing
        if(!showWindow)
            return;
        
        super.setVisible(visible);
    }
    
    private static final long serialVersionUID = -6556940545421437508L;
}
