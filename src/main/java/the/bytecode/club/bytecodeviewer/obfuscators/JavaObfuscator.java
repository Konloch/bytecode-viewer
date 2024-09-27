/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
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

package the.bytecode.club.bytecodeviewer.obfuscators;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * An unfinished obfuscator.
 *
 * @author Konloch
 */

public abstract class JavaObfuscator extends Thread
{

    public static int MAX_STRING_LENGTH = 25;
    public static int MIN_STRING_LENGTH = 5;
    private final List<String> names = new ArrayList<>();

    @Override
    public void run()
    {
        BytecodeViewer.updateBusyStatus(true);
        Configuration.runningObfuscation = true;

        obfuscate();

        BytecodeViewer.refactorer.run();
        Configuration.runningObfuscation = false;
        BytecodeViewer.updateBusyStatus(false);
    }

    public int getStringLength()
    {
        if (BytecodeViewer.viewer.obfuscatorGroup.isSelected(BytecodeViewer.viewer.strongObf.getModel()))
            return MAX_STRING_LENGTH;
        else // if(BytecodeViewer.viewer.obfuscatorGroup.isSelected(BytecodeViewer.viewer.lightObf.getModel()))
            return MIN_STRING_LENGTH;
    }

    protected String generateUniqueName(int length)
    {
        boolean found = false;
        String name = "";

        while (!found)
        {
            String nameTry = MiscUtils.randomString(1) + MiscUtils.randomStringNum(length - 1);
            if (!Character.isJavaIdentifierStart(nameTry.toCharArray()[0]))
                continue;

            if (!names.contains(nameTry))
            {
                names.add(nameTry);
                name = nameTry;
                found = true;
            }
        }

        return name;
    }

    public abstract void obfuscate();
}
