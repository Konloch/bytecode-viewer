package jd.cli.loader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.jd.core.v1.api.loader.LoaderException;


public class DirectoryLoader extends BaseLoader {
    public DirectoryLoader(File file) throws LoaderException {
        super(file);

        if (!(file.exists() && file.isDirectory()))
            throw new LoaderException("'" + codebase + "' is not a directory");
    }

    @Override
    public byte[] load(String internalPath)
            throws LoaderException {
        File file = new File(this.codebase, internalPath);

        try {
            return IOUtils.toByteArray(
                    new BufferedInputStream(new FileInputStream(file)));
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
