package org.testar.statemodel.qlearning;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.testar.statemodel.exceptions.HydrationException;
import org.testar.statemodel.persistence.orientdb.entity.DiscreteEntity;
import org.testar.statemodel.persistence.orientdb.entity.Property;
import org.testar.statemodel.persistence.orientdb.entity.PropertyValue;
import org.testar.statemodel.persistence.orientdb.hydrator.EntityHydrator;

public class QLearningRewardHydrator implements EntityHydrator<DiscreteEntity> {
  @Override
  public void hydrate(DiscreteEntity target, Object source) throws HydrationException {
    if (!(source instanceof QLearningReward)) {
      throw new HydrationException("Object provided to the reward hydrator has an unexpected type: " + source.getClass().getName());
    }

    Property identifier = target.getEntityClass().getIdentifier();
    if (identifier == null) {
      throw new HydrationException("Missing entity identifier");
    }

    QLearningReward reward = (QLearningReward) source;
    target.addPropertyValue("modelIdentifier", new PropertyValue(OType.STRING, reward.getModelIdentifier()));
    target.addPropertyValue("targetActionId", new PropertyValue(OType.STRING, reward.getTargetActionId()));
    target.addPropertyValue("actionId", new PropertyValue(OType.STRING, reward.getActionId()));
    target.addPropertyValue("reward", new PropertyValue(OType.DOUBLE, reward.getValue()));
  }
}
