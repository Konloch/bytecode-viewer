package the.bytecode.club.bytecodeviewer.util.resources.impl;

import the.bytecode.club.bytecodeviewer.api.ExceptionUI;
import the.bytecode.club.bytecodeviewer.util.JarUtils;
import the.bytecode.club.bytecodeviewer.util.resources.Importer;

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
		try {
			JarUtils.put(file);
		} catch (IOException z) {
			try {
				JarUtils.put2(file);
			} catch (final Exception e) {
				new ExceptionUI(e);
				return false;
			}
		} catch (final Exception e) {
			new ExceptionUI(e);
			return false;
		}
		
		return true;
	}
}
