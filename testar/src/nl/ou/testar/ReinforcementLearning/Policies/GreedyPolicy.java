package nl.ou.testar.ReinforcementLearning.Policies;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import nl.ou.testar.ReinforcementLearning.Utils.ReinforcementLearningUtils;
import nl.ou.testar.StateModel.AbstractAction;
import org.fruit.alayer.Tag;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Implementation of a greedy policy
 */
public class GreedyPolicy<T extends Object & Comparable<? super T>>  implements Policy {

    private final Number defaultQValue;
    private final Tag<Number> tag;

    public GreedyPolicy(final Number defaultQValue, final Tag<Number> tag){
        this.defaultQValue = defaultQValue;
        this.tag = tag;
    }

    /**
     * For a {@link Set<AbstractAction>} selects actions to execute
     * @return An {@link AbstractAction} selected by the {@link Policy}, can be {@code null}
     */
    @Override
    public AbstractAction applyPolicy(final Set<AbstractAction> actions) {
        final Multimap<Number, AbstractAction> qValuesActionsMultimap = ArrayListMultimap.create();
        actions.forEach(action -> qValuesActionsMultimap.put(action.getAttributes().get(tag, defaultQValue), action));

        final Set<Number> qValues = qValuesActionsMultimap.keySet();
        if (qValues.isEmpty()) {
            return null;
        }

        final Number maxValue = Collections.max(qValues);
        final Collection<AbstractAction> actionsSelected = qValuesActionsMultimap.get(maxValue);
        return ReinforcementLearningUtils.selectAction(actionsSelected);
    }
}
