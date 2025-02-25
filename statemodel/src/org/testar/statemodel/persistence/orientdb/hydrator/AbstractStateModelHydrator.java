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
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.exceptions.HydrationException;
import org.testar.statemodel.persistence.orientdb.entity.Property;
import org.testar.statemodel.persistence.orientdb.entity.PropertyValue;
import org.testar.statemodel.persistence.orientdb.entity.TypeConvertor;
import org.testar.statemodel.persistence.orientdb.entity.VertexEntity;
import org.testar.statemodel.util.HydrationHelper;
import org.testar.monkey.alayer.Tag;

import java.util.HashSet;

public class AbstractStateModelHydrator implements EntityHydrator<VertexEntity> {

    @Override
    public void hydrate(VertexEntity vertexEntity, Object source) throws HydrationException {
        if (!(source instanceof AbstractStateModel)) {
            throw new HydrationException("Invalid source object was provided to AbstractStateModel hydrator");
        }

        // first make sure the identity property is set
        Property identifier = vertexEntity.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException();
        }

        // make sure the java and orientdb property types are compatible
        OType identifierType = TypeConvertor.getInstance().getOrientDBType(((AbstractStateModel) source).getModelIdentifier().getClass());
        if (identifierType != identifier.getPropertyType()) {
            throw new HydrationException();
        }
        vertexEntity.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), ((AbstractStateModel) source).getModelIdentifier()));

        // add the tags that were used in the creation of the unique states of the state model
        Property abstractionAttributes = HydrationHelper.getProperty(vertexEntity.getEntityClass().getProperties(), "abstractionAttributes");
        if (abstractionAttributes == null) {
            throw new HydrationException();
        }
        HashSet<String> attributeSet = new HashSet<>();
        for (Tag<?> tag : ((AbstractStateModel) source).getTags()) {
            attributeSet.add(tag.name());
        }
        vertexEntity.addPropertyValue(abstractionAttributes.getPropertyName(), new PropertyValue(abstractionAttributes.getPropertyType(), attributeSet));

        // add the application name and version
        vertexEntity.addPropertyValue("applicationName", new PropertyValue(OType.STRING, ((AbstractStateModel) source).getApplicationName()));
        vertexEntity.addPropertyValue("applicationVersion", new PropertyValue(OType.STRING, ((AbstractStateModel) source).getApplicationVersion()));
    }
}
