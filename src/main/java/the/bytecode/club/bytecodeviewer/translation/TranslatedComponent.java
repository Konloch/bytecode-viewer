package the.bytecode.club.bytecodeviewer.translation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Konloch
 * @since 6/28/2021
 */
public class TranslatedComponent
{
	public String key;
	public String value;
	public List<Runnable> runOnUpdate = new ArrayList<>();
}
