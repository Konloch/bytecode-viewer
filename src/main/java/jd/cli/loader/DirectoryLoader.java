package jd.cli.loader;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import jd.core.loader.LoaderException;


public class DirectoryLoader extends BaseLoader {
    public DirectoryLoader(File file) throws LoaderException {
        super(file);

        if (!(file.exists() && file.isDirectory()))
            throw new LoaderException("'" + codebase + "' is not a directory");
    }

    public DataInputStream load(String internalPath)
            throws LoaderException {
        File file = new File(this.codebase, internalPath);

        try {
            return new DataInputStream(
                    new BufferedInputStream(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            throw new LoaderException(
                    "'" + file.getAbsolutePath() + "'  not found.");
        }
    }

    public boolean canLoad(String internalPath) {
        File file = new File(this.codebase, internalPath);
        return file.exists() && file.isFile();
    }
}
