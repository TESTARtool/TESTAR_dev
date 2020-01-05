package nl.ou.testar.ReinforcementLearning;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import nl.ou.testar.GraphDB;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.Util.ActionHelper;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class GuiStateGraphForQlearning extends GraphDB {
    // Hashmap for action (actionId) q-value combinations
    private HashMap<String, Double> map = new HashMap<String, Double>();

    public GuiStateGraphForQlearning() {
        super(true,"memory:demo","admin","admin");
    }

    /**
     * This method retrieves for a set of actions the q-values for the State action combination
     * @param state The current state
     * @param actions The list of actions
     * @return A map containing actions and their q-value, the default return value is 0
     */
    @Nonnull
    public Multimap<Double, Action> getActionQValues(@Nonnull final State state, @Nonnull final Set<Action> actions) {
        final Multimap <Double, Action> qvalues = HashMultimap.create();
        actions.stream()
                .forEach(action -> qvalues.put(getActionId(action), action));
        return qvalues;
    }

    @Nonnull
    private Double getActionId(@Nonnull final Action action) {
        final Set<AbstractAction> abstractAction = ActionHelper.convertActionsToAbstractActions(Collections.singleton(action));
        if (abstractAction.isEmpty()) {
            return null;
        }
        final String actionId = abstractAction.iterator().next().getActionId();

        return map.get(actionId);
    }

    /**
     * returns the number of times an State action combination is executed
     * @param fromState
     * @param toState
     * @param action
     * @return
     */
    public int getExecutionCounter(@Nullable final AbstractState fromState, @Nullable final State toState, @Nonnull final AbstractAction action) {
         return 0;
    }

    /**
     * Saves an action and q-value
     * @param outgoingState
     * @param action
     * @param qValue
     */
    public void saveqValue(AbstractState outgoingState, AbstractAction action, double qValue) {
        map.put(action.getActionId(), qValue);
    }

    /**
     * Retrieve the q-value for a state, action combination
     * @param fromState
     * @param action
     * @return The qvalue stored in the database, -1 if not found
     */
    @Nullable
    public double getQValue(@Nullable final AbstractState fromState, @Nullable final AbstractAction action) {
        return Optional.ofNullable(map.get(action.getActionId()))
                .orElseGet(() -> -1d);
    }

    /**
     * For a given state retrieve all the Q-values of states with an outoing connection
     *
     * @param outgoingState
     * @return A list of type Double, returns an empty list of no outgoing connections are found
     */
    @Nonnull
    public Collection<Double> getQvaluesForAState(@Nonnull final AbstractState outgoingState) {
        return outgoingState.getActions().stream()
                .map(action -> map.get(action.getActionId()))
                .map(value -> value == null ? 0d : value )
                .collect(Collectors.toSet());
    }
}
