package nl.ou.testar.StateModel.Persistence.OrientDB;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.*;
import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Event.StateModelEventListener;
import nl.ou.testar.StateModel.Exception.ExtractionException;
import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Exception.InvalidEventException;
import nl.ou.testar.StateModel.Exception.StateModelException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.*;
import nl.ou.testar.StateModel.Persistence.OrientDB.Extractor.EntityExtractor;
import nl.ou.testar.StateModel.Persistence.OrientDB.Extractor.ExtractorFactory;
import nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator.EntityHydrator;
import nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator.HydratorFactory;
import nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator.SequenceHydrator;
import nl.ou.testar.StateModel.Persistence.OrientDB.Util.DependencyHelper;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Sequence.Sequence;
import nl.ou.testar.StateModel.Sequence.SequenceManager;
import nl.ou.testar.StateModel.Sequence.SequenceNode;
import nl.ou.testar.StateModel.Util.EventHelper;
import nl.ou.testar.StateModel.Widget;
import org.fruit.Pair;

import javax.swing.text.html.parser.Entity;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.exit;


public class OrientDBManager implements PersistenceManager, StateModelEventListener {

    /**
     * Helper class for dealing with events
     */
    private EventHelper eventHelper;

    /**
     * Manager class that will handle the OrientDB specific communications with the database
     */
    private EntityManager entityManager;

    /**
     * Is the manager listening to events?
     */
    private boolean listening = true;

    /**
     * A set of orientdb classes that this class needs to operate
     */
    private Set<EntityClassFactory.EntityClassName> entityClassNames = new HashSet<>(Arrays.asList(
            EntityClassFactory.EntityClassName.AbstractAction,
            EntityClassFactory.EntityClassName.AbstractState,
            EntityClassFactory.EntityClassName.AbstractStateModel,
            EntityClassFactory.EntityClassName.Widget,
            EntityClassFactory.EntityClassName.ConcreteState,
            EntityClassFactory.EntityClassName.ConcreteAction,
            EntityClassFactory.EntityClassName.isParentOf,
            EntityClassFactory.EntityClassName.isChildOf,
            EntityClassFactory.EntityClassName.isAbstractedBy,
            EntityClassFactory.EntityClassName.BlackHole,
            EntityClassFactory.EntityClassName.UnvisitedAbstractAction,
            EntityClassFactory.EntityClassName.TestSequence,
            EntityClassFactory.EntityClassName.SequenceNode,
            EntityClassFactory.EntityClassName.Accessed
    ));

    /**
     * Constructor
     * @param eventHelper
     */
    public OrientDBManager(EventHelper eventHelper, EntityManager entityManager) {
        this.eventHelper = eventHelper;
        this.entityManager = entityManager;
        init();
    }

    /**
     * Initialization code goes here.
     */
    private void init() {
        // we need to make sure before operation that the required classes exist.
        HashSet<EntityClass> entityClassSet = new HashSet<>();
        for (EntityClassFactory.EntityClassName className : entityClassNames) {
            EntityClass entityClass = EntityClassFactory.createEntityClass(className);
            entityClassSet.add(EntityClassFactory.createEntityClass(className));
        }
        // make sure the entityclasses are sorted by dependency on super classes first
        for (EntityClass entityClass : DependencyHelper.sortDependenciesForDeletion(entityClassSet)) {
            System.out.println("Creating " + entityClass.getClassName() + " - " + entityClass.getSuperClassName());
            entityManager.createClass(entityClass);
        }
    }

    @Override
    public void persistAbstractStateModel(AbstractStateModel abstractStateModel) {

    }

    @Override
    public void persistAbstractState(AbstractState abstractState) {
        // create an entity to persist to the database
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.AbstractState);
        VertexEntity vertexEntity = new VertexEntity(entityClass);

        // hydrate the entity to a format the orient database can store
        try {
            EntityHydrator hydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_ABSTRACT_STATE);
            hydrator.hydrate(vertexEntity, abstractState);
        } catch (HydrationException e) {
            e.printStackTrace();
            System.out.println("Encountered a problem while saving abstract state with id " + abstractState.getStateId() + " to the orient database");
            return;
        }

        // save the entity!
        entityManager.saveEntity(vertexEntity);

        // deal with the unvisited actions on the states
        persistUnvisitedActions(abstractState, vertexEntity);
    }

    private void persistUnvisitedActions(AbstractState abstractState, VertexEntity vertexEntity) {
        // 1) delete the unvisited actions that are no longer unvisited
        // 2) save the unvisited actions (for newly saved states)

        // step 1:
        Set<AbstractAction> visitedActions = abstractState.getVisitedActions();
        // we need the ids
        Set<Object> visitedActionIds = visitedActions.stream().map(action -> action.getActionId()).collect(Collectors.toSet());
        EntityClass unvisitedActionEntityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.UnvisitedAbstractAction);
        entityManager.deleteEntities(unvisitedActionEntityClass, visitedActionIds);

        // step 2:
        // all unvisited actions go to the black hole vertex!
        EntityClass targetEntityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.BlackHole);
        VertexEntity blackHole = new VertexEntity(targetEntityClass);
        try {
            EntityHydrator blackHoleHydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_BLACKHOLE);
            blackHoleHydrator.hydrate(blackHole, null);
        }
        catch (HydrationException ex) {
            ex.printStackTrace();
            System.out.println("Encountered a problem while hydrating the black hole class for state " + abstractState.getStateId());
            return;
        }

        try {
            EntityHydrator actionHydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_ABSTRACT_ACTION);
            for (AbstractAction unvisitedAction : abstractState.getUnvisitedActions()) {
                EdgeEntity actionEntity = new EdgeEntity(unvisitedActionEntityClass, vertexEntity, blackHole);
                actionHydrator.hydrate(actionEntity, unvisitedAction);
                entityManager.saveEntity(actionEntity);
            }
        }
        catch (HydrationException ex) {
            System.out.println(ex.getMessage());
        }
        catch (NullPointerException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            exit(1);
        }
    }

    @Override
    public void persistAbstractAction(AbstractAction abstractAction) {

    }

    @Override
    public void persistConcreteState(ConcreteState concreteState) {
        // create an entity to persist to the database
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.ConcreteState);
        VertexEntity vertexEntity = new VertexEntity(entityClass);

        // hydrate the entity to a format the orient database can store
        try {
            EntityHydrator hydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_CONCRETE_STATE);
            hydrator.hydrate(vertexEntity, concreteState);
        }
        catch (HydrationException e) {
            e.printStackTrace();
            System.out.println("Encountered a problem while saving concrete state with id " + concreteState.getId() + " to the orient database");
            return;
        }

        // save the entity!
        entityManager.saveEntity(vertexEntity);

        // store the widgettree attached to this concrete state
        persistWidgetTree(concreteState, vertexEntity);

        // optional: if an abstract state is provided, we connect the concrete state to it using an isAbstractedBy relation
        if (concreteState.getAbstractState() == null) {
            return;
        }
        EntityClass targetEntityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.AbstractState);
        VertexEntity targetVertexEntity = new VertexEntity(targetEntityClass);
        // hydrate the entity to a format the orient database can store
        try {
            EntityHydrator hydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_ABSTRACT_STATE);
            hydrator.hydrate(targetVertexEntity, concreteState.getAbstractState());
        } catch (HydrationException e) {
            e.printStackTrace();
            System.out.println("Encountered a problem while saving abstract state with id " + concreteState.getAbstractState().getStateId() + " to the orient database");
            return;
        }

        // create the edge entity and persist it
        EntityClass edgeEntityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.isAbstractedBy);
        EdgeEntity edgeEntity = new EdgeEntity(edgeEntityClass, vertexEntity, targetVertexEntity);

        try {
            EntityHydrator entityHydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_ABSTRACTED_BY);
            entityHydrator.hydrate(edgeEntity, null);
        }
        catch (HydrationException ex) {
            //@todo add some meaningful logging here as well
        }
        entityManager.saveEntity(edgeEntity);
    }

    /**
     * This method will store a widget tree to the orient database.
     * @param widget
     */
    private void persistWidgetTree(Widget widget, VertexEntity widgetEntity) {
        // we assume the root widget of the tree has already been stored, as this will be the concrete state
        // we loop through the child widgets and for each widget, store the widget and then store the needed edges between them
        for (Widget childWidget : widget.getChildren()) {
            VertexEntity childWidgetEntity = persistWidget(childWidget);

            // with the widget saved, we need to also store parent and child relationships between the two widgets
            EntityClass isChildEntityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.isChildOf);
            EntityClass isParentEntityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.isParentOf);
            EdgeEntity isChildEntity = new EdgeEntity(isChildEntityClass, childWidgetEntity, widgetEntity);
            EdgeEntity isParentEntity = new EdgeEntity(isParentEntityClass, widgetEntity, childWidgetEntity);

            // hydrate the entities
            try {
                EntityHydrator hydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_WIDGET_RELATION);
                hydrator.hydrate(isChildEntity, null);
                hydrator.hydrate(isParentEntity, null);
            }
            catch (HydrationException e) {
                e.printStackTrace();
                System.out.println("Encountered a problem while saving the inter-widget relation to the orient database");
                return;
            }
            entityManager.saveEntity(isChildEntity);
            entityManager.saveEntity(isParentEntity);

            // go down the widget tree and do it again
            persistWidgetTree(childWidget, childWidgetEntity);
        }
    }

    /**
     * This method will persist a single widget to the OrientDB data store.
     * @param widget
     * @return
     */
    private VertexEntity persistWidget(Widget widget) {
        // create an entity to persist to the database
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.Widget);
        VertexEntity vertexEntity = new VertexEntity(entityClass);

        // hydrate the widget entity
        try {
            EntityHydrator hydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_WIDGET);
            hydrator.hydrate(vertexEntity, widget);
        }
        catch (HydrationException e) {
            e.printStackTrace();
            System.out.println("Encountered a problem while saving a widget to the orient database");
            return null;
        }

        // save the widget
        entityManager.saveEntity(vertexEntity);
        return vertexEntity;
    }

    @Override
    public void persistAbstractStateTransition(AbstractStateTransition abstractStateTransition) {
        if (abstractStateTransition.getSourceState() == null || abstractStateTransition.getTargetState() == null || abstractStateTransition.getAction() == null) {
            System.out.println("Objects missing in abstract state transition");
            return;
        }

        // persist the source and target states
        persistAbstractState(abstractStateTransition.getSourceState());
        persistAbstractState(abstractStateTransition.getTargetState());

        // create entities for the target and source states
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.AbstractState);
        VertexEntity sourceVertexEntity = new VertexEntity(entityClass);
        VertexEntity targetVertexEntity = new VertexEntity(entityClass);

        // hydrate the entities to a format the orient database can store
        try {
            EntityHydrator stateHydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_ABSTRACT_STATE);
            stateHydrator.hydrate(sourceVertexEntity, abstractStateTransition.getSourceState());
            stateHydrator.hydrate(targetVertexEntity, abstractStateTransition.getTargetState());
        } catch (HydrationException e) {
            //@todo add some meaningful logging here
            return;
        }

        // now we create an action entity that will link our two state entities
        entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.AbstractAction);
        EdgeEntity actionEntity = new EdgeEntity(entityClass, sourceVertexEntity, targetVertexEntity);

        try {
            EntityHydrator actionHydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_ABSTRACT_ACTION);
            actionHydrator.hydrate(actionEntity, abstractStateTransition.getAction());
        }
        catch (HydrationException ex) {
            //@todo add some meaningful logging here as well
        }
        entityManager.saveEntity(actionEntity);
    }

    @Override
    public void persistConcreteStateTransition(ConcreteStateTransition concreteStateTransition) {
        if (concreteStateTransition.getSourceState() == null || concreteStateTransition.getTargetState() == null | concreteStateTransition.getAction() == null) {
            System.out.println("Objects missing in concrete state transition");
            return;
        }

        // persist the source and target states
        persistConcreteState(concreteStateTransition.getSourceState());
        persistConcreteState(concreteStateTransition.getTargetState());

        // create entities for the target and source states
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.ConcreteState);
        VertexEntity sourceVertexEntity = new VertexEntity(entityClass);
        VertexEntity targetVertexEntity = new VertexEntity(entityClass);

        // hydrate the entities to a format the orient database can store
        try {
            EntityHydrator stateHydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_CONCRETE_STATE);
            stateHydrator.hydrate(sourceVertexEntity, concreteStateTransition.getSourceState());
            stateHydrator.hydrate(targetVertexEntity, concreteStateTransition.getTargetState());
        } catch (HydrationException e) {
            //@todo add some meaningful logging here
            return;
        }

        // now we create an action entity that will link our two state entities
        entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.ConcreteAction);
        EdgeEntity actionEntity = new EdgeEntity(entityClass, sourceVertexEntity, targetVertexEntity);

        try {
            EntityHydrator actionHydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_CONCRETE_ACTION);
            actionHydrator.hydrate(actionEntity, concreteStateTransition.getAction());
        }
        catch (HydrationException ex) {
            //@todo add some meaningful logging here as well
        }
        entityManager.saveEntity(actionEntity);
    }

    @Override
    public void initAbstractStateModel(AbstractStateModel abstractStateModel) {
        // there are two options here: either the abstract state model already exists in the database,
        // in which case we load it, or it doesn't exist yet, in which case we save it
        // first, disable the event listener. We do not want to process the events resulting from our object creations
        setListening(false);

        EntityClass stateModelClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.AbstractStateModel);
        VertexEntity stateModelEntity = new VertexEntity(stateModelClass);

        // hydrate the entity with the abstract state model information
        try {
            EntityHydrator stateModelHydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_ABSTRACT_STATE_MODEL);
            stateModelHydrator.hydrate(stateModelEntity, abstractStateModel);
        }
        catch (HydrationException ex) {
            ex.printStackTrace();
        }

        // step 1: persist the state model entity to the database. if it already exists, nothing will happen
        entityManager.saveEntity(stateModelEntity);

        // step 2: see if there are abstract states present in the data store that are tied to this abstract state model
        EntityClass abstractStateClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.AbstractState);
        if (abstractStateClass == null) throw new RuntimeException("Error occurred: could not retrieve an abstract state entity class.");

        // in order to retrieve the abstract states, we need to provide the abstract state model identifier to the query
        Map<String, PropertyValue> entityProperties = new HashMap<>();
        Property stateModelClassIdentifier = stateModelClass.getIdentifier();
        if (stateModelClassIdentifier == null) throw new RuntimeException("Error occurred: abstract state model does not have an id property set.");
        entityProperties.put("abstractionLevelIdentifier", stateModelEntity.getPropertyValue(stateModelClassIdentifier.getPropertyName()));

        Set<DocumentEntity> retrievedDocuments = entityManager.retrieveAllOfClass(abstractStateClass, entityProperties);
        if (retrievedDocuments.isEmpty()) {
            System.out.println("Could not find abstract states in the model");
        }
        else {
            // we need to create the abstract states from the returned document entities
            try {
                EntityExtractor<AbstractState> abstractStateExtractor = ExtractorFactory.getExtractor(ExtractorFactory.EXTRACTOR_ABSTRACT_STATE);
                for (DocumentEntity documentEntity : retrievedDocuments) {
                    AbstractState abstractState = abstractStateExtractor.extract(documentEntity, abstractStateModel);
                    abstractStateModel.addState(abstractState);
                }
            } catch (ExtractionException | StateModelException e) {
                e.printStackTrace();
            }
        }

        // step 3: fetch the transitions from the database
        EntityClass abstractActionClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.AbstractAction);
        if (abstractActionClass == null) throw new RuntimeException("Error occurred: could not retrieve an abstract action entity class");

        retrievedDocuments = entityManager.retrieveAllOfClass(abstractActionClass, entityProperties);
        if (retrievedDocuments.isEmpty()) {
            System.out.println("Could not find abstract actions in the model");
        }
        else {
            System.out.println(retrievedDocuments.size() + " number of abstract actions were returned");
            // we need to create the transitions from the returned document entities
            try {
                EntityExtractor<AbstractStateTransition> abstractStateTransitionEntityExtractor = ExtractorFactory.getExtractor(ExtractorFactory.EXTRACTOR_ABSTRACT_STATE_TRANSITION);
                for (DocumentEntity documentEntity : retrievedDocuments) {
                    AbstractStateTransition abstractStateTransition = abstractStateTransitionEntityExtractor.extract(documentEntity, abstractStateModel);
                    abstractStateModel.addTransition( abstractStateTransition.getSourceState(), abstractStateTransition.getTargetState(), abstractStateTransition.getAction());
                }
            } catch (ExtractionException | StateModelException e) {
                e.printStackTrace();
            }
        }

        // enable the event listener again
        setListening(true);
    }

    public void persistSequence(Sequence sequence) {
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.TestSequence);
        VertexEntity vertexEntity = new VertexEntity(entityClass);

        try {
            EntityHydrator sequenceHydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_SEQUENCE);
            sequenceHydrator.hydrate(vertexEntity, sequence);
        } catch (HydrationException e) {
            e.printStackTrace();
        }

        entityManager.saveEntity(vertexEntity);
    }

    @Override
    public void persistSequenceNode(SequenceNode sequenceNode) {
        // we save the node as an edge, from sequence node to concrete state
        EntityClass nodeClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.SequenceNode);
        VertexEntity nodeEntity = new VertexEntity(nodeClass);

        try {
            EntityHydrator sequenceNodeHydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_SEQUENCE_NODE);
            sequenceNodeHydrator.hydrate(nodeEntity, sequenceNode);
        } catch (HydrationException e) {
            e.printStackTrace();
        }

        // get the concrete state and make a vertex out of it
        EntityClass concreteStateClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.ConcreteState);
        VertexEntity stateEntity = new VertexEntity(concreteStateClass);

        // hydrate the entity to a format the orient database can store
        try {
            EntityHydrator hydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_CONCRETE_STATE);
            hydrator.hydrate(stateEntity, sequenceNode.getConcreteState());
        }
        catch (HydrationException e) {
            e.printStackTrace();
            System.out.println("Encountered a problem while saving concrete state with id " + sequenceNode.getConcreteState().getId() + " to the orient database");
        }

        // we have to add an edge from sequence node to the concrete state it accessed
        EntityClass accessedClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.Accessed);
        EdgeEntity accessedEdge = new EdgeEntity(accessedClass, nodeEntity, stateEntity);
        try {
            EntityHydrator hydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_ACCESSED);
            hydrator.hydrate(accessedEdge, null);
        }
        catch (HydrationException e) {
            e.printStackTrace();
            System.out.println("Encountered a problem while hydrating the accessed relation for sequence node " + sequenceNode.getNodeId());
        }

        entityManager.saveEntity(accessedEdge);
    }

    @Override
    public void initSequenceManager(SequenceManager sequenceManager) {
        // the aim is
    }

    @Override
    public void eventReceived(StateModelEvent event) {
        if (!listening) return;

        try {
            eventHelper.validateEvent(event);
        } catch (InvalidEventException e) {
            // There is something wrong with the event. we do nothing and exit
            System.out.println("Received wrong payload for event: " + event.getPayload().getClass().toString());
            return;
        }

        switch (event.getEventType()) {
            case ABSTRACT_STATE_ADDED:
            case ABSTRACT_STATE_CHANGED:
                persistAbstractState((AbstractState) (event.getPayload()));
                break;

            case ABSTRACT_STATE_TRANSITION_ADDED:
            case ABSTRACT_STATE_TRANSITION_CHANGED:
                //@todo the abstract action changed event needs to just update the action attributes
                persistAbstractStateTransition((AbstractStateTransition) (event.getPayload()));
                break;

            case ABSTRACT_STATE_MODEL_INITIALIZED:
                initAbstractStateModel((AbstractStateModel) (event.getPayload()));
                break;

            case SEQUENCE_STARTED:
                persistSequence((Sequence) event.getPayload());
                break;

            case SEQUENCE_MANAGER_INITIALIZED:
                initSequenceManager((SequenceManager) event.getPayload());
                break;

            case SEQUENCE_NODE_ADDED:

        }

    }

    @Override
    public void setListening(boolean listening) {
        this.listening = listening;
    }
}
