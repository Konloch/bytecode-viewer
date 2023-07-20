package the.bytecode.club.bytecodeviewer.util;

import com.googlecode.d2j.dex.Dex2jar;
import com.googlecode.d2j.dex.DexExceptionHandler;
import com.googlecode.d2j.Method;
import com.googlecode.d2j.node.DexMethodNode;
import org.objectweb.asm.MethodVisitor;
import java.io.File;
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
 * A simple wrapper for Dex2Jar.
 *
 * @author Konloch
 */

public class Dex2Jar {

    /**
     * Converts a .apk or .dex to .jar
     *
     * @param input  the input .apk or .dex file
     * @param output the output .jar file
     */
    public static synchronized void dex2Jar(File input, File output) {
        try {
            Dex2jar d2Jar = Dex2jar.from(input)
                                   .withExceptionHandler(new DexExceptionHandler() {
                                       public void handleFileException(Exception e) {
                                           e.printStackTrace();
                                       }
                                       
                                       public void handleMethodTranslateException(Method method, DexMethodNode methodNode, MethodVisitor mv, Exception e) {
                                           e.printStackTrace();
                                       }
                                   });
            d2Jar.to(output.toPath());
        } catch (com.googlecode.d2j.DexException e) {
            e.printStackTrace();
        } catch (Exception e) {
            BytecodeViewer.handleException(e);
        }
    }

    /**
     * Converts a .jar to .dex
     *
     * @param input  the input .jar file
     * @param output the output .dex file
     */
    public static synchronized void saveAsDex(File input, File output) {
        saveAsDex(input, output, true);
    }

    public static synchronized void saveAsDex(File input, File output, boolean delete) {
        try {
            com.googlecode.dex2jar.tools.Jar2Dex.main(input.getAbsolutePath(),
                    "-o", output.getAbsolutePath(),
                    "-s", BytecodeViewer.viewer.getMinSdkVersion() + "");
            if (delete)
                input.delete();
        } catch (Exception e) {
            BytecodeViewer.handleException(e);
        }
    }
}
