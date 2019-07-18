package nl.ou.testar.StateModel.ActionSelection;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.AbstractStateModel;
import nl.ou.testar.StateModel.AbstractStateTransition;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import nl.ou.testar.StateModel.Exception.StateModelException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class UnvisitedActionsSelector implements ActionSelector {

    // the maximum nr of transitions to look ahead for unvisited actions
    private final int MAX_HOPS = 4;

    @Override
    public AbstractAction selectAction(AbstractState currentState, AbstractStateModel abstractStateModel) throws ActionNotFoundException{
        Set<AbstractAction> unvisitedActions = getUnvisitedActions(currentState, abstractStateModel, MAX_HOPS);
        if (unvisitedActions.size() == 0) {
            throw new ActionNotFoundException();
        }
        else if (unvisitedActions.size() == 1) {
            return unvisitedActions.iterator().next();
        }
        else {
            // multiple actions
            // in this selection algorithm, we just choose a random one.
            long graphTime = System.currentTimeMillis();
            Random rnd = new Random(graphTime);
            return (new ArrayList<>(unvisitedActions)).get(rnd.nextInt(unvisitedActions.size()));
        }
    }

    /**
     * This helper method will recursively look for unvisited actions
     * @param state
     * @param abstractStateModel
     * @param nrOfHopsLeft
     * @return
     */
    private Set<AbstractAction> getUnvisitedActions(AbstractState state, AbstractStateModel abstractStateModel, int nrOfHopsLeft) {
        Set<AbstractAction> actions = state.getUnvisitedActions();
        if (!actions.isEmpty() || nrOfHopsLeft == 0) {
            return actions;
        }
        actions = new HashSet<>();

        // go one layer deeper
        // the object is to return the action for this state that will lead to other states with
        // unvisited actions
        // the fact that all the actions are visited means we have transitions for them
        //@todo ad the following algorithm isn't perfect, as it will follow transitions for each action until it reaches
        //@todo an unvisited action. For one transition this will be the next state, for some it will be 3 down.
        //@todo Ideally the algorithm should go one level at a time
        // it's meant as a demo of what is possible
        for (AbstractStateTransition transition:abstractStateModel.getOutgoingTransitionsForState(state.getStateId())) {
            AbstractState targetState = transition.getTargetState();
            if (getUnvisitedActions(targetState, abstractStateModel, nrOfHopsLeft - 1).size() > 0) {
                actions.add(transition.getAction());
            }
        }
        return actions;
    }


}
