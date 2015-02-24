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

	/**
	 * Loads the classes and resources from the input jar file
	 * @param jarFile the input jar file
	 * @param clazzList the existing map of loaded classes
	 * @throws IOException
	 */
	public static void put(final File jarFile, final HashMap<String, ClassNode> clazzList) throws IOException {
		ZipInputStream jis = new ZipInputStream(new FileInputStream(jarFile));
		ZipEntry entry;
		while ((entry = jis.getNextEntry()) != null) {
			try {
				final String name = entry.getName();
				if (!name.endsWith(".class")) {
					if(!entry.isDirectory())
						BytecodeViewer.loadedResources.put(name, getBytes(jis));
				} else {
					byte[] bytes = getBytes(jis);
					String cafebabe = String.format("%02X", bytes[0])
							+ String.format("%02X", bytes[1])
							+ String.format("%02X", bytes[2])
							+ String.format("%02X", bytes[3]);
					if(cafebabe.toLowerCase().equals("cafebabe")) {
						try {
							final ClassNode cn = getNode(bytes);
							clazzList.put(cn.name, cn);
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else {
						System.out.println(jarFile+">"+name+": Header does not start with CAFEBABE, ignoring.");
					}
				}

			} catch(Exception e) {
				new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
			} finally {
				jis.closeEntry();
			}
		}
		jis.close();

	}
	

	/**
	 * Loads resources only, just for .APK
	 * @param zipFile the input zip file
	 * @throws IOException
	 */
	public static void loadResources(final File zipFile) throws IOException {
		ZipInputStream jis = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry entry;
		while ((entry = jis.getNextEntry()) != null) {
			try {
				final String name = entry.getName();
				if (!name.endsWith(".class") && !name.endsWith(".dex")) {
					if(!entry.isDirectory())
						BytecodeViewer.loadedResources.put(name, getBytes(jis));
					
					jis.closeEntry();
					continue;
				}
			} catch(Exception e) {
				new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
			} finally {
				jis.closeEntry();
			}
		}
		jis.close();

	}

	/**
	 * Reads an InputStream and returns the read byte[]
	 * @param the InputStream
	 * @return the read byte[]
	 * @throws IOException
	 */
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

	/**
	 * Creates a new ClassNode instances from the provided byte[]
	 * @param bytez the class file's byte[]
	 * @return the ClassNode instance
	 */
	public static ClassNode getNode(final byte[] bytez) {
		ClassReader cr = new ClassReader(bytez);
		ClassNode cn = new ClassNode();
		try {
			cr.accept(cn, ClassReader.EXPAND_FRAMES);
		} catch (Exception e) {
			try {
				cr.accept(cn, ClassReader.SKIP_FRAMES);
			} catch(Exception e2) {
				e2.printStackTrace(); //just skip it
			}
		}
		cr = null;
		return cn;
	}

	/**
	 * Saves as jar with manifest
	 * @param nodeList the loaded ClassNodes
	 * @param path the exact path of the output jar file
	 * @param manifest the manifest contents
	 */
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

	/**
	 * Saves a jar without the manifest
	 * @param nodeList The loaded ClassNodes
	 * @param path the exact jar output path
	 */
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
