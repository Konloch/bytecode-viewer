package the.bytecode.club.bytecodeviewer.resources.importing;

import java.io.File;

/**
 * @author Konloch
 * @since 6/26/2021
 */
public interface Importer
{
	boolean open(File file) throws Exception;
}
