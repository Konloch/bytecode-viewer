package the.bytecode.club.bootloader.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author Bibl (don't ban me pls)
 * @created 19 Jul 2015 02:33:23
 */
public class ExternalLibrary extends ExternalResource<JarContents<ClassNode>> {

	/**
	 * @param location
	 */
	public ExternalLibrary(URL location) {
		super(location);
	}

	/**
	 * @param jar
	 */
	public ExternalLibrary(JarInfo jar) {
		super(createJarURL(jar));
	}
	
	public static URL createJarURL(JarInfo jar) {
		try {
			return jar.formattedURL();
		} catch(MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] read(InputStream in) throws IOException {
		ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1)
			byteArrayOut.write(buffer, 0, bytesRead);
		byteArrayOut.close();
		return byteArrayOut.toByteArray();
	}
	
	protected ClassNode create(byte[] b) {
		ClassReader cr = new ClassReader(b);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		return cn;
	}

	/* (non-Javadoc)
	 * @see the.bytecode.club.bytecodeviewer.loadermodel.ExternalResource#load()
	 */
	@Override
	public JarContents<ClassNode> load() throws IOException {
		JarContents<ClassNode> contents = new JarContents<ClassNode>();
		
		JarURLConnection con = (JarURLConnection) getLocation().openConnection();
		JarFile jar = con.getJarFile();

		Enumeration<JarEntry> entries = jar.entries();
		while(entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			byte[] bytes = read(jar.getInputStream(entry));
			if (entry.getName().endsWith(".class")) {
				ClassNode cn = create(bytes);
				contents.getClassContents().add(cn);
			} else {
				JarResource resource = new JarResource(entry.getName(), bytes);
				contents.getResourceContents().add(resource);
			}
		}
		
		return contents;
	}
}