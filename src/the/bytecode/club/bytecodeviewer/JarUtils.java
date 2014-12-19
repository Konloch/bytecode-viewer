package the.bytecode.club.bytecodeviewer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

/**
 * Loading and saving jars
 * 
 * @author Konloch
 * @author WaterWolf
 * 
 */

public class JarUtils {

	private static JarInputStream jis;
	private static JarEntry entry;

	public static void put(final File jarFile,
			final HashMap<String, ClassNode> clazzList) throws IOException {
		jis = new JarInputStream(new FileInputStream(jarFile));
		while ((entry = jis.getNextJarEntry()) != null) {
			final String name = entry.getName();
			if (!name.endsWith(".class")) {
				BytecodeViewer.loadedResources.put(name, getBytes(jis));
				jis.closeEntry();
				continue;
			}

			final ClassNode cn = getNode(getBytes(jis));
			clazzList.put(cn.name, cn);

			jis.closeEntry();
		}
		jis.close();

	}

	private static ByteArrayOutputStream baos = null;
	private static byte[] buffer = null;
	private static int a = 0;

	public static byte[] getBytes(final InputStream is) throws IOException {
		baos = new ByteArrayOutputStream();
		buffer = new byte[1024];
		a = 0;
		while ((a = is.read(buffer)) != -1) {
			baos.write(buffer, 0, a);
		}
		baos.close();
		buffer = null;
		return baos.toByteArray();
	}

	private static ClassReader cr = null;
	private static ClassNode cn = null;

	public static ClassNode getNode(final byte[] bytez) {
		cr = new ClassReader(bytez);
		cn = new ClassNode();
		try {
			cr.accept(cn, ClassReader.EXPAND_FRAMES);
		} catch (Exception e) {
			cr.accept(cn, ClassReader.SKIP_FRAMES);
		}
		cr = null;
		return cn;
	}

	public static void saveAsJar(ArrayList<ClassNode> nodeList, String path,
			String manifest) {
		try {
			JarOutputStream out = new JarOutputStream(
					new FileOutputStream(path));
			for (ClassNode cn : nodeList) {
				ClassWriter cw = new ClassWriter(0);
				cn.accept(cw);

				out.putNextEntry(new ZipEntry(cn.name + ".class"));
				out.write(cw.toByteArray());
				out.closeEntry();
			}

			out.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
			out.write((manifest.trim() + "\r\n\r\n").getBytes());
			out.closeEntry();

			for (Entry<String, byte[]> entry : BytecodeViewer.loadedResources
					.entrySet()) {
				String filename = entry.getKey();
				if (!filename.startsWith("META-INF")) {
					out.putNextEntry(new ZipEntry(filename));
					out.write(entry.getValue());
					out.closeEntry();
				}
			}

			out.close();
		} catch (IOException e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
	}

	public static void saveAsJar(ArrayList<ClassNode> nodeList, String path) {
		try {
			JarOutputStream out = new JarOutputStream(
					new FileOutputStream(path));
			for (ClassNode cn : nodeList) {
				ClassWriter cw = new ClassWriter(0);
				cn.accept(cw);

				out.putNextEntry(new ZipEntry(cn.name + ".class"));
				out.write(cw.toByteArray());
				out.closeEntry();
			}

			for (Entry<String, byte[]> entry : BytecodeViewer.loadedResources
					.entrySet()) {
				String filename = entry.getKey();
				if (!filename.startsWith("META-INF")) {
					out.putNextEntry(new ZipEntry(filename));
					out.write(entry.getValue());
					out.closeEntry();
				}
			}

			out.close();
		} catch (IOException e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
	}

}
