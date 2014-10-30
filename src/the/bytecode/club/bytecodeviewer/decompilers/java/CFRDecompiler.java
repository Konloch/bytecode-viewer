package the.bytecode.club.bytecodeviewer.decompilers.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import me.konloch.kontainer.io.DiskReader;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

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
		BytecodeViewer.showMessage("CFRDecompiler currently doesn't decompile as zip, please wait till Beta 1.4 of Bytecode Viewer.");

	}

}
