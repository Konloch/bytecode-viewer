package the.bytecode.club.bytecodeviewer.decompilers.java;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import me.konloch.kontainer.io.DiskReader;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.JarUtils;

/**
 * 
 * @author Konloch
 *
 */

public class CFRDecompiler extends JavaDecompiler {

	@Override
	public String decompileClassNode(ClassNode cn) {
        final ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        
        String fileStart = BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp";
        int fileNumber = getClassNumber(fileStart, ".class");
        
        final File tempClass = new File(fileStart+fileNumber+".class");
        
        try {
            final FileOutputStream fos = new FileOutputStream(tempClass);
            
            fos.write(cw.toByteArray());
            
            fos.close();
        } catch (final IOException e) {
			new the.bytecode.club.bytecodeviewer.gui.StackTraceUI(e);
        }

        String fuckery = fuckery(fileStart);
        org.benf.cfr.reader.Main.main(generateMainMethod(tempClass.getAbsolutePath(), fuckery));
        
        tempClass.delete();
        

        return findFile(new File(fuckery).listFiles());

	}
	
	Random r = new Random();
	File f;
	public String fuckery(String start) {
		boolean b = false;
		while(!b) {
			f = new File(start+r.nextInt(Integer.MAX_VALUE));
			if(!f.exists())
				return f.toString();
		}
		
		return null;
	}
	
	public String findFile(File[] fA) {
		for(File f : fA) {
			if(f.isDirectory())
				return findFile(f.listFiles());
			else {
				String s = "";
				try {
					s = DiskReader.loadAsString(f.getAbsolutePath());
				} catch(Exception e) {
					new the.bytecode.club.bytecodeviewer.gui.StackTraceUI(e);
					return "CFR error! Send the stacktrace to Konloch at http://the.bytecode.club or konloch@gmail.com";
				}
				return s;
			}
		}
		return "CFR error! Send the stacktrace to Konloch at http://the.bytecode.club or konloch@gmail.com";
	}
	
	public String[] generateMainMethod(String filePath, String outputPath) {
		return new String[] {
			filePath,
			"--outputdir",
			outputPath,
			"--decodeenumswitch",
			String.valueOf(BytecodeViewer.viewer.decodeenumswitch.isSelected()),
			"--sugarenums",
			String.valueOf(BytecodeViewer.viewer.sugarenums.isSelected()),
			"--decodestringswitch",
			String.valueOf(BytecodeViewer.viewer.decodestringswitch.isSelected()),
			"--arrayiter",
			String.valueOf(BytecodeViewer.viewer.arrayiter.isSelected()),
			"--collectioniter",
			String.valueOf(BytecodeViewer.viewer.collectioniter.isSelected()),
			"--innerclasses",
			String.valueOf(BytecodeViewer.viewer.innerclasses.isSelected()),
			"--removeboilerplate",
			String.valueOf(BytecodeViewer.viewer.removeboilerplate.isSelected()),
			"--removeinnerclasssynthetics",
			String.valueOf(BytecodeViewer.viewer.removeinnerclasssynthetics.isSelected()),
			"--decodelambdas",
			String.valueOf(BytecodeViewer.viewer.decodelambdas.isSelected()),
			"--hidebridgemethods",
			String.valueOf(BytecodeViewer.viewer.hidebridgemethods.isSelected()),
			"--liftconstructorinit",
			String.valueOf(BytecodeViewer.viewer.liftconstructorinit.isSelected()),
			"--removedeadmethods",
			String.valueOf(BytecodeViewer.viewer.removedeadmethods.isSelected()),
			"--removebadgenerics",
			String.valueOf(BytecodeViewer.viewer.removebadgenerics.isSelected()),
			"--sugarasserts",
			String.valueOf(BytecodeViewer.viewer.sugarasserts.isSelected()),
			"--sugarboxing",
			String.valueOf(BytecodeViewer.viewer.sugarboxing.isSelected()),
			"--showversion",
			String.valueOf(BytecodeViewer.viewer.showversion.isSelected()),
			"--decodefinally",
			String.valueOf(BytecodeViewer.viewer.decodefinally.isSelected()),
			"--tidymonitors",
			String.valueOf(BytecodeViewer.viewer.tidymonitors.isSelected()),
			"--lenient",
			String.valueOf(BytecodeViewer.viewer.lenient.isSelected()),
			"--dumpclasspath",
			String.valueOf(BytecodeViewer.viewer.dumpclasspath.isSelected()),
			"--comments",
			String.valueOf(BytecodeViewer.viewer.comments.isSelected()),
			"--forcetopsort",
			String.valueOf(BytecodeViewer.viewer.forcetopsort.isSelected()),
			"--forcetopsortaggress",
			String.valueOf(BytecodeViewer.viewer.forcetopsortaggress.isSelected()),
			"--stringbuffer",
			String.valueOf(BytecodeViewer.viewer.stringbuffer.isSelected()),
			"--stringbuilder",
			String.valueOf(BytecodeViewer.viewer.stringbuilder.isSelected()),
			"--silent",
			String.valueOf(BytecodeViewer.viewer.silent.isSelected()),
			"--recover",
			String.valueOf(BytecodeViewer.viewer.recover.isSelected()),
			"--eclipse",
			String.valueOf(BytecodeViewer.viewer.eclipse.isSelected()),
			"--override",
			String.valueOf(BytecodeViewer.viewer.override.isSelected()),
			"--showinferrable",
			String.valueOf(BytecodeViewer.viewer.showinferrable.isSelected()),
			"--aexagg",
			String.valueOf(BytecodeViewer.viewer.aexagg.isSelected()),
			"--forcecondpropagate",
			String.valueOf(BytecodeViewer.viewer.forcecondpropagate.isSelected()),
			"--hideutf",
			String.valueOf(BytecodeViewer.viewer.hideutf.isSelected()),
			"--hidelongstrings",
			String.valueOf(BytecodeViewer.viewer.hidelongstrings.isSelected()),
			"--commentmonitors",
			String.valueOf(BytecodeViewer.viewer.commentmonitor.isSelected()),
			"--allowcorrecting",
			String.valueOf(BytecodeViewer.viewer.allowcorrecting.isSelected()),
			"--labelledblocks",
			String.valueOf(BytecodeViewer.viewer.labelledblocks.isSelected()),
			"--j14classobj",
			String.valueOf(BytecodeViewer.viewer.j14classobj.isSelected()),
			"--hidelangimports",
			String.valueOf(BytecodeViewer.viewer.hidelangimports.isSelected()),
			"--recovertypeclash",
			String.valueOf(BytecodeViewer.viewer.recoverytypeclash.isSelected()),
			"--recovertypehints",
			String.valueOf(BytecodeViewer.viewer.recoverytypehints.isSelected()),
			"--forcereturningifs",
			String.valueOf(BytecodeViewer.viewer.forceturningifs.isSelected()),
			"--forloopaggcapture",
			String.valueOf(BytecodeViewer.viewer.forloopaggcapture.isSelected()),
		};
	}

	byte[] buffer = new byte[1024];
	@Override
	public void decompileToZip(String zipName) {
		File tempZip = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp.jar");
		if(tempZip.exists())
			tempZip.delete();
		
		JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), tempZip.getAbsolutePath());

        String fileStart = BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp";
        
        String fuckery = fuckery(fileStart);

        org.benf.cfr.reader.Main.main(generateMainMethod(tempZip.getAbsolutePath(), fuckery));
        
        tempZip.delete();
		File fuck = new File(fuckery);
        
		try {
			zip(fuck, new File(zipName));
		} catch (IOException e) {
			new the.bytecode.club.bytecodeviewer.gui.StackTraceUI(e);
		}
        
		fuck.delete();
	}
	
	@SuppressWarnings("resource")
	public void zip(File directory, File zipfile) throws IOException {
		    java.net.URI base = directory.toURI();
		    Deque<File> queue = new LinkedList<File>();
		    queue.push(directory);
		    OutputStream out = new FileOutputStream(zipfile);
		    Closeable res = out;
		    try {
		      ZipOutputStream zout = new ZipOutputStream(out);
		      res = zout;
		      while (!queue.isEmpty()) {
		        directory = queue.pop();
		        for (File kid : directory.listFiles()) {
		          String name = base.relativize(kid.toURI()).getPath();
		          if (kid.isDirectory()) {
		            queue.push(kid);
		            name = name.endsWith("/") ? name : name + "/";
		            zout.putNextEntry(new ZipEntry(name));
		          } else {
		            zout.putNextEntry(new ZipEntry(name));
		            copy(kid, zout);
		            zout.closeEntry();
		          }
		        }
		      }
		    } finally {
		      res.close();
		      out.close();
		    }
		  }

	  private static void copy(InputStream in, OutputStream out) throws IOException {
		  byte[] buffer = new byte[1024];
		    while (true) {
		      int readCount = in.read(buffer);
		      if (readCount < 0) {
		        break;
		      }
		      out.write(buffer, 0, readCount);
		    }
		  }

		  private static void copy(File file, OutputStream out) throws IOException {
		    InputStream in = new FileInputStream(file);
		    try {
		      copy(in, out);
		    } finally {
		      in.close();
		    }
		  }
	  
}
