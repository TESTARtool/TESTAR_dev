/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog.tagsvisualization;

public class TagFilter {
    private static ITagFilter instance;

    /**
     * Get the TagFilter interface.
     * @return The TagFilter interface.
     */
    public static ITagFilter getInstance() {
        return instance;
    }

    /**
     * Sets the actual implementation of the interface.
     * @param implementation The concrete implementation of the interface.
     */
    public static void setInstance(ITagFilter implementation) {
        instance = implementation;
    }
}