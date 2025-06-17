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

package org.testar.statemodel.persistence.orientdb.extractor;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.AbstractStateTransition;
import org.testar.statemodel.exceptions.ActionNotFoundException;
import org.testar.statemodel.exceptions.ExtractionException;
import org.testar.statemodel.exceptions.StateModelException;
import org.testar.statemodel.persistence.orientdb.entity.DocumentEntity;
import org.testar.statemodel.persistence.orientdb.entity.EdgeEntity;
import org.testar.statemodel.persistence.orientdb.entity.PropertyValue;
import org.testar.statemodel.persistence.orientdb.entity.VertexEntity;

public class AbstractStateTransitionExtractor implements EntityExtractor<AbstractStateTransition> {

    private AbstractStateExtractor abstractStateExtractor;

    private AbstractActionExtractor abstractActionExtractor;

    public AbstractStateTransitionExtractor(AbstractStateExtractor abstractStateExtractor, AbstractActionExtractor abstractActionExtractor) {
        this.abstractStateExtractor = abstractStateExtractor;
        this.abstractActionExtractor = abstractActionExtractor;
    }

    @Override
    public AbstractStateTransition extract(DocumentEntity entity, AbstractStateModel abstractStateModel) throws ExtractionException {
        if (!(entity instanceof EdgeEntity)) {
            throw new ExtractionException("Abstract action extractor expects an edge entity. Instance of " + entity.getClass().toString() + " was given.");
        }
        if (!entity.getEntityClass().getClassName().equals("AbstractAction")) {
            throw new ExtractionException("Entity of class AbstractAction or UnvisitedAbstractAction expected. Class " + entity.getEntityClass().getClassName() + " given.");
        }

        VertexEntity sourceEntity = ((EdgeEntity) entity).getSourceEntity();
        VertexEntity targetEntity = ((EdgeEntity) entity).getTargetEntity();

        // first we get the source and target entities, and we verify that they are of class AbstractState
        if (!(sourceEntity instanceof VertexEntity)) {
            throw new ExtractionException("Abstract state extractor expects a vertex entity for the source. Instance of " + entity.getClass().toString() + " was given.");
        }
        if (!sourceEntity.getEntityClass().getClassName().equals("AbstractState")) {
            throw new ExtractionException("Entity of class AbstractState expected for the source. Class " + entity.getEntityClass().getClassName() + " given.");
        }

        // first we get the source and target entities, and we verify that they are of class AbstractState
        if (!(targetEntity instanceof VertexEntity)) {
            throw new ExtractionException("Abstract state extractor expects a vertex entity for the target. Instance of " + entity.getClass().toString() + " was given.");
        }
        if (!targetEntity.getEntityClass().getClassName().equals("AbstractState")) {
            throw new ExtractionException("Entity of class AbstractState expected for the target. Class " + entity.getEntityClass().getClassName() + " given.");
        }

        // we look up the states in the statemodel.
        // if for some reason we cannot find them, we extract them.

        //source:
        // to create an abstract state, we need an abstract state id and a set of abstract actions
        // first, the id
        PropertyValue propertyValue = sourceEntity.getPropertyValue("stateId");
        if (propertyValue.getType() != OType.STRING) {
            throw new ExtractionException("Expected string value for stateId attribute for the source. Type " + propertyValue.getType().toString() + " given.");
        }
        String abstractStateId = propertyValue.getValue().toString();
        if (abstractStateId == null) {
            throw new ExtractionException("AbstractStateId for the source has a null value.");
        }

        AbstractState abstractSourceState;
        if (abstractStateModel.containsState(abstractStateId)) {
            try {
                abstractSourceState = abstractStateModel.getState(abstractStateId);
            }
            catch (StateModelException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Could not retrieve source state from statemodel");
            }
        }
        else {
            abstractSourceState = abstractStateExtractor.extract(sourceEntity, abstractStateModel);
        }

        //target:
        // to create an abstract state, we need an abstract state id and a set of abstract actions
        // first, the id
        propertyValue = targetEntity.getPropertyValue("stateId");
        if (propertyValue.getType() != OType.STRING) {
            throw new ExtractionException("Expected string value for stateId attribute for the target. Type " + propertyValue.getType().toString() + " given.");
        }
        abstractStateId = propertyValue.getValue().toString();
        if (abstractStateId == null) {
            throw new ExtractionException("AbstractStateId for the target has a null value.");
        }

        AbstractState abstractTargetState;
        if (abstractStateModel.containsState(abstractStateId)) {
            try {
                abstractTargetState = abstractStateModel.getState(abstractStateId);
            }
            catch (StateModelException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Could not retrieve source state from statemodel");
            }
        }
        else {
            abstractTargetState = abstractStateExtractor.extract(sourceEntity, abstractStateModel);
        }

        // action:
        // get the action id
        propertyValue = entity.getPropertyValue("actionId");
        if (propertyValue.getType() != OType.STRING) {
            throw new ExtractionException("Expected string value for actionId attribute. Type " + propertyValue.getType().toString() + " given.");
        }
        String actionId = propertyValue.getValue().toString();
        if (actionId == null) {
            throw new ExtractionException("ActionId for the abstract action has a null value");
        }

        // look for the action on the source state. It should be there, but if not, we extract it
        AbstractAction abstractAction;
        try {
            abstractAction = abstractSourceState.getAction(actionId);
        }
        catch (ActionNotFoundException ex) {
            abstractAction = abstractActionExtractor.extract(entity, abstractStateModel);
        }

        // now that we have the 3 main ingredients, let's assemble the transition and return it
        return new AbstractStateTransition(abstractSourceState, abstractTargetState, abstractAction);
    }
}
