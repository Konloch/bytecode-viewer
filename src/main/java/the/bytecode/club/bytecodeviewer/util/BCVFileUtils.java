package the.bytecode.club.bytecodeviewer.util;

import java.io.File;

/**
 * @author Konloch
 * @since 7/4/2021
 */
public class BCVFileUtils
{
	/**
	 * Searches a directory until the extension is found
	 */
	public static File findFile(File basePath, String extension)
	{
		for(File f : basePath.listFiles())
		{
			if(f.isDirectory())
			{
				File child = findFile(f, extension);
				
				if(child != null)
					return child;
				
				continue;
			}
			
			if(f.getName().endsWith(extension))
			{
				return f;
			}
		}
		
		return null;
	}
}
