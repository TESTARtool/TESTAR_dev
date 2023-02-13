package org.testar.statemodel.persistence.orientdb.extractor;

import org.testar.statemodel.ConcreteAction;
import org.testar.statemodel.ConcreteStateTransition;
import org.testar.statemodel.IConcreteAction;
import org.testar.statemodel.IConcreteState;
import org.testar.statemodel.exceptions.ActionNotFoundException;
import org.testar.statemodel.exceptions.ExtractionException;
import org.testar.statemodel.exceptions.StateModelException;
import org.testar.statemodel.persistence.orientdb.entity.DocumentEntity;
import org.testar.statemodel.persistence.orientdb.entity.EdgeEntity;
import org.testar.statemodel.persistence.orientdb.entity.PropertyValue;
import org.testar.statemodel.persistence.orientdb.entity.VertexEntity;
import org.testar.statemodel.util.ExtendedStateModel;

import com.orientechnologies.orient.core.metadata.schema.OType;

public class ConcreteStateTransitionExtractor implements EntityExtractor<ConcreteStateTransition, ExtendedStateModel> {

    private ConcreteStateProxyExtractor concreteStateExtractor;
    private ConcreteActionProxyExtractor concreteActionExtractor;

    public ConcreteStateTransitionExtractor(ConcreteStateProxyExtractor concreteStateExtractor, ConcreteActionProxyExtractor concreteActionExtractor) {
        this.concreteStateExtractor = concreteStateExtractor;
        this.concreteActionExtractor = concreteActionExtractor;
    }

    @Override
    public ConcreteStateTransition extract(DocumentEntity entity, ExtendedStateModel model) throws ExtractionException {
        if (!(entity instanceof EdgeEntity)) { 
            throw new ExtractionException("Concrete state transition extractor expects an edge entity. Instance of " + entity.getClass().toString() + " was given.");
        }
        if (!entity.getEntityClass().getClassName().equals("ConcreteAction")) {
            throw new ExtractionException("Entity of class ConcreteAction expected. Class " + entity.getEntityClass().getClassName() + " given.");
        }

        VertexEntity sourceEntity = ((EdgeEntity) entity).getSourceEntity();
        VertexEntity targetEntity = ((EdgeEntity) entity).getTargetEntity();

        // first we get the source and target entities, and we verify that they are of class AbstractState
        if (!(sourceEntity instanceof VertexEntity)) {
            throw new ExtractionException("Concrete state extractor expects a vertex entity for the source. Instance of " + entity.getClass().toString() + " was given.");
        }
        if (!sourceEntity.getEntityClass().getClassName().equals("ConcreteState")) {
            throw new ExtractionException("Entity of class ConcreteState expected for the source. Class " + entity.getEntityClass().getClassName() + " given.");
        }

        // first we get the source and target entities, and we verify that they are of class AbstractState
        if (!(targetEntity instanceof VertexEntity)) {
            throw new ExtractionException("Concrete state extractor expects a vertex entity for the target. Instance of " + entity.getClass().toString() + " was given.");
        }
        if (!targetEntity.getEntityClass().getClassName().equals("ConcreteState")) {
            throw new ExtractionException("Entity of class ConcreteState expected for the target. Class " + entity.getEntityClass().getClassName() + " given.");
        }

        // we look up the states in the statemodel.
        // if for some reason we cannot find them, we extract them.

        //source:
        // to create a concrete state proxy object, we need a concrete state id and an abstract state
        // first, the id
        PropertyValue propertyValue = sourceEntity.getPropertyValue("stateId");
        if (propertyValue.getType() != OType.STRING) {
            throw new ExtractionException("Expected string value for stateId attribute for the source. Type " + propertyValue.getType().toString() + " given.");
        }
        String concreteStateId = propertyValue.getValue().toString();
        if (concreteStateId == null) {
            throw new ExtractionException("ConcreteStateId for the source has a null value.");
        }

        IConcreteState concreteSourceState;
        if (model.containsConcreteState(concreteStateId)) {
            try {
                concreteSourceState = model.getConcreteState(concreteStateId);
            }
            catch (StateModelException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Could not retrieve source state from statemodel");
            }
        }
        else {
            concreteSourceState = concreteStateExtractor.extract(sourceEntity, model);
        }

        //target:
        // to create a concrete state proxy object, we need a concrete state id and an abstract state
        // first, the id
        propertyValue = targetEntity.getPropertyValue("stateId");
        if (propertyValue.getType() != OType.STRING) {
            throw new ExtractionException("Expected string value for stateId attribute for the target. Type " + propertyValue.getType().toString() + " given.");
        }
        concreteStateId = propertyValue.getValue().toString();
        if (concreteStateId == null) {
            throw new ExtractionException("ConcreteStateId for the target has a null value.");
        }

        IConcreteState concreteTargetState;
        if (model.containsConcreteState(concreteStateId)) {
            try {
                concreteTargetState = model.getConcreteState(concreteStateId);
            }
            catch (StateModelException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Could not retrieve target state from statemodel");
            }
        }
        else {
            concreteTargetState = concreteStateExtractor.extract(sourceEntity, model);
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
        IConcreteAction concreteAction = model.getConcreteAction(actionId);
        if (concreteAction == null) {
            concreteAction = concreteActionExtractor.extract(entity, model);
        }

        // now that we have the 3 main ingredients, let's assemble the transition and return it
        return new ConcreteStateTransition(concreteSourceState, concreteTargetState, concreteAction);
    }
    
}
