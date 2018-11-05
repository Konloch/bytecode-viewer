package org.objectweb.asm.commons.cfg.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Tree<E extends Tree<E>> extends CopyOnWriteArrayList<E> {

    protected Tree<E> parent;

    public Tree() {
        super();
    }

    public Tree(Collection<? extends E> collection) {
        super(collection);
    }

    public void addFirst(E e) {
        Collection<E> list = new ArrayList<>();
        for (E element : this) {
            list.add(element);
        }
        clear();
        e.parent = this;
        add(e);
        addAll(list);
    }

    public void set(E predecessor, E successor) {
        Iterator<E> it = parent.iterator();
        Collection<E> es = new LinkedList<>();
        while (it.hasNext()) {
            E e = it.next();
            if (e.equals(predecessor)) {
                es.add(successor);
            } else {
                es.add(e);
            }
        }
        parent.clear();
        parent.addAll(es);
    }

    @SuppressWarnings("unchecked")
    public E parent() {
        return (E) parent;
    }

    public boolean hasParent() {
        return parent() != null;
    }

    public E previous() {
        Tree<E> p = parent;
        if (p == null) {
            return null;
        }
        Iterator<E> it = parent.iterator();
        E prev = null;
        while (it.hasNext()) {
            E e = it.next();
            if (e.equals(this)) {
                return prev;
            }
            prev = e;
        }
        return null;
    }

    public boolean hasPrevious() {
        return previous() != null;
    }

    public E next() {
        Tree<E> p = parent;
        if (p == null) {
            return null;
        }
        Iterator<E> it = parent.iterator();
        while (it.hasNext()) {
            E e = it.next();
            if (e.equals(this)) {
                return it.hasNext() ? it.next() : null;
            }
        }
        return null;
    }

    public boolean hasNext() {
        return next() != null;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (E e : this) {
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        }
        return hashCode;
    }
}