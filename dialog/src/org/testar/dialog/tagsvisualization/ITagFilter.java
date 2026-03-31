/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog.tagsvisualization;

import org.testar.core.tag.Tag;
import java.util.Set;

public interface ITagFilter {
    /**
     * Set the new filter containing the tags we are allowed to visualize.
     * @param newFilter The new filter.
     */
    void setFilter(Set<Tag<?>> newFilter);

    /**
     * Check if we may visualize the tag.
     * @param tag The tag we want to visualize.
     * @return True if it's allowed to visualize the tag.
     */
    boolean visualizeTag(Tag<?> tag);
}
