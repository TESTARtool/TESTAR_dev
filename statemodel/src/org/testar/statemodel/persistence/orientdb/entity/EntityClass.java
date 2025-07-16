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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EntityClass {

    public enum EntityType {Vertex, Edge}

    // name of the class
    private final String className;

    // name of the superclass, if any
    private String superClassName;

    // is the entity an edge or a vertex?
    private final EntityType entityType;

    // a set of properties to add to the class
    private final Set<Property> properties;

    /**
     * Constructor
     * @param classname classname for the orientdb class
     */
    public EntityClass(String classname, EntityType entityType) {
        Objects.requireNonNull(classname, "classname cannot be null");
        if (classname.trim().isEmpty()) {
            throw new IllegalArgumentException("classname cannot be empty or blank");
        }
        this.className = classname;

        this.entityType = Objects.requireNonNull(entityType, "EntityType cannot be null");;
        properties = new HashSet<>();
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    /**
     * Adds a property to this class
     * @param property
     */
    public void addProperty(Property property) {
        properties.add(property);
    }

    public String getClassName() {
        return className;
    }

    public Set<Property> getProperties() {
        return properties;
    }

    /**
     * Get the property that is identifying for this state.
     * @return
     */
    public Property getIdentifier() {
        Property returnProperty = null;
        for (Property property:properties) {
            if (property.isIdentifier()) {
                returnProperty = property;
            }
        }
        return returnProperty;
    }

    /**
     * Method returns true if the entityclass represents a vertex.
     * @return
     */
    public boolean isVertex() {
        return this.entityType == EntityType.Vertex;
    }

    /**
     * Method returns true if the entityclass represents an edge.
     * @return
     */
    public boolean isEdge() {
        return this.entityType == EntityType.Edge;
    }
}
