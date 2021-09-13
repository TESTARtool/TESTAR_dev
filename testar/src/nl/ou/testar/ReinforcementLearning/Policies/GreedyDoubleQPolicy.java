package nl.ou.testar.ReinforcementLearning.Policies;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import nl.ou.testar.ReinforcementLearning.Utils.ReinforcementLearningUtil;
import nl.ou.testar.StateModel.AbstractAction;
import org.fruit.alayer.Tag;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class GreedyDoubleQPolicy<T extends Float>  implements Policy {

    private final T defaultQValue;
    private final Tag<T> tag;
    private final Tag<T> tagB;

    public GreedyDoubleQPolicy(final T defaultQValue, final Tag<T> tag, final Tag<T> tagB){
        this.defaultQValue = defaultQValue;
        this.tag = tag;
        this.tagB = tagB;
    }

    /**
     * For a {@link Set < AbstractAction >} selects actions to execute
     * @return An {@link AbstractAction} selected by the {@link Policy}, can be {@code null}
     */
    @Override
    public AbstractAction applyPolicy(final Set<AbstractAction> actions) {
        final Multimap<Float, AbstractAction> qValuesActionsMultimap = ArrayListMultimap.create();
        actions.forEach(action -> qValuesActionsMultimap.put(
                (((float) action.getAttributes().get(tag, defaultQValue)) + ((float) action.getAttributes().get(tagB, defaultQValue))) / 2,
                action)
        );

        final Set<Float> qValues = qValuesActionsMultimap.keySet();
        if (qValues.isEmpty()) {
            return null;
        }

        final Float maxValue = Collections.max(qValues);
        final Collection<AbstractAction> actionsSelected = qValuesActionsMultimap.get(maxValue);
        return ReinforcementLearningUtil.selectAction(actionsSelected);
    }
}