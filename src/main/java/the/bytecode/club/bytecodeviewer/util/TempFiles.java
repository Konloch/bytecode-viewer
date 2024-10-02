package the.bytecode.club.bytecodeviewer.util;

import java.io.File;

import static the.bytecode.club.bytecodeviewer.Constants.TEMP_DIRECTORY;

/**
 * @author Konloch
 * @since 10/2/2024
 */
public class TempFiles
{
    public static File createTemporaryFile(boolean newDirecory, String extension)
    {
        //genereate a new temporary parent directory
        File parent = newDirecory ? createTemporaryDirectory() : new File(TEMP_DIRECTORY);

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

    public static File createTemporaryDirectory()
    {
        File directory;

        //generate a new name until the directory no longer exists
        while((directory = new File(TEMP_DIRECTORY, MiscUtils.randomString(32))).exists())
        {
        }

        return directory;
    }
}
