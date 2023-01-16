package org.testar.statemodel.qlearning;

import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.ConcreteAction;
import org.testar.statemodel.ConcreteState;
import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.statemodel.event.StateModelEventType;
import org.testar.statemodel.exceptions.StateModelException;

import java.util.*;
import java.util.stream.Collectors;

public class QLearningModel {

  private AbstractStateModel stateModel;

  private String targetActionId;

  private Map<String, QLearningNode> nodesByStateIds;
  private Map<String, QLearningEdge> edgesByActionIds;

  // a set of event listeners
  private Set<StateModelEventListener> eventListeners;

  // are we emitting events or not?
  private boolean emitEvents = true;

  private int iterationCount = 0;

  //TODO get from settings if needed
  private double qlAlpha = 0.5;
  private double qlGamma = 9.5;
  private double qlActionReward = -1.0;
  private double qlMinReward = -999.99;

  public QLearningModel(//String modelIdentifier,
                        //String targetActionId,
                        StateModelEventListener... eventListeners) {
//    this.modelIdentifier = modelIdentifier;
//    this.targetActionId = targetActionId;
    this.nodesByStateIds = new HashMap<>();
    this.edgesByActionIds = new HashMap<>();
    this.eventListeners = Arrays.stream(eventListeners).collect(Collectors.toSet());

    initModel();
  }

  /**
   * initialization code for the state model should go in this method
   */
  private void initModel() {
    // add code here to initialize the model, such as loading a model from disk/database/external storage
    emitEvent(new StateModelEvent(StateModelEventType.QLEARNING_MODEL_INITIALIZED, this));
  }

  /**
   * Notify our listeners of emitted events
   * @param event
   */
  private void emitEvent(StateModelEvent event) {
    if (!emitEvents) return;
    for (StateModelEventListener eventListener: eventListeners) {
      eventListener.eventReceived(event);
    }
  }

  public void addTransition(QLearningTransition transition) {
    String sourceConcreteStateId = transition.getSourceConcreteStateId();
    String targetConcreteStateId = transition.getTargetConcreteStateId();
    QLearningNode sourceNode, targetNode;
    if (!nodesByStateIds.keySet().contains(sourceConcreteStateId)) {
      sourceNode = new QLearningNode(transition.getSourceAbstractState(), sourceConcreteStateId);
      nodesByStateIds.put(sourceConcreteStateId, sourceNode);
    }
    else {
      sourceNode = nodesByStateIds.get(sourceConcreteStateId);
    }
    if (!nodesByStateIds.keySet().contains(targetConcreteStateId)) {
      targetNode = new QLearningNode(transition.getTargetAbstractState(), targetConcreteStateId);
      nodesByStateIds.put(targetConcreteStateId, targetNode);
    }
    else {
      targetNode = nodesByStateIds.get(targetConcreteStateId);
    }

    QLearningEdge edge = sourceNode.addChild(targetNode, transition.getAbstractAction(), transition.getConcreteActionId());
    edge.setReward(transition.getReward());
    edgesByActionIds.put(transition.getConcreteActionId(), edge);
  }

//  public String getModelIdentifier() {
//    return modelIdentifier;
//  }


  public AbstractStateModel getStateModel() {
    return stateModel;
  }

  public void setStateModel(AbstractStateModel stateModel) {
    this.stateModel = stateModel;
  }

  public String getTargetActionId() {
    return targetActionId;
  }

  public int getIterationCount() {
    return iterationCount;
  }

  public Set<QLearningReward> getRewards() {
    return edgesByActionIds.entrySet().stream().map(entry -> new QLearningReward(stateModel.getModelIdentifier(),
        entry.getValue().getConcreteActionId(),
        targetActionId, entry.getValue().getReward()))
      .collect(Collectors.toSet());
  }

  public void iterate() {
    Set<QLearningNode> processedVertices = new HashSet<>();
    Collection<QLearningEdge> selectedEdges = new HashSet<>();
    QLearningEdge targetEdge = edgesByActionIds.get(targetActionId);
    targetEdge.setReward(0);
    selectedEdges.add(edgesByActionIds.get(targetActionId));
    Map<QLearningNode, QLearningEdge> currentFront = new HashMap<>();
    while (true) {
      for (QLearningEdge selectedEdge: selectedEdges) {
        QLearningNode selectedNode = selectedEdge.getSourceNode();
        double selectedReward = selectedEdge.getReward();
        if (!processedVertices.contains(selectedNode) &&
          (!currentFront.containsKey(selectedNode) || currentFront.get(selectedNode).getReward() < selectedReward)) {
          currentFront.put(selectedNode, selectedEdge);
        }
      }

      if (currentFront.size() == 0) {
        break;
      }

      selectedEdges = currentFront.entrySet().stream().flatMap(entry -> {
        QLearningNode selectedNode = entry.getKey();
        double maxReward = selectedNode.outgoingEdges.values().stream().mapToDouble(QLearningEdge::getReward)
          .filter(Objects::nonNull).max().orElse(qlMinReward);
        double newReward = qlActionReward + qlGamma * maxReward;

        Collection<QLearningEdge> incomingEdges = entry.getKey().getIncomingEdges().values();
        for (QLearningEdge incomingEdge: incomingEdges) {
          Double oldReward = incomingEdge.getReward();
          incomingEdge.setReward(oldReward == null ? newReward : (1.0 - qlAlpha) * oldReward + qlAlpha * newReward);
        }
        return incomingEdges.stream();
      }).collect(Collectors.toSet());
    }

    processedVertices.addAll(currentFront.keySet());
    currentFront.clear();
    iterationCount++;
  }
}
