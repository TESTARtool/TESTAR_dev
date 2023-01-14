package org.testar.statemodel.qlearning;

import org.testar.monkey.alayer.Action;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.ConcreteAction;

public class QLearningEdge {
  private AbstractAction abstractAction;
  private String concreteActionId;
  private double reward = 0;
  private QLearningNode sourceNode;
  private QLearningNode targetNode;

  public QLearningEdge(QLearningNode sourceNode, QLearningNode targetNode, AbstractAction abstractAction, String concreteActionId) {
    this.abstractAction = abstractAction;
    this.concreteActionId = concreteActionId;
    this.sourceNode = sourceNode;
    this.targetNode = targetNode;
  }

  public AbstractAction getAbstractAction() {
    return abstractAction;
  }

  public String getConcreteActionId() {
    return concreteActionId;
  }

  public QLearningNode getSourceNode() {
    return sourceNode;
  }

  public QLearningNode getTargetNode() {
    return targetNode;
  }

  public Double getReward() {
    return reward;
  }

  public void setReward(double reward) {
    this.reward = reward;
  }

  public void detach() {
    sourceNode.getOutgoingEdges().remove(concreteActionId);
    targetNode.getIncomingEdges().remove(concreteActionId);

    //TODO: check for orphans
  }
}
