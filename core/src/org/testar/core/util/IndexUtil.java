/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.util;

import java.util.Objects;

import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.state.Widget;

public class IndexUtil {

    private IndexUtil() {
    }

    /**
     * Calculate the max and the min ZIndex of all the widgets in a state
     * 
     * @param state
     */
    public static State calculateZIndices(State state) {
        Objects.requireNonNull(state, "State cannot be null");
        double minZIndex = Double.MAX_VALUE;
        double maxZIndex = Double.MIN_VALUE;
        for (Widget w : state) {
            double zindex = w.get(Tags.ZIndex, 0.0).doubleValue();
            if (zindex < minZIndex) {
                minZIndex = zindex;
            }
            if (zindex > maxZIndex) {
                maxZIndex = zindex;
            }
        }
        state.set(Tags.MinZIndex, minZIndex);
        state.set(Tags.MaxZIndex, maxZIndex);
        return state;
    }
}
