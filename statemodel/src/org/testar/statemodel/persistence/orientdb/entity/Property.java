/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.persistence.orientdb.entity;

import java.util.Objects;

import com.orientechnologies.orient.core.metadata.schema.OType;

public class Property {

    // name of the property
    private final String propertyName;

    // orientdb type to use for the property
    private final OType propertyType;

    // needed child type in case of embedded list, sets, maps
    private final OType childType;

    private boolean isMandatory = false;

    private boolean isReadOnly = false;

    private boolean isNullable = true;

    // is this property the identifying property for a given class?
    private boolean identifier = false;

    // should the property be regarded as an autoincrement value?
    private boolean autoIncrement = false;

    // is this property indexable?
    private boolean indexAble = false;

    /**
     * Constructor
     * @param propertyName
     * @param propertyType
     */
    public Property(String propertyName, OType propertyType) {
        this(propertyName, propertyType, null);
    }

    /**
     * Constructor
     * @param propertyName
     * @param propertyType
     */
    public Property(String propertyName, OType propertyType, OType childType) {
        Objects.requireNonNull(propertyName, "propertyName cannot be null");
        if (propertyName.trim().isEmpty()) {
            throw new IllegalArgumentException("propertyName cannot be empty or blank");
        }
        this.propertyName = propertyName;

        this.propertyType = Objects.requireNonNull(propertyType, "propertyType cannot be null");
        this.childType = childType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public OType getPropertyType() {
        return propertyType;
    }

    public OType getChildType() {
        return childType;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }

    public boolean isIdentifier() {
        return identifier;
    }

    public void setIdentifier(boolean identifier) {
        this.identifier = identifier;
    }

    public boolean isIndexAble() {
        return indexAble;
    }

    public void setIndexAble(boolean indexAble) {
        this.indexAble = indexAble;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }
}
