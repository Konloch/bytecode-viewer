package the.bytecode.club.bootloader;

import java.net.URL;

import org.objectweb.asm.tree.ClassNode;

/**
 * @author Bibl (don't ban me pls)
 * @created ages ago
 */
public class LocateableJarContents<C extends ClassNode> extends JarContents<C> {

	private final URL[] jarUrls;

	public LocateableJarContents(URL... jarUrls) {
		super();
		this.jarUrls = jarUrls;
	}

	public LocateableJarContents(DataContainer<C> classContents, DataContainer<JarResource> resourceContents, URL... jarUrls) {
		super(classContents, resourceContents);
		this.jarUrls = jarUrls;
	}

	public URL[] getJarUrls() {
		return jarUrls;
	}
}