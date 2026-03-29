/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.tag;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class TagsBase {

    protected static final Set<Tag<?>> tagSet = new HashSet<>();

    protected static <T> Tag<T> from(String name, Class<T> valueType) {
        Tag<T> ret = Tag.from(name, valueType);
        tagSet.add(ret);
        return ret;
    }

    public static Set<Tag<?>> tagSet() {
        return Collections.unmodifiableSet(tagSet);
    }
}
