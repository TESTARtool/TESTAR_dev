/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog.tagsvisualization;

import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.webdriver.tag.WdTags;
import org.testar.windows.tag.UIATags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DefaultTagFilter {

    private static final List<Tag<?>> allAvailableTags;
    static{
        // Fill the list with all the available tags.
        List<Tag<?>> tmpList = new ArrayList<>(UIATags.getAllTags());
        tmpList.addAll(Tags.getAllTags());
        tmpList.addAll(WdTags.getAllTags());
        allAvailableTags = Collections.unmodifiableList(tmpList);
    }
    public static List<Tag<?>> getSet() {
        return allAvailableTags;
    }

    private static Tag<?> findTagByName(String name, Set<Tag<?>> tagSet) {
        return tagSet.stream()
                .filter(tag -> tag.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static Tag<?> findTagByName(String name) {
        Tag<?> tag = findTagByName(name, UIATags.getAllTags());
        if (tag == null) {
            tag = findTagByName(name, Tags.getAllTags());
        }
        if (tag == null) {
            tag = findTagByName(name, WdTags.getAllTags());
        }
        return tag;
    }

}
