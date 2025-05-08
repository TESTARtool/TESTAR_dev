/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.settings.dialog;

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
