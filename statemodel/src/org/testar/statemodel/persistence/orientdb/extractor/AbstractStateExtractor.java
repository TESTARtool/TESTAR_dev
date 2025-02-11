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
import org.testar.statemodel.exceptions.ExtractionException;
import org.testar.statemodel.persistence.orientdb.entity.*;

import java.util.HashSet;
import java.util.Set;

public class AbstractStateExtractor implements EntityExtractor<AbstractState> {

    private AbstractActionExtractor abstractActionExtractor;

    /**
     * Constructor.
     * @param abstractActionExtractor
     */
    public AbstractStateExtractor(AbstractActionExtractor abstractActionExtractor) {
        this.abstractActionExtractor = abstractActionExtractor;
    }

    @Override
    public AbstractState extract(DocumentEntity entity, AbstractStateModel abstractStateModel) throws ExtractionException {
        if (!(entity instanceof VertexEntity)) {
            throw new ExtractionException("Abstract state extractor expects a vertex entity. Instance of " + entity.getClass().toString() + " was given.");
        }
        if (!entity.getEntityClass().getClassName().equals("AbstractState")) {
            throw new ExtractionException("Entity of class AbstractState expected. Class " + entity.getEntityClass().getClassName() + " given.");
        }

        // to create an abstract state, we need an abstract state id and a set of abstract actions
        // first, the id
        PropertyValue propertyValue = entity.getPropertyValue("stateId");
        if (propertyValue.getType() != OType.STRING) {
            throw new ExtractionException("Expected string value for stateId attribute. Type " + propertyValue.getType().toString() + " given.");
        }
        String abstractStateId = propertyValue.getValue().toString();
        if (abstractStateId == null) {
            throw new ExtractionException("AbstractStateId has a null value.");
        }

        // now, the actions
        Set<AbstractAction> actions = new HashSet<>();
        Set<AbstractAction> unvisitedActions = new HashSet<>();
        for (EdgeEntity edgeEntity : ((VertexEntity) entity).getOutgoingEdges()) {
            // for each edge, we check if the edge is an abstract action or an unvisited abstract action
            EntityClass edgeEntityClass = edgeEntity.getEntityClass();
            if (edgeEntityClass.getClassName().equals("AbstractAction") || edgeEntityClass.getClassName().equals("UnvisitedAbstractAction")) {
                AbstractAction action = abstractActionExtractor.extract(edgeEntity, abstractStateModel);
                actions.add(action);

                if (edgeEntityClass.getClassName().equals("UnvisitedAbstractAction")) {
                    unvisitedActions.add(action);
                }
            }
        }

        // create the abstract state
        AbstractState abstractState = new AbstractState(abstractStateId, actions);

        // is it an initial state?
        propertyValue = entity.getPropertyValue("isInitial");
        if (propertyValue.getType() != OType.BOOLEAN) {
            throw new ExtractionException("Expected boolean value for isInitial attribute. Type " + propertyValue.getType().toString() + " was given.");
        }
        boolean isInitial = (boolean) propertyValue.getValue();
        abstractState.setInitial(isInitial);

        // add the visited abstract actions
        Set<AbstractAction> visitedActions = (HashSet<AbstractAction>)((HashSet<AbstractAction>) actions).clone();
        visitedActions.removeAll(unvisitedActions);
        for (AbstractAction visitedAction : visitedActions) {
            abstractState.addVisitedAction(visitedAction);
        }

        // get the concrete state ids
        PropertyValue concreteStateIdValues = entity.getPropertyValue("concreteStateIds");
        if (concreteStateIdValues.getType() != OType.EMBEDDEDSET) {
            throw new ExtractionException("Embedded set was expected for concrete state ids. " + concreteStateIdValues.getType().toString() + " was given.");
        }
        if (concreteStateIdValues.getValue() != null) {
            if (!Set.class.isAssignableFrom(concreteStateIdValues.getValue().getClass())) {
                throw new ExtractionException("Set expected for value of concrete state ids");
            }
            for(String concreteStateId : (Set<String>)concreteStateIdValues.getValue()) {
                abstractState.addConcreteStateId(concreteStateId);
            }
        }
        return abstractState;
    }
}
