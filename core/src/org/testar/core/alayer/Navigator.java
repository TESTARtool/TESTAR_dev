/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.alayer;

import java.util.LinkedList;
import java.util.function.Consumer;

import org.testar.core.state.Widget;

public interface Navigator extends Consumer<LinkedList<Widget>> {
    @Override
    void accept(LinkedList<Widget> buffer);
}
