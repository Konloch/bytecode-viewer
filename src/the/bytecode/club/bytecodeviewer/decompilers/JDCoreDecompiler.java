package the.bytecode.club.bytecodeviewer.decompilers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import me.konloch.kontainer.io.DiskWriter;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.strobel.core.StringUtilities;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.decompiler.languages.java.JavaFormattingOptions;
import com.strobel.assembler.InputTypeLoader;
import com.strobel.assembler.metadata.Buffer;
import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.JarTypeLoader;
import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.JarUtils;
import the.bytecode.club.bytecodeviewer.MiscUtils;

/**
 * JDCore Decompiler Wrapper
 * 
 * @author Konloch
 * 
 */

public class JDCoreDecompiler extends Decompiler {

	@Override
	public void decompileToClass(String className, String classNameSaved) {
		ClassNode cn = BytecodeViewer.getClassNode(className);
		final ClassWriter cw = new ClassWriter(0);
		try {
			cn.accept(cw);
		} catch(Exception e) {
			e.printStackTrace();
			try {
				Thread.sleep(200);
				cn.accept(cw);
			} catch (InterruptedException e1) { }
		}
		String contents = decompileClassNode(cn, cw.toByteArray());
		DiskWriter.replaceFile(classNameSaved, contents, false);
	}

	@Override
	public String decompileClassNode(ClassNode cn, byte[] b) {
		String exception = "";
		try {
			String decompiledSource = "dicks WIP";

			return decompiledSource;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			e.printStackTrace();

			exception = "Bytecode Viewer Version: " + BytecodeViewer.version + BytecodeViewer.nl + BytecodeViewer.nl + sw.toString();
		}
		return "Procyon error! Send the stacktrace to Konloch at http://the.bytecode.club or konloch@gmail.com"+BytecodeViewer.nl+BytecodeViewer.nl+"Suggested Fix: Click refresh class, if it fails again try another decompiler."+BytecodeViewer.nl+BytecodeViewer.nl+exception;
	}

	@Override
	public void decompileToZip(String zipName) {
	}

}
