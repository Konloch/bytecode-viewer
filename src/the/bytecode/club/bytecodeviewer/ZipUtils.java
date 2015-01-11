package the.bytecode.club.bytecodeviewer;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.filechooser.FileFilter;

/**
 * Rudimentary utility class for Zip archives creation.
 */
public final class ZipUtils {
	
	 public static final int BUFFER = 2048;
	
	 /**
	  * Unzip files to path.
	  * 
	  * @param zipFileName the zip file name
	  * @param fileExtractPath the file extract path
	  * @throws IOException Signals that an I/O exception has occurred.
	  */
	 public static void unzipFilesToPath(String jarPath, String destinationDir) throws IOException {
			File file = new File(jarPath);
			JarFile jar = new JarFile(file);
	 
			// fist get all directories,
			// then make those directory on the destination Path
			for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements();) {
				JarEntry entry = (JarEntry) enums.nextElement();
	 
				String fileName = destinationDir + File.separator + entry.getName();
				File f = new File(fileName);
	 
				if (fileName.endsWith("/")) {
					f.mkdirs();
				}
	 
			}
	 
			//now create all files
			for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements();) {
				JarEntry entry = (JarEntry) enums.nextElement();
	 
				String fileName = destinationDir + File.separator + entry.getName();
				File f = new File(fileName);
	 
				if (!fileName.endsWith("/")) {
					InputStream is = jar.getInputStream(entry);
					FileOutputStream fos = new FileOutputStream(f);
	 
					// write contents of 'is' to 'fos'
					while (is.available() > 0) {
						fos.write(is.read());
					}
	 
					fos.close();
					is.close();
				}
			}
			
			try {
				jar.close();
			} catch(Exception e) {
				
			}
		}

	private static final String ZIP_FILE_EXTENSION = ".zip";
	private static final FileFilter ZIP_FILE_FILTER = new FileFilter() {

		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(ZIP_FILE_EXTENSION);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	private ZipUtils() {
		// Utility class, cannot be instantiated.
	}

	/**
	 * Compress the given root and all its underlying folders and files to the
	 * target file, preserving files hierarchy.
	 * 
	 * @param root
	 *            The root of the Zip archive
	 * @param target
	 *            The target archive file (must be a valid Zip file name)
	 * @throws IOException
	 *             If an error occurs during the process
	 */
	public static void zipDirectory(final File root, final File target)
			throws IOException {
		if (!ZIP_FILE_FILTER.accept(target)) {
			throw new IllegalArgumentException("Target file "
					+ target.getName() + " is not a valid Zip file name");
		}

		byte[] buffer = new byte[1024];
		FileOutputStream fileOutputStream = null;
		ZipOutputStream zipOutputStream = null;

		try {
			fileOutputStream = new FileOutputStream(target);
			zipOutputStream = new ZipOutputStream(fileOutputStream);

			FileInputStream fileInputStream = null;

			for (File file : ZipUtils.listFilesRecursive(root)) {
				ZipEntry entry = new ZipEntry(ZipUtils.stripRootInclusive(file,
						root).getPath());
				zipOutputStream.putNextEntry(entry);
				try {
					fileInputStream = new FileInputStream(file);
					int length;
					while ((length = fileInputStream.read(buffer)) > 0) {
						zipOutputStream.write(buffer, 0, length);
					}
				} finally {
					fileInputStream.close();
				}

				zipOutputStream.closeEntry();
			}
		} finally {
			zipOutputStream.close();
		}
	}

	/**
	 * Unzip the given archive Zip file to the target location. If target
	 * location is a file, the extraction will be performed in the same
	 * directory of this target file.
	 * 
	 * @param zipFile
	 *            The Zip archive file
	 * @param target
	 *            The target location
	 * @throws IOException
	 *             If an error occurs during the process
	 */
	public static void unzip(final File zipFile, File target)
			throws IOException {
		if (zipFile == null) {
			throw new IllegalArgumentException("Cannot unzip a null file!");
		} /*else if (!ZIP_FILE_FILTER.accept(zipFile)) {
			throw new IllegalArgumentException(
					"Given archive is not a valid Zip file!");
		}*/
		if (target == null) {
			throw new IllegalArgumentException("Cannot unzip to a null target!");
		}

		byte[] buffer = new byte[1024];

		if (!target.exists()) {
			target.mkdir();
		} else if (target.isFile()) {
			// Target is a file, will try to unzip in the same folder.
			target = target.getParentFile();
			if (target == null) {
				throw new IllegalArgumentException(
						"Target is a file and has no parent!");
			}
		}

		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(
				zipFile));
		try {
			for (ZipEntry entry = zipInputStream.getNextEntry(); entry != null; entry = zipInputStream
					.getNextEntry()) {
				File file = new File(target, entry.getName());

				// Create parent folders (folders are not in the Zip entries).
				new File(file.getParent()).mkdirs();

				FileOutputStream fileOutputStream = new FileOutputStream(file);
				try {
					int length;
					while ((length = zipInputStream.read(buffer)) > 0) {
						fileOutputStream.write(buffer, 0, length);
					}
				} finally {
					fileOutputStream.close();
				}
				zipInputStream.closeEntry();
			}
		} finally {
			zipInputStream.close();
		}

	}

	/**
	 * List all files and folders from the given root.
	 * 
	 * @param root
	 *            The root of the listing
	 * @return A list of the files under the given root
	 */
	public static List<File> listFilesRecursive(final File root) {
		List<File> packedFiles = new ArrayList<File>();

		File[] subFiles = root.listFiles();
		if (subFiles == null) {
			return packedFiles;
		}

		for (File file : subFiles) {
			if (file.isFile()) {
				File packedFile = new File(root, file.getName());
				packedFiles.add(packedFile);
			} else if (file.isDirectory()) {
				packedFiles.addAll(ZipUtils.listFilesRecursive(file));
			}
		}

		return packedFiles;
	}

	/**
	 * Strip the given file from any parent path, preserving the root as the
	 * absolute parent.
	 * <p>
	 * Ex. with 'Folder' as the root: /home/johnj/Test/Folder/File.txt =>
	 * /Folder/File.txt
	 * </p>
	 * 
	 * @param file
	 *            The file to strip
	 * @param root
	 *            The root of the stripping
	 * @return The stripped file
	 */
	private static File stripRootInclusive(final File file, final File root) {
		String parentPath = root.getParent();

		if (parentPath == null) {
			// Assuming no existing parent.
			return file;
		}

		return new File(file.getAbsolutePath().substring(parentPath.length()));
	}
	
	public static void zipFile(File inputFile, File outputZip) {
    	byte[] buffer = new byte[1024];
    	 
    	try {
    		FileOutputStream fos = new FileOutputStream(outputZip);
    		ZipOutputStream zos = new ZipOutputStream(fos);
    		ZipEntry ze= new ZipEntry(inputFile.getName());
    		zos.putNextEntry(ze);
    		FileInputStream in = new FileInputStream(inputFile);
 
    		int len;
    		while ((len = in.read(buffer)) > 0) {
    			zos.write(buffer, 0, len);
    		}
 
    		in.close();
    		zos.closeEntry();
 
    		zos.close();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
	}
}