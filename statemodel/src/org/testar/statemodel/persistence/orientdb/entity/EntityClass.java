/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.persistence.orientdb.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EntityClass {

    public enum EntityType { Vertex, Edge }

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

        this.entityType = Objects.requireNonNull(entityType, "EntityType cannot be null");
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
        for (Property property : properties) {
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
