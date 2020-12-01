package nl.ou.testar.ReinforcementLearning.Policies;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import nl.ou.testar.ReinforcementLearning.Utils.ReinforcementLearningUtil;
import nl.ou.testar.StateModel.AbstractAction;
import org.fruit.alayer.Tag;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Implementation of a greedy policy
 */
public class GreedyPolicy<T extends Object & Comparable<? super T>>  implements Policy {

    private final T defaultQValue;
    private final Tag<T> tag;

    public GreedyPolicy(final T defaultQValue, final Tag<T> tag){
        this.defaultQValue = defaultQValue;
        this.tag = tag;
    }

    /**
     * For a {@link Set<AbstractAction>} selects actions to execute
     * @return An {@link AbstractAction} selected by the {@link Policy}, can be {@code null}
     */
    @Override
    public AbstractAction applyPolicy(final Set<AbstractAction> actions) {
        final Multimap<T, AbstractAction> qValuesActionsMultimap = ArrayListMultimap.create();
        actions.forEach(action -> qValuesActionsMultimap.put(action.getAttributes().get(tag, defaultQValue), action));

        final Set<T> qValues = qValuesActionsMultimap.keySet();
        if (qValues.isEmpty()) {
            return null;
        }

        final T maxValue = Collections.max(qValues);
        final Collection<AbstractAction> actionsSelected = qValuesActionsMultimap.get(maxValue);
        return ReinforcementLearningUtil.selectAction(actionsSelected);
    }
}
