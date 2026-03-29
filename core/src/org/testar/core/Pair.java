/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core;

import java.io.Serializable;

import org.testar.core.util.Util;

public class Pair<L, R> implements Serializable {
    private static final long serialVersionUID = 6777608823096421544L;
    private final L left;
    private final R right;
    
    public static <L, R> Pair<L, R> from(L left, R right) {
        return new Pair<L, R>(left, right);
    }

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }
    
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (o instanceof Pair) {
            Pair<?, ?> po = (Pair<?, ?>) o;
            return Util.equals(left, po.left()) &&
                    Util.equals(right, po.right());
        }
        
        return false;
    }
    
    public int hashCode() {
        return Util.hashCode(left) + Util.hashCode(right);
    }
    
    public String toString() {
        return "(" + Util.toString(left) + ", " + Util.toString(right) + ")";
    }
    
    public L left() {
        return left;
    }

    public R right() {
        return right;
    }
}
