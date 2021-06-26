package the.bytecode.club.bytecodeviewer.resources.importing;

import the.bytecode.club.bytecodeviewer.resources.importing.impl.*;

/**
 * @author Konloch
 * @since 6/26/2021
 */
public enum ImportType
{
	DIRECTORY(new DirectoryResourceImporter()),
	FILE(new FileResourceImporter()),
	ZIP(new ZipResourceImporter()),
	CLASS(new ClassResourceImporter()),
	APK(new APKResourceImporter()),
	DEX(new DEXResourceImporter()),
	;
	
	private final Importer importer;
	
	ImportType(Importer importer) {this.importer = importer;}
	
	public Importer getImporter()
	{
		return importer;
	}
}
