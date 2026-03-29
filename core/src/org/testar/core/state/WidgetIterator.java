/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.state;

import java.util.Iterator;
import java.util.LinkedList;

import org.testar.core.Assert;
import org.testar.core.alayer.BFNavigator;
import org.testar.core.alayer.Navigator;

public final class WidgetIterator implements Iterator<Widget> {

    private final LinkedList<Widget> buffer;
    private final Navigator navi;

    public WidgetIterator(Widget start) {
        this(start, new BFNavigator());
    }

    public WidgetIterator(Widget start, Navigator navi) {
        Assert.notNull(start, navi);
        this.buffer = new LinkedList<Widget>();
        this.navi = navi;
        this.buffer.add(start);
        this.navi.accept(this.buffer);
    }

    public boolean hasNext() {
        return !buffer.isEmpty();
    }

    public Widget next() {
        Widget ret = buffer.remove();
        if (!buffer.isEmpty()) {
            navi.accept(buffer);
        }
        return ret;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
