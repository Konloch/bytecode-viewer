package the.bytecode.club.bytecodeviewer.resources.exporting;

import the.bytecode.club.bytecodeviewer.resources.exporting.impl.APKExport;
import the.bytecode.club.bytecodeviewer.resources.exporting.impl.DexExport;
import the.bytecode.club.bytecodeviewer.resources.exporting.impl.RunnableJarExporter;
import the.bytecode.club.bytecodeviewer.resources.exporting.impl.ZipExport;

/**
 * @author Konloch
 * @since 6/27/2021
 */
public enum Export
{
	RUNNABLE_JAR(new RunnableJarExporter()),
	ZIP(new ZipExport()),
	DEX(new DexExport()),
	APK(new APKExport())
	;
	
	private final Exporter exporter;
	
	Export(Exporter exporter) {this.exporter = exporter;}
	
	public Exporter getExporter()
	{
		return exporter;
	}
}
