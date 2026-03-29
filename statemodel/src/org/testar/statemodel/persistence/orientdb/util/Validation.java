/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.persistence.orientdb.util;

public abstract class Validation {

    /**
     * This method replaces characters that are not legal in OrientDB for use in attribute names.
     * @param attributeName
     * @return
     */
    public static String sanitizeAttributeName(String attributeName) {
        attributeName = attributeName.replaceAll(":", "_c_");
        attributeName = attributeName.replaceAll(",", "_k_");
        attributeName = attributeName.replaceAll("\\s", "_s_");
        return attributeName;
    }

}
