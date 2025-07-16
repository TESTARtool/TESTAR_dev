/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

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
