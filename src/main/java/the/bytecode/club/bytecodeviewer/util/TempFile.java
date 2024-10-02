package the.bytecode.club.bytecodeviewer.util;

import java.io.File;

import static the.bytecode.club.bytecodeviewer.Constants.TEMP_DIRECTORY;

/**
 * @author Konloch
 * @since 10/2/2024
 */
public class TempFile
{
    private final File parent;
    private final File file;
    private final String filePath;

    public TempFile(File file)
    {
        this.parent = file.getParentFile();
        this.file = file;
        this.filePath = file.getAbsolutePath();
    }

    public File getFile()
    {
        return file;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public File createFileFromExtension(String extension)
    {
        File file;

        //generate a new name until the directory no longer exists
        while((file = new File(parent, MiscUtils.getUniqueName("", extension))).exists())
        {
        }

        return file;
    }

    public static TempFile createTemporaryFile(boolean newDirectory, String extension)
    {
        //generate a new temporary parent directory
        File parent = newDirectory ? createTempDirectory() : new File(TEMP_DIRECTORY);

        return new TempFile(createTempFile(parent, extension));
    }

    private static File createTempFile(File parent, String extension)
    {
        //make the parent directories
        parent.mkdirs();

        //return the temporary file
        File file;

        //generate a new name until the directory no longer exists
        while((file = new File(parent, MiscUtils.getUniqueName("", extension))).exists())
        {
        }

        return file;
    }

    private static File createTempDirectory()
    {
        File directory;

        //generate a new name until the directory no longer exists
        while((directory = new File(TEMP_DIRECTORY, MiscUtils.randomString(32))).exists())
        {
        }

        return directory;
    }
}
