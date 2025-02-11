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

package org.testar.statemodel.persistence.orientdb.hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.exceptions.HydrationException;
import org.testar.statemodel.persistence.orientdb.entity.Property;
import org.testar.statemodel.persistence.orientdb.entity.PropertyValue;
import org.testar.statemodel.persistence.orientdb.entity.TypeConvertor;
import org.testar.statemodel.persistence.orientdb.entity.VertexEntity;
import org.testar.statemodel.util.HydrationHelper;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TaggableBase;

public class AbstractStateHydrator implements EntityHydrator<VertexEntity> {

    @Override
    public void hydrate(VertexEntity target, Object source) throws HydrationException {
        if (!(source instanceof AbstractState)) {
            throw new HydrationException();
        }

        // first make sure the identity property is set
        Property identifier = target.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException("No identifying properties were provided for entity class " + target.getEntityClass().getClassName());
        }

        // add the two identifying properties
        String stateId = null;
        String modelIdentifier = null;
        for (Property prop : target.getEntityClass().getProperties()) {
            if (prop.getPropertyName().equals("stateId")) {
                OType propertyType = TypeConvertor.getInstance().getOrientDBType(((AbstractState) source).getStateId().getClass());
                if (propertyType != prop.getPropertyType()) {
                    throw new HydrationException();
                }
                target.addPropertyValue(prop.getPropertyName(), new PropertyValue(prop.getPropertyType(), ((AbstractState) source).getStateId()));
                stateId = ((AbstractState) source).getStateId();
            }
            else if (prop.getPropertyName().equals("modelIdentifier")) {
                OType propertyType = TypeConvertor.getInstance().getOrientDBType(((AbstractState) source).getModelIdentifier().getClass());
                if (propertyType != prop.getPropertyType()) {
                    throw new HydrationException();
                }
                target.addPropertyValue(prop.getPropertyName(), new PropertyValue(prop.getPropertyType(), ((AbstractState) source).getModelIdentifier()));
                modelIdentifier = ((AbstractState) source).getModelIdentifier();
            }
        }

        // we need to combine the stateId and the abstractionLevelidentifier into a unique id
        if (stateId == null || modelIdentifier == null) {
            throw new HydrationException("Missing identity property in abstract state hydrator");
        }
        String uniqueId = HydrationHelper.lowCollisionID(modelIdentifier + "--" + stateId);
        target.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), uniqueId));

        // loop through the tagged attributes for this state and add them
        TaggableBase attributes = ((AbstractState) source).getAttributes();
        for (Tag<?> tag :attributes.tags()) {
            // we simply add a property for each tag
            target.addPropertyValue(tag.name(), new PropertyValue(TypeConvertor.getInstance().getOrientDBType(attributes.get(tag).getClass()), attributes.get(tag)));
        }

        // is it an initial, meaning starting, state?
        Property isInitial = HydrationHelper.getProperty(target.getEntityClass().getProperties(), "isInitial");
        if (isInitial == null) {
            throw new HydrationException("Property `isInitial` was not found in abstract state class");
        }
        target.addPropertyValue(isInitial.getPropertyName(), new PropertyValue(isInitial.getPropertyType(), ((AbstractState) source).isInitial()));

        // we need to add the concrete state ids
        Property concreteStateIds = HydrationHelper.getProperty(target.getEntityClass().getProperties(), "concreteStateIds");
        if (concreteStateIds == null) {
            throw new HydrationException();
        }
        if (!((AbstractState) source).getConcreteStateIds().isEmpty()) {
            target.addPropertyValue(concreteStateIds.getPropertyName(), new PropertyValue(concreteStateIds.getPropertyType(), ((AbstractState) source).getConcreteStateIds()));
        }

    }


}
