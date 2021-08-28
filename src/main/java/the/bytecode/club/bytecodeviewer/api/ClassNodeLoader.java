package the.bytecode.club.bytecodeviewer.api;

import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

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
 * @author Demmonic
 */

public final class ClassNodeLoader extends ClassLoader
{
    private final Map<String, ClassNode> classes = new HashMap<>();

    /**
     * Adds the provided class node to the class loader
     *
     * @param cn The class
     */
    public void addClass(ClassNode cn) {
        classes.put(cn.name.replace("/", "."), cn);
    }

    /**
     * @param name The name of the class
     * @return If this class loader contains the provided class node
     */
    public boolean contains(String name) {
        return (classes.get(name) != null);
    }

    /**
     * @return All class nodes in this loader
     */
    public Collection<ClassNode> getAll() {
        return classes.values();
    }

    /**
     * Clears out all class nodes
     */
    public void clear() {
        classes.clear();
    }

    /**
     * @return All classes in this loader
     */
    public Collection<Class<?>> getAllClasses() {
        List<Class<?>> classes = new ArrayList<>();
        for (String s : this.classes.keySet()) {
            try {
                classes.add(loadClass(s));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return classes;
    }

    /**
     * @param name The name of the class
     * @return The class node with the provided name
     */
    public ClassNode get(String name) {
        return classes.get(name);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return findClass(className);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        if (classes.containsKey(name)) {
            return nodeToClass(classes.get(name));
        } else {
            return super.loadClass(name);
        }
    }

    /**
     * Converts a class node to a class
     *
     * @param node The node to convert
     * @return The converted class
     */
    public Class<?> nodeToClass(ClassNode node)
    {
        if (super.findLoadedClass(node.name.replace("/", ".")) != null)
            return findLoadedClass(node.name.replace("/", "."));
       
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        try {
            node.accept(cw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        byte[] b = cw.toByteArray();
        return defineClass(node.name.replaceAll("/", "."), b, 0, b.length,
                getDomain());
    }

    /**
     * @return This class loader's protection domain
     */
    private ProtectionDomain getDomain() {
        CodeSource code = new CodeSource(null, (Certificate[]) null);
        return new ProtectionDomain(code, getPermissions());
    }

    /**
     * @return This class loader's permissions
     */
    private Permissions getPermissions() {
        Permissions permissions = new Permissions();
        permissions.add(new AllPermission());
        return permissions;
    }
}
