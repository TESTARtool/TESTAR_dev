package org.testar.statemodel.qlearning;

import org.testar.statemodel.AbstractEntity;
import org.testar.statemodel.persistence.Persistable;

public class QLearningReward {

  private String modelIdentifier;
  private String actionId;
  private String targetActionId;
  private Double value; //could be null

  public QLearningReward(String modelIdentifier, String actionId, String targetActionId, double value) {
    this.modelIdentifier = modelIdentifier;
    this.actionId = actionId;
    this.targetActionId = targetActionId;
    this.value = value;
  }

  public String getModelIdentifier() {
    return modelIdentifier;
  }

  public String getActionId() {
    return actionId;
  }

  public String getTargetActionId() {
    return targetActionId;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }
}
