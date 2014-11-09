package the.bytecode.club.bytecodeviewer.decompilers.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.konloch.kontainer.io.DiskReader;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.JarUtils;

/**
 * A complete FernFlower launcher with all the options (except 2)
 * 
 * @author Konloch
 * @author WaterWolf
 *
 */

public class FernFlowerDecompiler extends JavaDecompiler {
	
	@Override
	public void decompileToZip(String zipName) {
		File tempZip = new File(BytecodeViewer.tempDirectory + "temp.zip");
		if(tempZip.exists())
			tempZip.delete();
		
		File f = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs +"temp" + BytecodeViewer.fs);
		f.mkdir();
		
		JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), tempZip.getAbsolutePath());

		org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler.main(generateMainMethod(tempZip.getAbsolutePath(), BytecodeViewer.tempDirectory + "./temp/"));
        File tempZip2 = new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp" + BytecodeViewer.fs +tempZip.getName());
        if(tempZip2.exists())
        	tempZip2.renameTo(new File(zipName));
        
        tempZip.delete();
        new File(BytecodeViewer.tempDirectory + BytecodeViewer.fs + "temp").delete();
	}

	@Override
    public String decompileClassNode(final ClassNode cn) {
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
        
        org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler.main(generateMainMethod(tempClass.getAbsolutePath(), "."));
        
        tempClass.delete();
        
        final File outputJava = new File("temp"+fileNumber+".java");
        if (outputJava.exists()) {
        	String s;
			try {
				s = DiskReader.loadAsString(outputJava.getAbsolutePath());
	        	
	            outputJava.delete();
	            
	            return s;
			} catch (Exception e) {
				new the.bytecode.club.bytecodeviewer.gui.StackTraceUI(e);
			}
        }
        return "FernFlower error! Send the stacktrace to Konloch at http://the.bytecode.club or konloch@gmail.com";
    }

    private String[] generateMainMethod(String className, String folder) {
    	return new String[] {
    			"-rbr="+r(BytecodeViewer.viewer.rbr.isSelected()),
    			"-rsy="+r(BytecodeViewer.viewer.rsy.isSelected()),
    			"-din="+r(BytecodeViewer.viewer.din.isSelected()),
    			"-dc4="+r(BytecodeViewer.viewer.dc4.isSelected()),
    			"-das="+r(BytecodeViewer.viewer.das.isSelected()),
    			"-hes="+r(BytecodeViewer.viewer.hes.isSelected()),
    			"-hdc="+r(BytecodeViewer.viewer.hdc.isSelected()),
    			"-dgs="+r(BytecodeViewer.viewer.dgs.isSelected()),
    			"-ner="+r(BytecodeViewer.viewer.ner.isSelected()),
    			"-den="+r(BytecodeViewer.viewer.den.isSelected()),
    			"-rgn="+r(BytecodeViewer.viewer.rgn.isSelected()),
    			"-bto="+r(BytecodeViewer.viewer.bto.isSelected()),
    			"-nns="+r(BytecodeViewer.viewer.nns.isSelected()),
    			"-uto="+r(BytecodeViewer.viewer.uto.isSelected()),
    			"-udv="+r(BytecodeViewer.viewer.udv.isSelected()),
    			"-rer="+r(BytecodeViewer.viewer.rer.isSelected()),
    			"-fdi="+r(BytecodeViewer.viewer.fdi.isSelected()),
    			"-asc="+r(BytecodeViewer.viewer.asc.isSelected()),
    			className,
    			folder};
    }
    
    private String r(boolean b) {
    	if(b) {
    		return "1";
    	} else {
    		return "0";
    	}
    }

}
