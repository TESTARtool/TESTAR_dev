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
import org.testar.statemodel.persistence.orientdb.entity.Property;
import org.testar.statemodel.persistence.orientdb.entity.PropertyValue;
import org.testar.statemodel.persistence.orientdb.entity.VertexEntity;
import org.testar.statemodel.sequence.SequenceNode;

import java.util.Date;

public class SequenceNodeHydrator implements EntityHydrator<VertexEntity> {

    @Override
    public void hydrate(VertexEntity entity, Object source) throws HydrationException {
        if (!(source instanceof SequenceNode)) {
            throw new HydrationException();
        }

        Property identifier = entity.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException("Entityclass did not have a valid identifier property");
        }
        entity.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), ((SequenceNode) source).getNodeId()));

        // add the nodenr and the timestamp
        Date date = new Date(((SequenceNode) source).getTimestamp().toEpochMilli());
        entity.addPropertyValue("timestamp", new PropertyValue(OType.DATETIME, date));
        entity.addPropertyValue("nodeNr", new PropertyValue(OType.INTEGER, ((SequenceNode) source).getNodeNr()));
        entity.addPropertyValue("concreteStateId", new PropertyValue(OType.STRING, ((SequenceNode) source).getConcreteState().getId()));
        entity.addPropertyValue("sequenceId", new PropertyValue(OType.STRING, ((SequenceNode) source).getSequenceId()));

        // check for error messages
        entity.addPropertyValue("containsErrors", new PropertyValue(OType.BOOLEAN, ((SequenceNode) source).containsErrors()));
        if (((SequenceNode) source).containsErrors()) {
            entity.addPropertyValue("errorMessage", new PropertyValue(OType.STRING, ((SequenceNode) source).getErrorMessage()));
        }
    }
}
