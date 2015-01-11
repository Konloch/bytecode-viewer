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
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

	private static ZipInputStream jis;
	private static ZipEntry entry;

	public static void put(final File jarFile,
			final HashMap<String, ClassNode> clazzList) throws IOException {
		jis = new ZipInputStream(new FileInputStream(jarFile));
		while ((entry = jis.getNextEntry()) != null) {
			try {
				final String name = entry.getName();
				if (!name.endsWith(".class")) {
					BytecodeViewer.loadedResources.put(name, getBytes(jis));
					jis.closeEntry();
					continue;
				}
	
				byte[] bytes = getBytes(jis);
				String cafebabe = String.format("%02X", bytes[0])
						+ String.format("%02X", bytes[1])
						+ String.format("%02X", bytes[2])
						+ String.format("%02X", bytes[3]);
				if(cafebabe.toLowerCase().equals("cafebabe")) {
					final ClassNode cn = getNode(bytes);
					clazzList.put(cn.name, cn);
				} else {
					System.out.println(jarFile+">"+name+": Header does not start with CAFEBABE, ignoring.");
				}

			} catch(Exception e) {
				new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
			} finally {
				jis.closeEntry();
			}
		}
		jis.close();

	}

	public static byte[] getBytes(final InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int a = 0;
		while ((a = is.read(buffer)) != -1) {
			baos.write(buffer, 0, a);
		}
		baos.close();
		buffer = null;
		return baos.toByteArray();
	}

	public static ClassNode getNode(final byte[] bytez) {
		ClassReader cr = new ClassReader(bytez);
		ClassNode cn = new ClassNode();
		try {
			cr.accept(cn, ClassReader.EXPAND_FRAMES);
		} catch (Exception e) {
			cr.accept(cn, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG | ClassReader.SKIP_CODE);
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
