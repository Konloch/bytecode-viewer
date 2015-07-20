package the.bytecode.club.bootloader;

import java.util.HashMap;
import java.util.Map;

import the.bytecode.club.bootloader.resource.ExternalResource;

/**
 * @author Bibl (don't ban me pls)
 * @created 21 Jul 2015 00:18:07
 */
public final class AbstractLoaderFactory {

	private static final String DEFAULT_KEY = "default-factory";
	private static final Map<String, LoaderFactory<?>> FACTORYCACHE = new HashMap<String, LoaderFactory<?>>();
	
	public static void register(LoaderFactory<?> factory) {
		register(DEFAULT_KEY, factory);
	}
	
	public static void register(String key, LoaderFactory<?> factory) {
		if(key == null || factory == null) {
			throw new IllegalArgumentException("null key or factory");
		}
		
		if(FACTORYCACHE.containsKey(key)) {
			throw new IllegalArgumentException("factory already registered with key: " + key);
		}
		
		FACTORYCACHE.put(key, factory);
	}
	
	public static void unregister(String key) {
		if(key == null) {
			throw new IllegalArgumentException("null key");
		}
		
		if(!FACTORYCACHE.containsKey(key)) {
			throw new IllegalArgumentException("factory doesn't key for key: " + key);
		}
		
		FACTORYCACHE.remove(key);
	}
	
	public static <T extends ExternalResource<?>> LoaderFactory<T> find() {
		return find(DEFAULT_KEY);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends ExternalResource<?>> LoaderFactory<T> find(String key) {
		if(key == null) {
			throw new IllegalArgumentException("null key");
		}
		
		if(!FACTORYCACHE.containsKey(key)) {
			throw new IllegalArgumentException("factory doesn't key for key: " + key);
		}
		
		return (LoaderFactory<T>) FACTORYCACHE.get(key);
	}
}