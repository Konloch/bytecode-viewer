package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.util.JarUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Konloch
 * @since 6/26/2021
 */
public class ZipResourceImporter implements Importer
{
	@Override
	public boolean open(File file) throws Exception
	{
		//attempt to load archives using the first method
		try
		{
			JarUtils.importArchiveA(file);
		}
		catch (IOException z)
		{
			//attempt to load archives using the fallback method on fail
			try
			{
				JarUtils.importArchiveB(file);
			}
			catch (final Exception e)
			{
				new ExceptionUI(e);
				return false;
			}
		}
		catch (final Exception e)
		{
			new ExceptionUI(e);
			return false;
		}
		
		return true;
	}
}
