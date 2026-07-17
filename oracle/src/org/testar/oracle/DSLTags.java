/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.testar.core.tag.Tag;
import org.testar.core.tag.TagsBase;

public class DSLTags extends TagsBase {

    protected static final Set<Tag<?>> dslTags = new HashSet<>();

    private DSLTags() {
    }

    public static final Tag<String> DSLTableHeaderText = from("DSLTableHeaderText", String.class);

    public static Set<Tag<?>> getAllTags() {
        return tagSet;
    }

    protected static <T> Tag<T> from(String name, Class<T> valueType) {
        Tag<T> tag = TagsBase.from(name, valueType);
        dslTags.add(tag);
        return tag;
    }

    public static Set<Tag<?>> getDSLTags() {
        return Collections.unmodifiableSet(dslTags);
    }
}
