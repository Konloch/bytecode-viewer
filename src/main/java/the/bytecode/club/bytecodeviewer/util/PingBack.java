package the.bytecode.club.bytecodeviewer.util;

import me.konloch.kontainer.io.HTTPRequest;
import the.bytecode.club.bytecodeviewer.Configuration;

import java.net.URL;

/**
 * Pings back to bytecodeviewer.com to be added into the total running statistics
 *
 * @author Konloch
 * @since May 1, 2015
 */
public class PingBack implements Runnable
{
	@Override
	public void run()
	{
		try {
			new HTTPRequest(new URL("https://bytecodeviewer.com/add.php")).read();
		} catch (Exception e) {
			Configuration.pingback = false;
		}
	}
}
