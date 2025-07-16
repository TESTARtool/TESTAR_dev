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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class DocumentEntity {

    /**
     * The entity class this entity `belongs` to.
     */
    private final EntityClass entityClass;

    /**
     * A map of properties for this entity.
     */
    private final Map<String, PropertyValue> entityProperties;

    /**
     * Should the entity be updated if it already exists?
     */
    private boolean updateEnabled = true;

    public DocumentEntity(EntityClass entityClass) {
        this.entityClass = Objects.requireNonNull(entityClass, "EntityClass cannot be null");
        entityProperties = new HashMap<>();
    }

    public EntityClass getEntityClass() {
        return entityClass;
    }

    /**
     * Add a property to this document entity
     * @param propertyName
     * @param propertyValue
     */
    public void addPropertyValue(String propertyName, PropertyValue propertyValue) {
        entityProperties.put(propertyName, propertyValue);
    }

    /**
     * Get a property and its value from this entity
     * @param propertyName
     * @return
     */
    public PropertyValue getPropertyValue(String propertyName) {
        return entityProperties.getOrDefault(propertyName, null);
    }

    /**
     * Returns a set of names of all the properties connected to this entity
     * @return
     */
    public Set<String> getPropertyNames() {
        return entityProperties.keySet();
    }

    /**
     * Specify whether or not the entity can be updated if it already exists.
     * @param updateEnabled
     */
    public void enableUpdate(boolean updateEnabled) {
        this.updateEnabled = updateEnabled;
    }

    /**
     * This method returns true if the entity can be updated when it already exists in the datastore.
     * @return
     */
    public boolean updateEnabled() {
        return  updateEnabled;
    }

}
