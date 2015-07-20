package the.bytecode.club.bootloader.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.tree.ClassNode;

/**
 * @author Bibl (don't ban me pls)
 * @created 25 May 2015 (actually before this)
 */
public class ClassHelper {

	public static Map<String, ClassNode> convertToMap(Collection<ClassNode> classes) {
		Map<String, ClassNode> map = new HashMap<String, ClassNode>();
		for (ClassNode cn : classes) {
			map.put(cn.name, cn);
		}
		return map;
	}
	
	public static <T, K> Map<T, K> copyOf(Map<T, K> src) {
		Map<T, K> dst = new HashMap<T, K>();
		copy(src, dst);
		return dst;
	}
	
	public static <T, K> void copy(Map<T, K> src, Map<T, K> dst) {
		for(Entry<T, K> e : src.entrySet()) {
			dst.put(e.getKey(), e.getValue());
		}
	}
}