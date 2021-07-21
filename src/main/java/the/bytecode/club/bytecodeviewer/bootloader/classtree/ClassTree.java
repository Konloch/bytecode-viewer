package the.bytecode.club.bytecodeviewer.bootloader.classtree;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import the.bytecode.club.bytecodeviewer.bootloader.classtree.nullpermablehashmap.NullPermeableHashMap;
import the.bytecode.club.bytecodeviewer.bootloader.classtree.nullpermablehashmap.SetCreator;

import static the.bytecode.club.bytecodeviewer.bootloader.classtree.ClassHelper.convertToMap;
import static the.bytecode.club.bytecodeviewer.bootloader.classtree.ClassHelper.copyOf;

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
 * @created 25 May 2015 (actually before this)
 */
public class ClassTree {
    private static final SetCreator<ClassNode> SET_CREATOR = new SetCreator<>();

    private final Map<String, ClassNode> classes;
    private final NullPermeableHashMap<ClassNode, Set<ClassNode>> supers;
    private final NullPermeableHashMap<ClassNode, Set<ClassNode>> delgates;

    public ClassTree() {
        classes = new HashMap<>();
        supers = new NullPermeableHashMap<>(SET_CREATOR);
        delgates = new NullPermeableHashMap<>(SET_CREATOR);
    }

    public ClassTree(Collection<ClassNode> classes) {
        this(convertToMap(classes));
    }

    public ClassTree(Map<String, ClassNode> classes_) {
        classes = copyOf(classes_);
        supers = new NullPermeableHashMap<>(SET_CREATOR);
        delgates = new NullPermeableHashMap<>(SET_CREATOR);

        build(classes);
    }

    // TODO: optimise
    public void build(Map<String, ClassNode> classes) {
        for (ClassNode node : classes.values()) {
            for (String iface : node.interfaces) {
                ClassNode ifacecs = classes.get(iface);
                if (ifacecs == null)
                    continue;

                getDelegates0(ifacecs).add(node);

                Set<ClassNode> superinterfaces = new HashSet<>();
                buildSubTree(classes, superinterfaces, ifacecs);

                getSupers0(node).addAll(superinterfaces);
            }
            ClassNode currentSuper = classes.get(node.superName);
            while (currentSuper != null) {
                getDelegates0(currentSuper).add(node);
                getSupers0(node).add(currentSuper);
                for (String iface : currentSuper.interfaces) {
                    ClassNode ifacecs = classes.get(iface);
                    if (ifacecs == null)
                        continue;
                    getDelegates0(ifacecs).add(currentSuper);
                    Set<ClassNode> superinterfaces = new HashSet<>();
                    buildSubTree(classes, superinterfaces, ifacecs);
                    getSupers0(currentSuper).addAll(superinterfaces);
                    getSupers0(node).addAll(superinterfaces);
                }
                currentSuper = classes.get(currentSuper.superName);
            }

            getSupers0(node);
            getDelegates0(node);
        }
    }

    public void build(ClassNode node) {
        for (String iface : node.interfaces) {
            ClassNode ifacecs = classes.get(iface);
            if (ifacecs == null)
                continue;

            getDelegates0(ifacecs).add(node);

            Set<ClassNode> superinterfaces = new HashSet<>();
            buildSubTree(classes, superinterfaces, ifacecs);

            getSupers0(node).addAll(superinterfaces);
        }
        ClassNode currentSuper = classes.get(node.superName);
        while (currentSuper != null) {
            getDelegates0(currentSuper).add(node);
            getSupers0(node).add(currentSuper);
            for (String iface : currentSuper.interfaces) {
                ClassNode ifacecs = classes.get(iface);
                if (ifacecs == null)
                    continue;
                getDelegates0(ifacecs).add(currentSuper);
                Set<ClassNode> superinterfaces = new HashSet<>();
                buildSubTree(classes, superinterfaces, ifacecs);
                getSupers0(currentSuper).addAll(superinterfaces);
                getSupers0(node).addAll(superinterfaces);
            }
            currentSuper = classes.get(currentSuper.superName);
        }

        getSupers0(node);
        getDelegates0(node);

        classes.put(node.name, node);
    }

    private void buildSubTree(Map<String, ClassNode> classes, Collection<ClassNode> superinterfaces,
                              ClassNode current) {
        superinterfaces.add(current);
        for (String iface : current.interfaces) {
            ClassNode cs = classes.get(iface);
            if (cs != null) {
                getDelegates0(cs).add(current);
                buildSubTree(classes, superinterfaces, cs);
            } /*else {
                System.out.println("Null interface -> " + iface);
            }*/
        }
    }

    public Set<MethodNode> getMethodsFromSuper(ClassNode node, String name, String desc) {
        Set<MethodNode> methods = new HashSet<>();
        for (ClassNode super_ : getSupers(node)) {
            for (MethodNode mn : super_.methods) {
                if (mn.name.equals(name) && mn.desc.equals(desc)) {
                    methods.add(mn);
                }
            }
        }
        return methods;
    }

    public Set<MethodNode> getMethodsFromDelegates(ClassNode node, String name, String desc) {
        Set<MethodNode> methods = new HashSet<>();
        for (ClassNode delegate : getDelegates(node)) {
            for (MethodNode mn : delegate.methods) {
                if (mn.name.equals(name) && mn.desc.equals(desc)) {
                    methods.add(mn);
                }
            }
        }
        return methods;
    }

    public MethodNode getFirstMethodFromSuper(ClassNode node, String name, String desc) {
        for (ClassNode super_ : getSupers(node)) {
            for (MethodNode mn : super_.methods) {
                if (mn.name.equals(name) && mn.desc.equals(desc)) {
                    return mn;
                }
            }
        }
        return null;
    }

    public ClassNode getClass(String name) {
        return classes.get(name);
    }

    public boolean isInherited(ClassNode cn, String name, String desc) {
        return getFirstMethodFromSuper(cn, name, desc) != null;
    }

    private Set<ClassNode> getSupers0(ClassNode cn) {
        return supers.getNonNull(cn);
    }

    private Set<ClassNode> getDelegates0(ClassNode cn) {
        return delgates.getNonNull(cn);
    }

    public Map<String, ClassNode> getClasses() {
        return classes;
    }

    public Set<ClassNode> getSupers(ClassNode cn) {
        return Collections.unmodifiableSet(supers.get(cn));
        // return supers.get(cn);
    }

    public Set<ClassNode> getDelegates(ClassNode cn) {
        return Collections.unmodifiableSet(delgates.get(cn));
        // return delgates.get(cn);
    }
}