package the.bytecode.club.bytecodeviewer.util;

import the.bytecode.club.bootloader.Boot;

import static the.bytecode.club.bytecodeviewer.Constants.OFFLINE_MODE;

/**
 * Downloads & installs the krakatau & enjarify zips
 *
 * Alternatively if OFFLINE_MODE is enabled it will drop the Krakatau and Enjarify versions supplied with BCV
 *
 * @author Konloch
 * @since 7/6/2021
 */
public class InstallFatJar implements Runnable
{
	@Override
	public void run()
	{
		try
		{
			if (OFFLINE_MODE)
			{
				Boot.dropKrakatau();
				Boot.dropEnjarify();
			}
			else
			{
				Boot.populateUrlList();
				Boot.populateLibsDirectory();
				Boot.downloadZipsOnly();
				Boot.checkKrakatau();
				Boot.checkEnjarify();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}