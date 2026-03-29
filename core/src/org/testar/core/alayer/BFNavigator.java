/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.alayer;

import java.util.LinkedList;

import org.testar.core.state.Widget;

public class BFNavigator implements Navigator {
    @Override
    public void accept(LinkedList<Widget> buffer) {
        Widget w = buffer.getFirst();
        for (int i = 0; i < w.childCount(); i++) {
            buffer.add(w.child(i));
        }
    }
}
