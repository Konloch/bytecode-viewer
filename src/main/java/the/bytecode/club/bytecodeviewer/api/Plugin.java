package the.bytecode.club.bytecodeviewer.api;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;

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
 * A simple plugin class, it will run the plugin in a background thread.
 *
 * @author Konloch
 */

public abstract class Plugin extends Thread
{
    //as long as your code is being called from the execute function
    // this will be the current container
    public ResourceContainer activeContainer = null;
    
    @Override
    public void run()
    {
        BytecodeViewer.updateBusyStatus(true);
        
        try
        {
            if (BytecodeViewer.promptIfNoLoadedResources())
                return;
    
            executeContainer();
        } catch (Exception e) {
            BytecodeViewer.handleException(e);
        } finally {
            finished = true;
            BytecodeViewer.updateBusyStatus(false);
        }
    }

    private boolean finished = false;

    /**
     * When the plugin is finally finished, this will return true
     *
     * @return true if the plugin is finished executing
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * If for some reason your plugin needs to keep the thread alive, yet will
     * still be considered finished (EZ-Injection), you can call this function
     * and it will set the finished boolean to true.
     */
    public void setFinished() {
        finished = true;
    }
    
    /**
     * On plugin start each resource container is iterated through
     */
    public void executeContainer()
    {
        BytecodeViewer.getResourceContainers().forEach(container -> {
            //set the active container
            activeContainer = container;
            
            //call on the plugin code
            execute(new ArrayList<>(container.resourceClasses.values()));
        });
    }
    
    /**
     * On plugin start each resource container is iterated through,
     * then this is called with the resource container classes
     *
     * @param classNodeList all the loaded classes for easy access.
     */
    public abstract void execute(List<ClassNode> classNodeList);
}
