/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.stub;

import java.util.Iterator;

import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.state.WidgetIterator;

public class StateStub extends WidgetStub implements State {

    private static final long serialVersionUID = -2972642849689796355L;

    public StateStub() {
        setRoot(this);
    }

    public void setRoot(State root) {
        super.setRoot(root);
    }

    @Override
    public Iterator<Widget> iterator() {
        return new WidgetIterator(this);
    }
}
