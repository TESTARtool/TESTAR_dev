package nl.ou.testar.StateModel.Persistence.OrientDB.Extractor;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.AbstractStateModel;
import nl.ou.testar.StateModel.Exception.ExtractionException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.*;
import org.fruit.Pair;

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
        PropertyValue concreteStateIdValues = ((VertexEntity) entity).getPropertyValue("concreteStateIds");
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
