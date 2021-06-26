package the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.synchronizedscroll;

import org.objectweb.asm.Type;

import java.util.Arrays;

/**
 * @author Konloch
 * @since 6/24/2021
 */
public class MethodData
{
	public String name, desc;
	
	@Override
	public boolean equals(final Object o)
	{
		return equals((MethodData) o);
	}
	
	public boolean equals(final MethodData md)
	{
		return this.name.equals(md.name) && this.desc.equals(md.desc);
	}
	
	public String constructPattern()
	{
		final StringBuilder pattern = new StringBuilder();
		pattern.append(name).append(" *\\(");
		final org.objectweb.asm.Type[] types = org.objectweb.asm.Type
				.getArgumentTypes(desc);
		pattern.append("(.*)");
		Arrays.stream(types).map(Type::getClassName)
				.forEach(clazzName -> pattern.append(clazzName.substring(clazzName.lastIndexOf(".") + 1)).append(
						"(.*)"));
		pattern.append("\\) *\\{");
		return pattern.toString();
	}
}
