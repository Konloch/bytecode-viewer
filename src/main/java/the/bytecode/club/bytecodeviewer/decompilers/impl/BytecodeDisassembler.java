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

package the.bytecode.club.bytecodeviewer.decompilers.impl;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.Constants;
import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.decompilers.AbstractDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.bytecode.ClassNodeDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.bytecode.PrefixedStringBuilder;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;
import the.bytecode.club.bytecodeviewer.util.ExceptionUtils;

import java.util.ArrayList;

import static the.bytecode.club.bytecodeviewer.Constants.NL;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.DEV_MODE_SIMULATED_ERROR;
import static the.bytecode.club.bytecodeviewer.translation.TranslatedStrings.ERROR;

/**
 * @author Konloch
 * @since 7/3/2021
 */
public class BytecodeDisassembler extends AbstractDecompiler
{
    public BytecodeDisassembler()
    {
        super("Bytecode Disassembler", "bcvbd");
    }

    @Override
    public String decompileClassNode(ClassNode cn, byte[] bytes)
    {
        String exception;

        try
        {
            //handle simulated errors
            if(Constants.DEV_FLAG_DECOMPILERS_SIMULATED_ERRORS)
                throw new RuntimeException(DEV_MODE_SIMULATED_ERROR.toString());

            //parse class-file
            return ClassNodeDecompiler.decompile(new PrefixedStringBuilder(), new ArrayList<>(), cn).toString();
        }
        catch (Throwable e)
        {
            exception = ExceptionUtils.exceptionToString(e);
        }

        return getDecompilerName() + " " + ERROR + "! " + ExceptionUI.SEND_STACKTRACE_TO + NL + NL
            + TranslatedStrings.SUGGESTED_FIX_DECOMPILER_ERROR + NL + NL + exception;
    }

    @Override
    public void decompileToZip(String sourceJar, String zipName)
    {
        decompileToZipFallBack(sourceJar, zipName);
    }
}
