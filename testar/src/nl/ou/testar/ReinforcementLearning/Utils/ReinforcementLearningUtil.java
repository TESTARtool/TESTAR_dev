package nl.ou.testar.ReinforcementLearning.Utils;

import nl.ou.testar.RandomActionSelector;
import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import org.fruit.alayer.Tag;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ReinforcementLearningUtil {

    /**
     * Util function for action selection in case a policy returns multiple actions.
     * If the provided collection is emtpy a null value is provided.
     * If one action is provided than this action is returned
     * If multiple actions are provided a random action is returns
     *
     * @param actionsSelected A collection of actions selected by a {@link nl.ou.testar.ReinforcementLearning.Policies.Policy}, is never {@code null}
     * @return An {@link AbstractAction}
     */
    public static AbstractAction selectAction(final Collection<AbstractAction> actionsSelected) {
        if (actionsSelected.isEmpty()) {
            return null;
        } else if (actionsSelected.size() == 1) {
            return actionsSelected.iterator().next();
        } else {
            final Set<AbstractAction> actionSetMaxValues = new HashSet<>(actionsSelected);
            return RandomActionSelector.selectAbstractAction(actionSetMaxValues);
        }
    }

    public static <T> Tag<T> getTag(final Settings settings) {

        String tagName = settings.get(ConfigTags.TagName);
        return (Tag<T>) RLTags.getTag(tagName);
    }
}
