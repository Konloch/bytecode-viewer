package the.bytecode.club.bytecodeviewer.plugin.strategies;

import java.io.File;

import me.konloch.kontainer.io.DiskReader;

import org.codehaus.janino.SimpleCompiler;

import the.bytecode.club.bytecodeviewer.api.Plugin;
import the.bytecode.club.bytecodeviewer.plugin.PluginLaunchStrategy;

/**
 * @author Konloch
 * @author Bibl (don't ban me pls)
 * @created 1 Jun 2015
 */
public class JavaPluginLaunchStrategy implements PluginLaunchStrategy {

	private static SimpleCompiler compiler = new SimpleCompiler();

	@Override
	public Plugin run(File file) throws Throwable {
		compiler.cook(DiskReader.loadAsString(file.getAbsolutePath()));

		System.out.println(file.getName().substring(0,(int)(file.getName().length()-(".java".length()))));
		Class<?> clazz = (Class<?>) Class.forName(
			file.getName().substring(0,(int)file.getName().length()-".java".length()),
			true,
			compiler.getClassLoader()
		);
		
		return (Plugin) clazz.newInstance();
	}
}