package the.bytecode.club.bytecodeviewer.api;

import java.util.ArrayList;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;

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

public abstract class Plugin extends Thread {

    @Override
    public void run() {
        BytecodeViewer.viewer.updateBusyStatus(true);
        try {
            if (BytecodeViewer.getLoadedClasses().isEmpty()) {
                BytecodeViewer.showMessage("First open a class, jar, zip, apk or dex file.");
                return;
            }
            execute(BytecodeViewer.getLoadedClasses());
        } catch (Exception e) {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        } finally {
            finished = true;
            BytecodeViewer.viewer.updateBusyStatus(false);
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
     * Whenever the plugin is started, this method is called
     *
     * @param classNodeList all of the loaded classes for easy access.
     */
    public abstract void execute(ArrayList<ClassNode> classNodeList);
}
