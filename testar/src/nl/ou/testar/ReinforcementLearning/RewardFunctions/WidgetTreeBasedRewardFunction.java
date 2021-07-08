package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import nl.ou.testar.StateModel.Widget;
import org.apache.commons.math3.analysis.function.Divide;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import java.util.HashMap;
import java.util.Map;

public class WidgetTreeBasedRewardFunction implements RewardFunction {

    Map<Tag<?>, Object>  attributesInPreviousState = new HashMap<>();

    /**
     * Gets the reward by dividing the number of new actions in a state by the total number of actions
     *
     * @param currentConcreteState The {@link ConcreteState} the SUT is in
     * @param currentAbstractState The {@link AbstractState} the SUT is in
     * @param executedAction The {@link AbstractAction} that was executed
     * @return The calculated reward
     */
    @Override
    public float getReward(State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final AbstractAction executedAction) {
        final Map<Tag<?>, Object>  tags = currentConcreteState.getAttributes().getTagValues();
        final int noOfEqualElements = tags.keySet().stream()
                .mapToInt(key -> isAttributeEqualToAttributeInPreviousState(key, tags, attributesInPreviousState))
                .sum();
        final float reward = (float) new Divide().value(noOfEqualElements, tags.size());

        attributesInPreviousState = tags;
        System.out.println("reward determined: " + reward);
        return Float.isNaN(reward) ? 0 : reward;
    }

    private int isAttributeEqualToAttributeInPreviousState(final Object key, final Map<Tag<?>, Object> newTagValues, final Map<Tag<?>, Object> OldTagValues) {
        System.out.println("---COMPARING");
        System.out.println(key);
         final Object newAttributeObject = newTagValues.getOrDefault(key, null);
         final Object oldAttributeObject = attributesInPreviousState.getOrDefault(key, null);

         if (newAttributeObject.equals(oldAttributeObject)) {
             return 0;
         }

        return 1;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    }
}
