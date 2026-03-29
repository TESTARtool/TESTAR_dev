/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.persistence.orientdb.entity;

import com.orientechnologies.orient.core.metadata.schema.OType;

import java.util.Objects;

public class PropertyValue {

    // the orientdb type of the property
    private final OType type;

    // the value of the property
    private final Object value;

    /**
     * Constructor
     * @param type
     * @param value
     */
    public PropertyValue(OType type, Object value) {
        this.type = Objects.requireNonNull(type, "OType cannot be null");
        this.value = value;
    }

    public OType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
