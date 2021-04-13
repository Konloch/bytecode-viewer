package jd.cli.loader;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.jd.core.v1.api.loader.LoaderException;


public class JarLoader extends BaseLoader {
    private final ZipFile zipFile;

    public JarLoader(File file) throws LoaderException {
        super(file);

        if (!(file.exists() && file.isFile())) {
            throw new LoaderException("'" + codebase + "' is not a directory");
        }

        try {
            this.zipFile = new ZipFile(codebase);
        } catch (IOException e) {
            throw new LoaderException("Error reading from '" + codebase + "'");
        }
    }

    @Override
    public byte[] load(String internalPath)
            throws LoaderException {
        ZipEntry zipEntry = this.zipFile.getEntry(internalPath);

        if (zipEntry == null) {
            throw new LoaderException("Can not read '" + internalPath + "'");
        }

        try {
            return IOUtils.toByteArray(this.zipFile.getInputStream(zipEntry));
        } catch (IOException e) {
            throw new LoaderException("Error reading '" + internalPath + "'");
        }
    }

    @Override
    public boolean canLoad(String internalPath) {
        return this.zipFile.getEntry(internalPath) != null;
    }
}
