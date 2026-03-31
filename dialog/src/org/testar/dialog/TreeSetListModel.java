/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog;

import javax.swing.*;
import java.util.*;

public class TreeSetListModel<T> extends AbstractListModel<T> {
    private static final long serialVersionUID = 5179138078302927167L;
    private TreeSet<T> treeSet;

    public TreeSetListModel(Comparator<? super T> comparator) {
        treeSet = new TreeSet<T>(comparator);
    }

    public Set<T> asSet() {
        return treeSet;
    }

    public void addAll(Collection<? extends T> elements) {
        boolean changed = false;
        for (T element : elements) {
            if (treeSet.add(element)) {
                changed = true;
            }
        }
        if (changed) {
            fireContentsChanged(this, 0, getSize());
        }
    }

    public boolean contains(T t) {
        return treeSet.contains(t);
    }

    @Override
    public T getElementAt(int index) {
        if (index < 0 || index >= getSize()) {
            String s = "index, " + index + ", is out of bounds for getSize() = "
                    + getSize();
            throw new IllegalArgumentException(s);
        }
        Iterator<T> iterator = treeSet.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            T t = (T) iterator.next();
            if (index == count) {
                return t;
            }
            count++;
        }
        // out of index. return null. will probably never reach this
        return null;
    }

    @Override
    public int getSize() {
        return treeSet.size();
    }

    public int getIndexOf(T t) {
        int index = 0;
        for (T treeItem : treeSet) {
            if (treeItem.equals(t)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public boolean add(T t) {
        boolean result = treeSet.add(t);
        if (result) {
            int index = getIndexOf(t);
            fireIntervalAdded(this, index, index + 1);
        }
        return result;
    }

    public boolean remove(T t) {
        int index = getIndexOf(t);
        if (index < 0) {
            return false;
        }
        boolean result = treeSet.remove(t);
        fireIntervalRemoved(this, index, index + 1);
        return result;
    }

    public void clear() {
        treeSet.clear();
    }
}
