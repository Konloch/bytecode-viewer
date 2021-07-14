package the.bytecode.club.bytecodeviewer.resources;

import org.objectweb.asm.tree.ClassNode;

/**
 * @author Konloch
 * @since 7/14/2021
 */
public class Resource
{
	public final String name;
	public String workingName;
	public final ResourceContainer container;
	
	public Resource(String name, String workingName, ResourceContainer container)
	{
		this.name = name;
		this.workingName = workingName;
		this.container = container;
	}
	
	/**
	 * Returns the resource bytes from the resource container
	 */
	public byte[] getResourceBytes()
	{
		return container.getFileContents(name);
	}
	
	/**
	 * Returns the resource bytes from the resource container
	 */
	public ClassNode getResourceClassNode()
	{
		return container.getClassNode(name);
	}
}
