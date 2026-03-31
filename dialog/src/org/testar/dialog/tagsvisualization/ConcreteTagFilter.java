/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog.tagsvisualization;

import org.testar.core.tag.Tag;

import java.util.Set;

public class ConcreteTagFilter implements ITagFilter {
    public Set<Tag<?>> filter;

    public void setFilter(Set<Tag<?>> newFilter) {
        filter = newFilter;
    }

    public boolean visualizeTag(Tag<?> t)
    {
        if(filter==null){
            return false;
        }
        return filter.contains(t);
    }
}
