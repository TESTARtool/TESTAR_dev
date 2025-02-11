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
import org.testar.statemodel.exceptions.HydrationException;
import org.testar.statemodel.persistence.orientdb.entity.EdgeEntity;
import org.testar.statemodel.persistence.orientdb.entity.Property;
import org.testar.statemodel.persistence.orientdb.entity.PropertyValue;
import org.testar.statemodel.persistence.orientdb.entity.TypeConvertor;

public class IsAbstractedByHydrator implements EntityHydrator<EdgeEntity> {

    @Override
    public void hydrate(EdgeEntity target, Object source) throws HydrationException {
        // first make sure the identity property is set
        Property identifier = target.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException();
        }

        // the edge between two classes needs an identifier to make sure we do not create unnecessary double edges
        // we combine the ids from the source and target for this purpose
        Property sourceIdentifier = target.getSourceEntity().getEntityClass().getIdentifier();
        String sourceId = (String)target.getSourceEntity().getPropertyValue(sourceIdentifier.getPropertyName()).getValue();
        Property targetIdentifier = target.getTargetEntity().getEntityClass().getIdentifier();
        String targetId = (String)target.getTargetEntity().getPropertyValue(targetIdentifier.getPropertyName()).getValue();

        String edgeId = sourceId + "-" + targetId;
        // make sure the java and orientdb property types are compatible
        OType identifierType = TypeConvertor.getInstance().getOrientDBType(edgeId.getClass());
        if (identifierType != identifier.getPropertyType()) {
            throw new HydrationException();
        }
        target.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), edgeId));
    }
}
