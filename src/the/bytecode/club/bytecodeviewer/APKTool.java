package the.bytecode.club.bytecodeviewer;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class APKTool {

	public static synchronized void decodeResources(File input, File output) {
		try {
			File dir = new File(BytecodeViewer.tempDirectory+BytecodeViewer.fs+"Decoded Resources");
			FileUtils.deleteDirectory(dir);
			brut.apktool.Main.main(new String[]{"-s", "-f", "-o", dir.getAbsolutePath(), "decode", input.getAbsolutePath()});
			File original = new File(dir.getAbsolutePath() + BytecodeViewer.fs + "original");
			FileUtils.deleteDirectory(original);
			File classes = new File(dir.getAbsolutePath() + BytecodeViewer.fs + "classes.dex");
			classes.delete();
			File apktool = new File(dir.getAbsolutePath() + BytecodeViewer.fs + "apktool.yml");
			apktool.delete();
			File zip = new File(BytecodeViewer.tempDirectory+BytecodeViewer.fs+MiscUtils.randomString(12)+".zip");
			ZipUtils.zipFolder(dir.getAbsolutePath(), zip.getAbsolutePath(), null);			
			if(zip.exists())
				zip.renameTo(output);
			FileUtils.deleteDirectory(dir);
		} catch(Exception e) {
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
		}
	}
}
