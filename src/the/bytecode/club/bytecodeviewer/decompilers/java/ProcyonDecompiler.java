package the.bytecode.club.bytecodeviewer.decompilers.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import me.konloch.kontainer.io.DiskReader;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.JarUtils;

public class ProcyonDecompiler extends JavaDecompiler {

	@Override
	public String decompileClassNode(ClassNode cn) {
		try {
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
	            e.printStackTrace();
	        }
	        
	        File tempJava = new File(fileStart + getClassNumber(fileStart, ".java") + ".java");
	        
	        final FileOutputStream stream = new FileOutputStream(tempJava);

		    try {
		        final OutputStreamWriter writer = new OutputStreamWriter(stream);
		        final PlainTextOutput p = new PlainTextOutput(writer);

		        try {
		            Decompiler.decompile(
		                cn.getClass().getCanonicalName(),
		                p,
		                DecompilerSettings.javaDefaults()
		            );
		        } finally {
		            writer.close();
		        }
		    }
		    finally {
		        stream.close();
		    }
		    

			String s = DiskReader.loadAsString(tempJava.getAbsolutePath());
			
			tempJava.delete();
			tempClass.delete();
			
			return s;
		}
		catch (final Exception e) {
		   e.printStackTrace();
		}

        return "Procyon error! Send the stacktrace to Konloch at http://the.bytecode.club or konloch@gmail.com";
	}

	@Override
	public void decompileToZip(String zipName) {
		/*File tempZip = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp.jar");
		if(tempZip.exists())
			tempZip.delete();
		
		JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), tempZip.getAbsolutePath());

        File zip = new File(zipName);
        
        try {
	        final FileOutputStream stream = new FileOutputStream(zip);
	
		    try {
		        final OutputStreamWriter writer = new OutputStreamWriter(stream);
		        final PlainTextOutput p = new PlainTextOutput(writer);
	
		        try {
		            Decompiler.decompile(
		                tempZip.getAbsolutePath(),
		                p,
		                DecompilerSettings.javaDefaults()
		            );
		        } finally {
		            writer.close();
		        }
		    }
		    finally {
		        stream.close();
		    }
        } catch(Exception e) {
        	e.printStackTrace();
        }
		
        File tempZip2 = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp" + BytecodeViewer.fs +tempZip.getName());
        if(tempZip2.exists())
        	tempZip2.renameTo(new File(zipName));
        
        tempZip.delete();
        new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp").delete();*/
		
		BytecodeViewer.showMessage("ProcyonDecompiler currently doesn't decompile as zip, please wait till 1.3 of Bytecode Viewer.");
		
	}

}
