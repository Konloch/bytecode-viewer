package the.bytecode.club.bytecodeviewer.bootloader.loader;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.bootloader.classtree.ClassTree;
import the.bytecode.club.bytecodeviewer.bootloader.resource.external.ExternalResource;
import the.bytecode.club.bytecodeviewer.bootloader.resource.jar.contents.JarContents;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * @author Bibl (don't ban me pls)
 * @created 19 Jul 2015 02:48:41
 *
 *         TODO: Resource loading
 */
@Deprecated
public class LibraryClassLoader extends ClassLoader implements ILoader<JarContents<ClassNode>> {

    private final Set<JarContents<ClassNode>> binded;
    private final Map<String, Class<?>> classCache;
    private final ClassTree tree;

    public LibraryClassLoader() {
        binded = new HashSet<>();
        classCache = new HashMap<>();
        tree = new ClassTree();
    }

    /* (non-Javadoc)
     * @see the.bytecode.club.bytecodeviewer.loadermodel.ILoader#bind(the.bytecode.club.bytecodeviewer.loadermodel
     * .ExternalResource)
     */
    @Override
    public void bind(ExternalResource<JarContents<ClassNode>> resource) {
        try {
            JarContents<ClassNode> contents = resource.load();
            if (contents != null) {
                binded.add(contents);
                tree.build(contents.getClassContents().namedMap());
            } else {
                System.err.println("Null contents?");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see the.bytecode.club.bytecodeviewer.loadermodel.ILoader#loadClass(java.lang.String)
     */
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException, NoClassDefFoundError {
        String byte_name = name.replace(".", "/");
        if (classCache.containsKey(byte_name))
            return classCache.get(byte_name);

        ClassNode cn = null;
        for (JarContents<ClassNode> contents : binded) {
            cn = contents.getClassContents().namedMap().get(byte_name);
            if (cn != null)
                break;
        }

        if (cn != null) {
            Class<?> klass = define(cn);
            if (klass != null) {
                classCache.put(byte_name, klass);
                return klass;
            }
        }

        return super.loadClass(name);
    }

    protected Class<?> define(ClassNode cn) {
        ClassWriter writer = new ResolvingClassWriter(tree);
        cn.accept(cn);
        byte[] bytes = writer.toByteArray();
        return defineClass(bytes, 0, bytes.length);
    }

    public static class ResolvingClassWriter extends ClassWriter {

        private final ClassTree classTree;

        public ResolvingClassWriter(ClassTree classTree) {
            super(ClassWriter.COMPUTE_FRAMES);
            this.classTree = classTree;
        }

        @Deprecated
        void update(Map<String, ClassNode> classes) {
            classTree.build(classes);
        }

        @Override
        protected String getCommonSuperClass(final String type1, final String type2) {
            ClassNode ccn = classTree.getClass(type1);
            ClassNode dcn = classTree.getClass(type2);

            //System.out.println(type1 + " " + type2);
            if (ccn == null) {
                classTree.build(createQuick(type1));
                return getCommonSuperClass(type1, type2);
            }

            if (dcn == null) {
                classTree.build(createQuick(type2));
                return getCommonSuperClass(type1, type2);
            }

            Set<ClassNode> c = classTree.getSupers(ccn);
            Set<ClassNode> d = classTree.getSupers(dcn);

            if (c.contains(dcn))
                return type1;

            if (d.contains(ccn))
                return type2;

            if (Modifier.isInterface(ccn.access) || Modifier.isInterface(dcn.access)) {
                return "java/lang/Object";
            } else {
                do {
                    ClassNode nccn = classTree.getClass(ccn.superName);
                    if (nccn == null)
                        break;
                    ccn = nccn;
                    c = classTree.getSupers(ccn);
                } while (!c.contains(dcn));
                return ccn.name;
            }
        }

        public ClassNode createQuick(String name) {
            try {
                ClassReader cr = new ClassReader(name);
                ClassNode cn = new ClassNode();
                cr.accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
                return cn;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
