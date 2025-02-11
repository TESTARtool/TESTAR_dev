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
import org.testar.statemodel.ConcreteAction;
import org.testar.statemodel.ConcreteState;
import org.testar.statemodel.exceptions.HydrationException;
import org.testar.statemodel.persistence.orientdb.entity.*;
import org.testar.statemodel.sequence.SequenceStep;
import org.testar.statemodel.util.HydrationHelper;

import java.util.Date;

public class SequenceStepHydrator implements EntityHydrator<EdgeEntity> {

    @Override
    public void hydrate(EdgeEntity edgeEntity, Object source) throws HydrationException {
        if (!(source instanceof SequenceStep)) {
            throw new HydrationException();
        }

        // first make sure the identity property is set
        Property identifier = edgeEntity.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException();
        }

        // the edge between two classes needs an identifier to make sure we do not create unnecessary double edges
        // we combine the ids from the source and target for this purpose
        Property sourceIdentifier = edgeEntity.getSourceEntity().getEntityClass().getIdentifier();
        String sourceId = (String)edgeEntity.getSourceEntity().getPropertyValue(sourceIdentifier.getPropertyName()).getValue();
        Property targetIdentifier = edgeEntity.getTargetEntity().getEntityClass().getIdentifier();
        String targetId = (String)edgeEntity.getTargetEntity().getPropertyValue(targetIdentifier.getPropertyName()).getValue();

        String edgeId = sourceId + "-" + targetId;
        // make sure the java and orientdb property types are compatible
        OType identifierType = TypeConvertor.getInstance().getOrientDBType(edgeId.getClass());
        if (identifierType != identifier.getPropertyType()) {
            throw new HydrationException();
        }
        edgeEntity.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), edgeId));

        // add the timestamp
        Date date = new Date(((SequenceStep) source).getTimestamp().toEpochMilli());
        edgeEntity.addPropertyValue("timestamp", new PropertyValue(OType.DATETIME, date));

        ConcreteState concreteStateSource = ((SequenceStep) source).getSourceNode().getConcreteState();
        ConcreteState concreteStateTarget = ((SequenceStep) source).getTargetNode().getConcreteState();
        ConcreteAction concreteAction = ((SequenceStep) source).getConcreteAction();

        // in order to obtain the unique identifier for the concrete state, we need to hydrate them.
        // create entities for the target and source states
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.ConcreteState);
        VertexEntity sourceVertexEntity = new VertexEntity(entityClass);
        VertexEntity targetVertexEntity = new VertexEntity(entityClass);

        try {
            EntityHydrator stateHydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_CONCRETE_STATE);
            stateHydrator.hydrate(sourceVertexEntity, concreteStateSource);
            stateHydrator.hydrate(targetVertexEntity, concreteStateTarget);
        } catch (HydrationException e) {
            //@todo add some meaningful logging here
            return;
        }

        Property ConcreteStateIdentifier = entityClass.getIdentifier();
        String concreteStateSourceId = (String)sourceVertexEntity.getPropertyValue(ConcreteStateIdentifier.getPropertyName()).getValue();
        String concreteStateTargetId = (String)targetVertexEntity.getPropertyValue(ConcreteStateIdentifier.getPropertyName()).getValue();

        // add the concrete action id
        edgeEntity.addPropertyValue("concreteActionId", new PropertyValue(OType.STRING, concreteAction.getActionId()));

        // construct the unique action id
        String concreteActionUid = HydrationHelper.createOrientDbActionId(concreteStateSourceId, concreteStateTargetId, concreteAction.getActionId(), null);
        edgeEntity.addPropertyValue("concreteActionUid", new PropertyValue(OType.STRING, concreteActionUid));

        // add the description of the action performed
        edgeEntity.addPropertyValue("actionDescription", new PropertyValue(OType.STRING, ((SequenceStep) source).getActionDescription()));

        // add whether the step introduced non-determinism
        edgeEntity.addPropertyValue("nonDeterministic", new PropertyValue(OType.BOOLEAN, ((SequenceStep) source).isNonDeterministic()));
    }
}
