package the.bytecode.club.bytecodeviewer;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Rudimentary utility class for Zip archives.
 */
public final class ZipUtils {
	
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
	
	 public static void zipFolder(String srcFolder, String destZipFile, String ignore) throws Exception {
		    ZipOutputStream zip = null;
		    FileOutputStream fileWriter = null;

		    fileWriter = new FileOutputStream(destZipFile);
		    zip = new ZipOutputStream(fileWriter);

		    addFolderToZip("", srcFolder, zip, ignore);
		    zip.flush();
		    zip.close();
		  }

		  public static void addFileToZip(String path, String srcFile, ZipOutputStream zip, String ignore)
		      throws Exception {
			  
		    File folder = new File(srcFile);
		    if (folder.isDirectory()) {
		      addFolderToZip(path, srcFile, zip, ignore);
		    } else {
		      byte[] buf = new byte[1024];
		      int len;
		      FileInputStream in = new FileInputStream(srcFile);
		      ZipEntry entry = null;
		      if(ignore == null)
		    	  entry = new ZipEntry(path + "/" + folder.getName());
		      else
		    	  entry = new ZipEntry(path.replace(ignore, "BCV_Krakatau") + "/" + folder.getName());
		      zip.putNextEntry(entry);
		      while ((len = in.read(buf)) > 0) {
		        zip.write(buf, 0, len);
		      }
		      in.close();
		    }
		  }

		 public static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip, String ignore)
		      throws Exception {
		    File folder = new File(srcFolder);

		    for (String fileName : folder.list()) {
		      if (path.equals("")) {
		        addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, ignore);
		      } else {
		        addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip, ignore);
		      }
		    }
		  }
}