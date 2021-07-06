package the.bytecode.club.bytecodeviewer.searching;

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
 * A simple class to make searching run in a background thread.
 *
 * @author Konloch
 */

public abstract class BackgroundSearchThread extends Thread
{
    public BackgroundSearchThread() { }

    public BackgroundSearchThread(boolean finished) {
        this.finished = finished;
    }

    public boolean finished = false;

    public abstract void search();

    @Override
    public void run()
    {
        BytecodeViewer.updateBusyStatus(true);
        search();
        finished = true;
        BytecodeViewer.updateBusyStatus(false);
    }
}
