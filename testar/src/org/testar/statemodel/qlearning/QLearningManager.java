package org.testar.statemodel.qlearning;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.testar.monkey.Pair;
import org.testar.statemodel.*;
import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.statemodel.exceptions.HydrationException;
import org.testar.statemodel.exceptions.InvalidEventException;
import org.testar.statemodel.persistence.orientdb.entity.*;
import org.testar.statemodel.persistence.orientdb.hydrator.EntityHydrator;
import org.testar.statemodel.util.EventHelper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.System.exit;

public class QLearningManager implements StateModelEventListener {
  /**
   * Helper class for dealing with events
   */
  private EventHelper eventHelper;

  /**
   * Manager class that will handle the OrientDB specific communications with the database
   */
  private EntityManager entityManager;

  private QLearningModel learningModel = null;

  private String targetActionId = null;

  /**
   * A set of orientdb classes that this class needs to operate
   */
  private Set<EntityClassFactory.EntityClassName> entityClassNames = new HashSet<>(Arrays.asList(
    EntityClassFactory.EntityClassName.ConcreteState,
    EntityClassFactory.EntityClassName.ConcreteAction,
    EntityClassFactory.EntityClassName.QLearningReward
  ));

  public QLearningManager(EventHelper eventHelper, EntityManager entityManager) {
    this.eventHelper = eventHelper;
    this.entityManager = entityManager;
  }

  public String getTargetActionId() {
    return targetActionId;
  }

  public void setTargetActionId(String targetActionId) {
    this.targetActionId = targetActionId;
  }

  private void initQLearningModel(QLearningModel model, AbstractStateModel stateModel) {
    EntityClass rewardClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.QLearningReward);

//    EntityClass abstractActionClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.AbstractAction);
//    Property abstractActionClassIdentifier = abstractActionClass.getIdentifier();
//    if (abstractActionClassIdentifier == null) throw new RuntimeException("Error occurred: abstract action model does not have an id property set.");
//
//    EntityClass abstractStateClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.AbstractState);
//    Property abstractStateClassIdentifier = abstractStateClass.getIdentifier();
//    if (abstractStateClassIdentifier == null) throw new RuntimeException("Error occurred: abstract state does not have an id property set.");

    EntityClass concreteActionClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.ConcreteAction);
    Property concreteActionClassIdentifier = concreteActionClass.getIdentifier();
    if (concreteActionClassIdentifier == null) throw new RuntimeException("Error occurred: concrete action does not have an id property set.");

    Map<String, PropertyValue> entityProperties = new HashMap<>();
    entityProperties.put("modelIdentifier", new PropertyValue(OType.STRING, stateModel.getModelIdentifier()));

    Property rewardClassIdentifier = rewardClass.getIdentifier();
    if (rewardClassIdentifier == null) throw new RuntimeException("Error occurred: reward does not have an id property set.");
    Map<String, PropertyValue> rewardProperties = new HashMap<>();
    rewardProperties.put("modelIdentifier", new PropertyValue(OType.STRING, stateModel.getModelIdentifier()));
    rewardProperties.put("targetActionId", new PropertyValue(OType.STRING, model.getTargetActionId()));

    try (ODatabaseSession db = entityManager.getConnection().getDatabaseSession()) {
      Map<String, DocumentEntity> concreteActionDocuments = entityManager.retrieveAllOfClass(concreteActionClass, entityProperties, db)
        .stream().collect(Collectors.toMap(
          element -> (String) element.getPropertyValue("actionId").getValue(),
          Function.identity()));
      if (concreteActionDocuments.isEmpty()) {
        System.out.println("Could not find concrete actions in the model");
        return;
      }

      Map<String, DocumentEntity> rewardDocuments = entityManager.retrieveAllOfClass(rewardClass, rewardProperties, db)
        .stream().collect(Collectors.toMap(
          element -> (String) element.getPropertyValue("actionId").getValue(),
          Function.identity()));

      Set<AbstractAction> abstractActions = stateModel.getAbstractActions();

      /**
       * Build reversed maps on concrete IDs
       */

        Map<String, AbstractState> abstractStatesByConcreteId = stateModel.getAbstractStates().stream().flatMap(
          state -> state.getConcreteStateIds().stream().map(id -> new Pair<>(id, state))
        ).collect(Collectors.toMap(Pair::left, Pair::right));
        Map<String, AbstractAction> abstractActionsByConcreteId = abstractActions.stream().flatMap(
          action -> action.getConcreteActionIds().stream().map(id -> new Pair<>(id, action))
        ).collect((Collectors.toMap(Pair::left, Pair::right)));

        Map<String, QLearningTransition> transitionsByConcreteActionId = new HashMap<>();

        for (AbstractAction abstractAction: abstractActions) {
          for (String concreteActionId: abstractAction.getConcreteActionIds()) {
            DocumentEntity concreteActionDocument = concreteActionDocuments.get(concreteActionId);

            if (concreteActionDocument == null) {
              System.out.println("Cannot find a concrete action with ID " + concreteActionId);
              continue;
            }

            if (!(concreteActionDocument instanceof EdgeEntity)) {
              System.out.println(String.format("Concrete action %s failure: edge type expected", concreteActionId));
              continue;
            }
            EdgeEntity concreteActionEdge = (EdgeEntity) concreteActionDocument;
            VertexEntity sourceStateVertex = concreteActionEdge.getSourceEntity();
            VertexEntity targetStateVertex = concreteActionEdge.getTargetEntity();

            String sourceStateId = (String) sourceStateVertex.getPropertyValue("stateId").getValue();
            String targetStateId = (String) targetStateVertex.getPropertyValue("stateId").getValue();

            if (!abstractStatesByConcreteId.containsKey(sourceStateId)) {
              System.out.println(String.format("Concrete action % s failure: source concrete state %s not present in the state model", concreteActionId, sourceStateId));
              continue;
            }
            if (!abstractStatesByConcreteId.containsKey(targetStateId)) {
              System.out.println(String.format("Concrete action % s failure: target concrete state %s not present in the state model", concreteActionId, targetStateId));
              continue;
            }

            QLearningTransition transition = new QLearningTransition(
              abstractStatesByConcreteId.get(sourceStateId), sourceStateId,
              abstractStatesByConcreteId.get(targetStateId), targetStateId,
              abstractActionsByConcreteId.get(concreteActionId), concreteActionId
            );

            if (rewardDocuments.containsKey(concreteActionId)) {
              DocumentEntity rewardDocument = rewardDocuments.get(concreteActionId);
              PropertyValue rewardValue = rewardDocument.getPropertyValue("reward");
              if (rewardValue.getType() != OType.DOUBLE) {
                System.out.println(String.format("Concrete action %s failure: double type expected for reward value", concreteActionId));
                continue;
              }
              transition.setReward((Double) rewardValue.getValue());
            }

            model.addTransition(transition);

//            concreteActions.add(new ConcreteAction(concreteActionId, abstractAction));
//            source

//            AbstractState abstractSourceState = abstractStates.get(sourceStateId);
//            if (abstractSourceState == null) {
//              System.out.println(String.format("Concrete action % s failure: source abstract state %s not present in the state model", concreteActionId, sourceStateId)
//            }
          }
        }
//        concreteActionDocuments.stream().map(entity -> {
//          PropertyValue actionIdValue =
//          String concreteActionId = entity.
//          if (!(entity instanceof EdgeEntity)) {
//            System.err.println();
//          }
//        });
//      }
//      catch (ExtractionException e) {
//        e.printStackTrace();
//      }
    }
  }

  private void persistQLearningProgress(QLearningModel model) {
    EntityClass modelClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.QLearningModel);
//    DocumentEntity rewardEntity = new VertexEntity(entityClass)new VertexEntity(entityClass);
    DocumentEntity modelEntity = new DiscreteEntity(modelClass);

    EntityClass rewardClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.QLearningReward);
    Set<DiscreteEntity> rewardEntities = null;

//    try {
//      //TODO: we might need some hydrator factory
//      EntityHydrator modelHydrator = new QLearningModelHydrator();
//      modelHydrator.hydrate(modelEntity, model);
//
//    } catch (HydrationException e) {
//      e.printStackTrace();
//      System.out.println("Encountered a problem while saving Q-Learning model with id " + model.getModelIdentifier() + " to the orient database");
//      return;
//    }

    EntityHydrator rewardHydrator = new QLearningRewardHydrator();
    rewardEntities = model.getRewards().stream().map(rewardObject -> {
      DiscreteEntity rewardEntity = new DiscreteEntity(rewardClass);
      try {
        rewardHydrator.hydrate(rewardEntity, rewardObject);
        return rewardEntity;
      } catch (HydrationException e) {
        System.out.println(String.format("Encountered a problem with saving Q-Learning reward (model id %s, action id %s",
          rewardObject.getModelIdentifier(), rewardObject.getActionId()));
        return null;
      }
    }).filter(Objects::nonNull).collect(Collectors.toSet());


    try (ODatabaseSession db = entityManager.getConnection().getDatabaseSession()) {
      db.begin();
      entityManager.saveEntity(modelEntity, db);
      for (DocumentEntity rewardEntity: rewardEntities) {
        entityManager.saveEntity(rewardEntity, db);
      }
      db.commit();
    } catch (NullPointerException ex) {
      ex.printStackTrace();
      System.out.println(ex.getMessage());
      exit(1);
    }
  }

  @Override
  public void eventReceived(StateModelEvent event) {
//    if (!listening) return;

    try {
      eventHelper.validateEvent(event);
    } catch (InvalidEventException e) {
      // There is something wrong with the event. we do nothing and exit
      System.out.println("Received wrong payload for event: " + event.getPayload().getClass().toString());
      return;
    }

    switch (event.getEventType()) {
//      case ABSTRACT_STATE_MODEL_INITIALIZED: {
//        AbstractStateModel stateModel = (AbstractStateModel) event.getPayload();
//        QLearningModel learningModel = new QLearningModel(stateModel.getModelIdentifier(), targetActionId);
//        break;
//      }
      case ABSTRACT_STATE_MODEL_READY: {
        AbstractStateModel stateModel = (AbstractStateModel) event.getPayload();
        initQLearningModel(learningModel, stateModel);
        break;
      }
      default:
        break;
    }
  }

  @Override
  public void setListening(boolean listening) {
    //TODO
  }
}
