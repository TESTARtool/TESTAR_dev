package org.testar.statemodel.qlearning;

import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;

public class QLearningTransition {
  private AbstractState sourceAbstractState;
  private String sourceConcreteStateId;
  private AbstractState targetAbstractState;
  private String targetConcreteStateId;
  private AbstractAction abstractAction;
  private String concreteActionId;
  private double reward = 0;

  public QLearningTransition(AbstractState sourceAbstractState,
                             String sourceConcreteStateId,
                             AbstractState targetAbstractState,
                             String targetConcreteStateId,
                             AbstractAction abstractAction,
                             String concreteActionId) {
    this.sourceAbstractState = sourceAbstractState;
    this.sourceConcreteStateId = sourceConcreteStateId;
    this.targetAbstractState = targetAbstractState;
    this.targetConcreteStateId = targetConcreteStateId;
    this.abstractAction = abstractAction;
    this.concreteActionId = concreteActionId;
  }

  public AbstractState getSourceAbstractState() {
    return sourceAbstractState;
  }

  public String getSourceConcreteStateId() {
    return sourceConcreteStateId;
  }

  public AbstractState getTargetAbstractState() {
    return targetAbstractState;
  }

  public String getTargetConcreteStateId() {
    return targetConcreteStateId;
  }

  public AbstractAction getAbstractAction() {
    return abstractAction;
  }

  public String getConcreteActionId() {
    return concreteActionId;
  }

  public void setReward(double reward) {
    this.reward = reward;
  }

  public double getReward() {
    return reward;
  }
}
