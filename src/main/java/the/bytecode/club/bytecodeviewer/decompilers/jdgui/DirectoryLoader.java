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

package the.bytecode.club.bytecodeviewer.decompilers.jdgui;

import org.apache.commons.io.IOUtils;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DirectoryLoader implements Loader
{
    protected String codebase;
    protected long lastModified;
    protected boolean isFile;

    public DirectoryLoader(File file) throws LoaderException
    {
        this.codebase = file.getAbsolutePath();
        this.lastModified = file.lastModified();
        this.isFile = file.isFile();

        if (!(file.exists() && file.isDirectory()))
            throw new LoaderException("'" + codebase + "' is not a directory");
    }

    @Override
    public byte[] load(String internalPath) throws LoaderException
    {
        File file = new File(this.codebase, internalPath);

        try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis))
        {
            return IOUtils.toByteArray(bis);
        }
        catch (IOException e)
        {
            throw new LoaderException("'" + file.getAbsolutePath() + "'  not found.");
        }
    }

    @Override
    public boolean canLoad(String internalPath)
    {
        File file = new File(this.codebase, internalPath);
        return file.exists() && file.isFile();
    }
}
