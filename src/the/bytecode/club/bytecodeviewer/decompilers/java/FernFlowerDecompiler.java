package the.bytecode.club.bytecodeviewer.decompilers.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

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

public class FernFlowerDecompiler {
	
	public void decompileToZip(String zipName) {
		File tempZip = new File(BytecodeViewer.tempDirectory + "temp.zip");
		if(tempZip.exists())
			tempZip.delete();
		
		JarUtils.saveAsJar(BytecodeViewer.getLoadedClasses(), tempZip.getAbsolutePath());

        de.fernflower.main.decompiler.ConsoleDecompiler.main(new String[] {tempZip.getAbsolutePath(), BytecodeViewer.tempDirectory + "./temp/"});
        File tempZip2 = new File(BytecodeViewer.tempDirectory + System.getProperty("file.separator") + "temp" + System.getProperty("file.separator") +tempZip.getName());
        if(tempZip2.exists())
        	tempZip2.renameTo(new File(zipName));
        
        tempZip.delete();
        new File(BytecodeViewer.tempDirectory + System.getProperty("file.separator") + "temp").delete();
	}

    public String decompileClassNode(final ClassNode cn) {
        final ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        
        String fileStart = BytecodeViewer.tempDirectory + System.getProperty("file.separator") + "temp";
        int fileNumber = getClassNumber(fileStart, ".class");
        
        final File tempClass = new File(fileStart+fileNumber+".class");
        
        try {
            final FileOutputStream fos = new FileOutputStream(tempClass);
            
            fos.write(cw.toByteArray());
            
            fos.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        
        de.fernflower.main.decompiler.ConsoleDecompiler.main(generateMainMethod(tempClass.getAbsolutePath(), "."));
        
        tempClass.delete();
        
        final File outputJava = new File("temp"+fileNumber+".java");
        if (outputJava.exists()) {
            
            final String nl = System.getProperty("line.separator");
            final StringBuffer javaSrc = new StringBuffer();
            
            try {
                final BufferedReader br = new BufferedReader(new FileReader(outputJava));
                String line;
                while ((line = br.readLine()) != null) {
                    javaSrc.append(line + nl);
                }
                br.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
            
            outputJava.delete();
            
            return javaSrc.toString();   
        }
        return "FernFlower error! Send the stacktrace to Konloch at http://the.bytecode.club or konloch@gmail.com";
    }
    
    File tempF = null;
    public int getClassNumber(String start, String ext) {
    	boolean b = true;
    	int i = 0;
    	while(b) {
    		tempF = new File(start + i + ext);
    		if(!tempF.exists())
    			b = false;
    		else
    			i++;
    	}
    	return i;
    }

    private String[] generateMainMethod(String className, String folder) {
    	boolean rbr = BytecodeViewer.viewer.rbr.isSelected();
    	boolean rsy = BytecodeViewer.viewer.rsy.isSelected();
    	boolean din = BytecodeViewer.viewer.din.isSelected();
    	boolean dc4 = BytecodeViewer.viewer.dc4.isSelected();
    	boolean das = BytecodeViewer.viewer.das.isSelected();
    	boolean hes = BytecodeViewer.viewer.hes.isSelected();
    	boolean hdc = BytecodeViewer.viewer.hdc.isSelected();
    	boolean dgs = BytecodeViewer.viewer.dgs.isSelected();
    	boolean ner = BytecodeViewer.viewer.ner.isSelected();
    	boolean den = BytecodeViewer.viewer.den.isSelected();
    	boolean rgn = BytecodeViewer.viewer.rgn.isSelected();
    	boolean bto = BytecodeViewer.viewer.bto.isSelected();
    	boolean nns = BytecodeViewer.viewer.nns.isSelected();
    	boolean uto = BytecodeViewer.viewer.uto.isSelected();
    	boolean udv = BytecodeViewer.viewer.udv.isSelected();
    	boolean rer = BytecodeViewer.viewer.rer.isSelected();
    	boolean fdi = BytecodeViewer.viewer.fdi.isSelected();
    	boolean asc = BytecodeViewer.viewer.asc.isSelected();
    	return new String[] {
    			"-rbr="+r(rbr),
    			"-rsy="+r(rsy),
    			"-din="+r(din),
    			"-dc4="+r(dc4),
    			"-das="+r(das),
    			"-hes="+r(hes),
    			"-hdc="+r(hdc),
    			"-dgs="+r(dgs),
    			"-ner="+r(ner),
    			"-den="+r(den),
    			"-rgn="+r(rgn),
    			"-bto="+r(bto),
    			"-nns="+r(nns),
    			"-uto="+r(uto),
    			"-udv="+r(udv),
    			"-rer="+r(rer),
    			"-fdi="+r(fdi),
    			"-asc="+r(asc),
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
