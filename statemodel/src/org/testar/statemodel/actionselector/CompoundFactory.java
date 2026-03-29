/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.actionselector;

import org.testar.core.tag.TaggableBase;
import org.testar.config.StateModelTags;

import java.util.ArrayList;
import java.util.List;

public class CompoundFactory {

    public static CompoundActionSelector getCompoundActionSelector(TaggableBase configTags) {
        List<ActionSelector> selectors = new ArrayList<>();
        if (configTags.get(StateModelTags.ActionSelectionAlgorithm).equals("unvisited")) {
            selectors.add(new ImprovedUnvisitedActionSelector());
        }
        selectors.add(new RandomActionSelector());
        return new CompoundActionSelector(selectors);
    }

}
