package nl.ou.testar.StateModel.Persistence.OrientDB.Extractor;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.Exception.ExtractionException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.DocumentEntity;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EdgeEntity;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EntityClass;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.VertexEntity;
import org.fruit.Pair;

import java.util.HashSet;
import java.util.Set;

public class AbstractStateExtractor implements EntityExtractor<AbstractState> {

    @Override
    public AbstractState extract(DocumentEntity entity) throws ExtractionException {
        if (!(entity instanceof VertexEntity)) {
            throw new ExtractionException("Abstract state extractor expects a vertex entity. Instance of " + entity.getClass().toString() + " was given.");
        }
        if (!entity.getEntityClass().getClassName().equals("AbstractState")) {
            throw new ExtractionException("Entity of class AbstractState expected. Class " + entity.getEntityClass().getClassName() + " given.");
        }

        // to create an abstract state, we need an abstract state id and a set of abstract actions
        // first, the id
        Pair<OType, Object> propertyValue = entity.getPropertyValue("stateId");
        if (propertyValue.left() != OType.STRING) {
            throw new ExtractionException("Expected string value for stateId attribute. Type " + propertyValue.left().toString() + " given.");
        }
        String abstractStateId = propertyValue.right().toString();
        if (abstractStateId == null) {
            throw new ExtractionException("AbstractStateId has a null value.");
        }

        // now, the actions
        Set<AbstractAction> actions = new HashSet<>();
        Set<AbstractAction> unvisitedActions = new HashSet<>();
        for (EdgeEntity edgeEntity : ((VertexEntity) entity).getOutgoingEdges()) {
            // for each edge, we check if the edge is an abstract action or an unvisited abstract action
            EntityClass edgeEntityClass = edgeEntity.getEntityClass();
            if (edgeEntityClass.getClassName().equals("AbstractAction") || edgeEntityClass.getClassName().equals("UnvisitedAction")) {
                AbstractAction action = processEdge(edgeEntity);
                actions.add(action);

                if (edgeEntityClass.getClassName().equals("UnvisitedAction")) {
                    unvisitedActions.add(action);
                }
            }
        }

        // create the abstract state
        AbstractState abstractState = new AbstractState(abstractStateId, actions);

        // is it an initial state?
        propertyValue = entity.getPropertyValue("isInitial");
        if (propertyValue.left() != OType.BOOLEAN) {
            throw new ExtractionException("Expected boolean value for isInitial attribute. Type " + propertyValue.left().toString() + " was given.");
        }
        boolean isInitial = (boolean) propertyValue.right();
        abstractState.setInitial(isInitial);

        // add the visited abstract actions
        Set<AbstractAction> visitedActions = (HashSet<AbstractAction>)((HashSet<AbstractAction>) actions).clone();
        visitedActions.removeAll(unvisitedActions);
        for (AbstractAction visitedAction : visitedActions) {
            abstractState.addVisitedAction(visitedAction);
        }

        // get the concrete state ids
        Pair<OType, Object> concreteStateIdValues = entity.getPropertyValue("concreteStateIds");
        if (concreteStateIdValues.left() != OType.EMBEDDEDSET) {
            throw new ExtractionException("Embedded set was expected for concrete state ids. " + concreteStateIdValues.left().toString() + " was given.");
        }
        if (concreteStateIdValues.right() != null) {
            if (!Set.class.isAssignableFrom(concreteStateIdValues.right().getClass())) {
                throw new ExtractionException("Set expected for value of concrete state ids");
            }
            for(String concreteStateId : (Set<String>)concreteStateIdValues.right()) {
                abstractState.addConcreteStateId(concreteStateId);
            }
        }
        return abstractState;
    }

    private AbstractAction processEdge(EdgeEntity edgeEntity) throws ExtractionException {
        // get the action id
        Pair<OType, Object> propertyValue;
        propertyValue = edgeEntity.getPropertyValue("actionId");
        if (propertyValue.left() != OType.STRING) {
            throw new ExtractionException("Expected string value for actionId attribute. Type " + propertyValue.left().toString() + " given.");
        }
        String actionId = propertyValue.right().toString();
        AbstractAction action = new AbstractAction(actionId);

        // get the concrete action id's
        Pair<OType, Object> concreteActionIdValues = edgeEntity.getPropertyValue("concreteActionIds");
        if (concreteActionIdValues == null) {
            return null;
        }
        if (concreteActionIdValues.left() != OType.EMBEDDEDSET) {
            throw new ExtractionException("Embedded set was expected for concrete action ids. " + concreteActionIdValues.left().toString() + " was given.");
        }
        if (!Set.class.isAssignableFrom(concreteActionIdValues.right().getClass())) {
            throw new ExtractionException("Set expected for value of concrete action ids");
        }
        Set<String> concreteActionIds = (Set<String>)concreteActionIdValues.right();
        for (String concreteActionId : concreteActionIds) {
            action.addConcreteActionId(concreteActionId);
        }
        return action;
    }
}
