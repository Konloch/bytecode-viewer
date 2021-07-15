package the.bytecode.club.bytecodeviewer.resources;

import javax.swing.*;
import java.util.HashMap;

/**
 * @author Konloch
 * @since 7/13/2021
 */

public enum ResourceType
{
	//TODO tar/gzip?
	//TODO add the files icons for the missing files from the.bytecode.club.bytecodeviewer.util.SyntaxLanguage
	
	CLASS_FILE(IconResources.classIcon, "class"),
	JAVA_ARCHIVE(IconResources.jarIcon, "jar", "war", "ear"),
	ZIP_ARCHIVE(IconResources.zipIcon, "zip"),
	ANDROID_ARCHIVE(IconResources.androidIcon, "apk", "wapk", "dex"),
	IMAGE_FILE(IconResources.imageIcon, "png", "jpg", "jpeg", "bmp", "wbmp", "gif", "tif"),
	CONFIG_TEXT_FILE(IconResources.configIcon, "properties", "xml", "jsp", "mf", "config",
			"csv", "yml", "yaml", "ini", "json", "sql", "gradle", "dockerfile", "htaccess",
			"plugin", "attachprovider", "transportservice", "connector"),
	JAVA_FILE(IconResources.javaIcon, "java"),
	TEXT_FILE(IconResources.textIcon, "txt", "md", "log", "html", "css"),
	CPP_FILE(IconResources.cplusplusIcon, "c", "cpp", "h"),
	CSHAR_FILE(IconResources.csharpIcon, "cs"),
	BAT_FILE(IconResources.batIcon, "bat", "batch"),
	SH_FILE(IconResources.shIcon, "sh", "bash"),
	;
	
	public static final HashMap<String, ResourceType> extensionMap = new HashMap<>();
	public static final HashMap<String, ResourceType> imageExtensionMap = new HashMap<>();
	public static final HashMap<String, ResourceType> supportedBCVExtensionMap = new HashMap<>();
	
	private final ImageIcon icon;
	private final String[] extensions;
	//private final byte[][] headerMagicNumber;
	
	static
	{
		//add all extensions
		for(ResourceType t : values())
			for(String extension : t.extensions)
				extensionMap.put(extension, t);
			
		//add image extensions
		for(String extension : IMAGE_FILE.extensions)
			imageExtensionMap.put(extension, IMAGE_FILE);
			
		//add extensions BCV can be opened with
		for(String extension : CLASS_FILE.extensions)
			supportedBCVExtensionMap.put(extension, CLASS_FILE);
		for(String extension : JAVA_ARCHIVE.extensions)
			supportedBCVExtensionMap.put(extension, JAVA_ARCHIVE);
		for(String extension : ZIP_ARCHIVE.extensions)
			supportedBCVExtensionMap.put(extension, ZIP_ARCHIVE);
		for(String extension : ANDROID_ARCHIVE.extensions)
			supportedBCVExtensionMap.put(extension, ANDROID_ARCHIVE);
	}
	
	ResourceType(ImageIcon icon, String... extensions)
	{
		this.icon = icon;
		this.extensions = extensions;
	}
	
	public ImageIcon getIcon()
	{
		return icon;
	}
}
