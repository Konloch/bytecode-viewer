package the.bytecode.club.bytecodeviewer.util;

/**
 * @author Konloch
 * @since 8/21/2024
 */
public class SleepUtil
{
	public static void sleep(long ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
