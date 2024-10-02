package the.bytecode.club.bytecodeviewer.util;

import java.io.File;
import java.util.HashSet;

import static the.bytecode.club.bytecodeviewer.Constants.TEMP_DIRECTORY;

/**
 * @author Konloch
 * @since 10/2/2024
 */
public class TempFile
{
    private File parent;
    private final File file;
    private final String uniqueName;
    private final HashSet<String> createdFilePaths = new HashSet<>();

    public TempFile(File file, String uniqueName)
    {
        this.parent = file.getParentFile();
        this.file = file;
        this.uniqueName = uniqueName;
        this.createdFilePaths.add(file.getAbsolutePath());
    }

    public File getParent()
    {
        return parent;
    }

    public File getFile()
    {
        return file;
    }

    public String getUniqueName()
    {
        return uniqueName;
    }

    public void setParent(File parent)
    {
        this.parent = parent;
    }

    public void markAsCreatedFile(File file)
    {
        createdFilePaths.add(file.getAbsolutePath());
    }

    public void delete()
    {
        //delete all the items
        for(String path : createdFilePaths)
        {
            File toDelete = new File(path);

            toDelete.delete();

            if(!toDelete.getParentFile().getAbsolutePath().equalsIgnoreCase(new File(TEMP_DIRECTORY).getAbsolutePath()))
            {
                toDelete.getParentFile().delete();
            }
        }

        //delete parent if it's not the main temp directory
        if(!getParent().getAbsolutePath().equalsIgnoreCase(new File(TEMP_DIRECTORY).getAbsolutePath()))
        {
            getParent().delete();
        }
    }

    public File createFileFromExtension(String extension)
    {
        return createFileFromExtension(true, false, extension);
    }

    public File createFileFromExtension(boolean newUniqueName, boolean canExist, String extension)
    {
        File file;

        String uniqueName = newUniqueName ? MiscUtils.getUniqueName("", extension) : this.uniqueName + extension;
        //String uniqueName = this.uniqueName + extension;

        //generate a new name until the directory no longer exists
        while((file = new File(parent, uniqueName)).exists())
        {
            if(canExist)
                break;
        }

        this.createdFilePaths.add(file.getAbsolutePath());

        return file;
    }

    public static TempFile createTemporaryFile(boolean newDirectory, String extension)
    {
        //generate a new temporary parent directory
        File parent = newDirectory ? createTempDirectory() : new File(TEMP_DIRECTORY);

        //make the parent directories
        parent.mkdirs();

        //create the temporary variables
        String uniqueName;
        File file = null;

        //generate a new name until the directory no longer exists
        while((uniqueName = MiscUtils.getUniqueName("", extension)) != null &&
                (file = new File(parent, uniqueName)).exists())
        {
        }

        if(uniqueName != null)
            uniqueName = uniqueName.substring(0, uniqueName.length() - extension.length());

        return new TempFile(file, uniqueName);
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
