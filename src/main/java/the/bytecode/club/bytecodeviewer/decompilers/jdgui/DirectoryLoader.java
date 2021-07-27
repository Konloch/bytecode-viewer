package the.bytecode.club.bytecodeviewer.decompilers.jdgui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;

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
    public byte[] load(String internalPath)
            throws LoaderException {
        File file = new File(this.codebase, internalPath);

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            return IOUtils.toByteArray(bis);
        } catch (IOException e) {
            throw new LoaderException(
                    "'" + file.getAbsolutePath() + "'  not found.");
        }
    }

    @Override
    public boolean canLoad(String internalPath) {
        File file = new File(this.codebase, internalPath);
        return file.exists() && file.isFile();
    }
}
