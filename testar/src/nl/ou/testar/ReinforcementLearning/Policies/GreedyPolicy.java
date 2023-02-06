package nl.ou.testar.ReinforcementLearning.Policies;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.ReinforcementLearning.Utils.ReinforcementLearningUtil;
import org.testar.statemodel.AbstractAction;
import org.testar.monkey.alayer.Tag;
import org.testar.statemodel.AbstractState;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Implementation of a greedy policy
 */
public class GreedyPolicy implements Policy {

    private final float defaultQValue;
    private final Tag<Float> tag;

    public GreedyPolicy(final float defaultQValue, final Tag<Float> tag) {
        this.defaultQValue = defaultQValue;
        this.tag = tag;
    }

    /**
     * For a {@link Set<AbstractAction>} selects actions to execute
     * @return An {@link AbstractAction} selected by the {@link Policy}, can be {@code null}
     */
    @Override
    public AbstractAction applyPolicy(final AbstractState state, final Set<AbstractAction> actions) {
        final Multimap<Float, AbstractAction> qValuesActionsMultimap = ArrayListMultimap.create();
        actions.forEach(action -> qValuesActionsMultimap.put(action.getAttributes().get(tag, defaultQValue), action));

        final Set<Float> qValues = qValuesActionsMultimap.keySet();
        if (qValues.isEmpty()) {
            return null;
        }

        final float maxValue = Collections.max(qValues);
        final Collection<AbstractAction> actionsSelected = qValuesActionsMultimap.get(maxValue);
        System.out.println("Selecting action with highest Q value: " + maxValue);
        return ReinforcementLearningUtil.selectAction(state, actionsSelected);
    }
}
