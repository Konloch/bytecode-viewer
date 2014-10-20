package the.bytecode.club.bytecodeviewer.decompilers.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import me.konloch.kontainer.io.DiskReader;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.JarUtils;

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
            e.printStackTrace();
        }

        String fuckery = fuckery(fileStart);
        org.benf.cfr.reader.Main.main(generateMainMethod(tempClass.getAbsolutePath(), fuckery));
        
        tempClass.delete();
        

        for(File outputJava : new File(fuckery).listFiles()) {
        	String s;
			try {
				s = DiskReader.loadAsString(outputJava.getAbsolutePath());
	        	
	            outputJava.delete();
	            
	            return s;
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        return "CFR error! Send the stacktrace to Konloch at http://the.bytecode.club or konloch@gmail.com";
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
	
	public String[] generateMainMethod(String filePath, String outputPath) {
		return new String[] {
			filePath,
			"--outputdir",
			outputPath
		};
	}

	@Override
	public void decompileToZip(String zipName) {
		/*
		File tempZip = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp.jar");
		if(tempZip.exists())
			tempZip.delete();
		
		JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), tempZip.getAbsolutePath());
		

        String fileStart = BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp";
        
		
        String fuckery = fuckery(fileStart);
        org.benf.cfr.reader.Main.main(generateMainMethod(tempZip.getAbsolutePath(), fuckery));

        tempZip.delete();

        for(File f : new File(fuckery).listFiles()) {
        	//put contents into a zipfile
        }*/
		BytecodeViewer.showMessage("CFRDecompiler currently doesn't decompile as zip, please wait till 1.3 of Bytecode Viewer.");

	}

}
