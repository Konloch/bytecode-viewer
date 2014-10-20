package the.bytecode.club.bytecodeviewer.decompilers.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.assembler.InputTypeLoader;
import com.strobel.assembler.metadata.Buffer;
import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

/**
 * 
 * @author Konloch
 * @author DeathMarine
 *
 */

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
	        

			DecompilerSettings settings = new DecompilerSettings();
			LuytenTypeLoader typeLoader = new LuytenTypeLoader();
			MetadataSystem metadataSystem = new MetadataSystem(typeLoader);
			TypeReference type = metadataSystem.lookupType(tempClass.getCanonicalPath());

			DecompilationOptions decompilationOptions = new DecompilationOptions();
			decompilationOptions.setSettings(DecompilerSettings.javaDefaults());
			decompilationOptions.setFullDecompilation(true);
	
			TypeDefinition resolvedType = null;
			if (type == null || ((resolvedType = type.resolve()) == null)) {
				throw new Exception("Unable to resolve type.");
			}
			StringWriter stringwriter = new StringWriter();
			settings.getLanguage().decompileType(resolvedType,
					new PlainTextOutput(stringwriter), decompilationOptions);
			String decompiledSource = stringwriter.toString();

	        
			return decompiledSource;
		} catch(Exception e) {
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
	
	
	/**
	 * 
	 * @author DeathMarine
	 *
	 */
	public final class LuytenTypeLoader implements ITypeLoader {
	    private final List<ITypeLoader> _typeLoaders;

	    public LuytenTypeLoader() {
	        _typeLoaders = new ArrayList<ITypeLoader>();
	        _typeLoaders.add(new InputTypeLoader());
	    }

	    public final List<ITypeLoader> getTypeLoaders() {
	        return _typeLoaders;
	    }

	    @Override
	    public boolean tryLoadType(final String internalName, final Buffer buffer) {
	        for (final ITypeLoader typeLoader : _typeLoaders) {
	            if (typeLoader.tryLoadType(internalName, buffer)) {
	                return true;
	            }

	            buffer.reset();
	        }

	        return false;
	    }
	}

}
