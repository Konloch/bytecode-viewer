package jd.cli.loader;

import jd.core.loader.Loader;

import java.io.File;

public abstract class BaseLoader implements Loader
{
	protected String codebase;
	protected long lastModified;
	protected boolean isFile;
	
	public BaseLoader(File file) 
	{
		this.codebase = file.getAbsolutePath();
		this.lastModified = file.lastModified();
		this.isFile = file.isFile();
	}

	public String getCodebase() 
	{
		return codebase;
	}

	public long getLastModified() 
	{
		return lastModified;
	}

	public boolean isFile() 
	{
		return isFile;
	}
}
