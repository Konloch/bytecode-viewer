package the.bytecode.club.bootloader.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.ClassNode;

/**
 * @author Bibl (don't ban me pls)
 * @created ages ago
 */
public class JarContents<C extends ClassNode> {

	private final DataContainer<C> classContents;
	private final DataContainer<JarResource> resourceContents;

	public JarContents() {
		classContents = new ClassNodeContainer<C>();
		resourceContents = new ResourceContainer();
	}

	public JarContents(DataContainer<C> classContents, DataContainer<JarResource> resourceContents) {
		this.classContents = classContents == null ? new ClassNodeContainer<C>() : classContents;
		this.resourceContents = resourceContents == null ? new ResourceContainer() : resourceContents;
	}

	public final DataContainer<C> getClassContents() {
		return classContents;
	}

	public final DataContainer<JarResource> getResourceContents() {
		return resourceContents;
	}

	public void merge(JarContents<C> contents) {
		classContents.addAll(contents.classContents);
		resourceContents.addAll(contents.resourceContents);
	}

	public JarContents<C> add(JarContents<C> contents) {
		List<C> c1 = classContents;
		List<C> c2 = contents.classContents;

		List<JarResource> r1 = resourceContents;
		List<JarResource> r2 = contents.resourceContents;

		List<C> c3 = new ArrayList<C>(c1.size() + c2.size());
		c3.addAll(c1);
		c3.addAll(c2);

		List<JarResource> r3 = new ArrayList<JarResource>(r1.size() + r2.size());
		r3.addAll(r1);
		r3.addAll(r2);

		return new JarContents<C>(new ClassNodeContainer<>(c3), new ResourceContainer(r3));
	}

	public static class ClassNodeContainer<C extends ClassNode> extends DataContainer<C> {
		private static final long serialVersionUID = -6169578803641192235L;

		private Map<String, C> lastMap = new HashMap<String, C>();
		private boolean invalidated;

		public ClassNodeContainer() {
			this(16);
		}

		public ClassNodeContainer(int cap) {
			super(cap);
		}

		public ClassNodeContainer(Collection<C> data) {
			super(data);
		}

		@Override
		public boolean add(C c) {
			invalidated = true;
			return super.add(c);
		}

		@Override
		public boolean addAll(Collection<? extends C> c) {
			invalidated = true;
			return super.addAll(c);
		}

		@Override
		public boolean remove(Object c) {
			invalidated = true;
			return super.remove(c);
		}

		@Override
		public Map<String, C> namedMap() {
			if (invalidated) {
				invalidated = false;
				Map<String, C> nodeMap = new HashMap<String, C>();
				Iterator<C> it = iterator();
				while (it.hasNext()) {
					C cn = it.next();
					if (nodeMap.containsKey(cn.name)) {
						it.remove();
					} else {
						nodeMap.put(cn.name, cn);
					}
				}
				lastMap = nodeMap;
			}
			return lastMap;
		}
	}

	public static class ResourceContainer extends DataContainer<JarResource> {
		private static final long serialVersionUID = -6169578803641192235L;

		public ResourceContainer() {
			this(16);
		}

		public ResourceContainer(int cap) {
			super(cap);
		}

		public ResourceContainer(List<JarResource> data) {
			addAll(data);
		}

		@Override
		public Map<String, JarResource> namedMap() {
			Map<String, JarResource> map = new HashMap<String, JarResource>();
			for (JarResource resource : this) {
				map.put(resource.getName(), resource);
			}
			return map;
		}
	}
}