package the.bytecode.club.bootloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import the.bytecode.club.bootloader.resource.ExternalResource;

/**
 * @author Bibl (don't ban me pls)
 * @created 21 Jul 2015 00:09:53
 */
public class ClassPathLoader implements ILoader<Object> {

	void extendClassPath(URL url) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> urlClass = URLClassLoader.class;
		Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke(urlClassLoader, new Object[] { url });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see the.bytecode.club.bootloader.ILoader#bind(the.bytecode.club.bootloader .resource.ExternalResource)
	 */
	@Override
	public void bind(ExternalResource<Object> resource) {
		try {
			if (resource != null) {
				URL url = resource.getLocation();
				if (url != null) {
					extendClassPath(url);
				}
			}
		}/* catch (IOException e) {
			System.err.println("Error loading resource.");
			e.printStackTrace();
		}*/ catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.err.println("Error reflecting URLClassLoader.addURL(URL) ?");
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see the.bytecode.club.bootloader.ILoader#findClass(java.lang.String)
	 */
	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException, NoClassDefFoundError {
		return Class.forName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see the.bytecode.club.bootloader.ILoader#loadClass(java.lang.String)
	 */
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException, NoClassDefFoundError {
		return findClass(name);
	}
}