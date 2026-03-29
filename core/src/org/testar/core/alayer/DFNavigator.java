/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.alayer;

import java.util.LinkedList;

import org.testar.core.state.Widget;

public class DFNavigator implements Navigator {
    @Override
    public void accept(LinkedList<Widget> buffer) {
        Widget f = buffer.getFirst();
        for (int i = f.childCount() - 1; i >= 0; i--) {
            buffer.add(1, f.child(i));
        }
    }
}
