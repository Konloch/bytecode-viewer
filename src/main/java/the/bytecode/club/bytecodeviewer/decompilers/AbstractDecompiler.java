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

package the.bytecode.club.bytecodeviewer.decompilers;

import org.objectweb.asm.tree.ClassNode;

/**
 * Used to represent a decompiler/disassembler
 *
 * @author Konloch
 */
public abstract class AbstractDecompiler
{
    private final String decompilerName;
    private final String decompilerNameProgrammatic;

    protected AbstractDecompiler(String decompilerName, String decompilerNameProgrammatic)
    {
        this.decompilerName = decompilerName;
        this.decompilerNameProgrammatic = decompilerNameProgrammatic;
    }

    public abstract String decompileClassNode(ClassNode cn, byte[] bytes);

    public abstract void decompileToZip(String sourceJar, String zipName);

    public void decompileToZipFallBack(String sourceJar, String zipName)
    {
        //TODO
    }

    public String getDecompilerName()
    {
        return decompilerName;
    }

    /**
     * Used for the compressed exports (Zip / Jar)
     */
    public String getDecompilerNameProgrammatic()
    {
        return decompilerNameProgrammatic;
    }
}
