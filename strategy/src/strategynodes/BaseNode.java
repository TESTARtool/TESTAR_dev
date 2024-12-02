package strategynodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import parsing.StrategyManager;

import java.util.*;

public abstract class BaseNode
{
    protected Action selectRandomAction(Collection<Action> actions)
    {
        return (Action) actions.toArray()[new Random().nextInt(actions.size())]; //return a random action
    }

    public static Collection<Action> filterActionsByExecution(Set<Action> actions)
    {
        Collection<Action> filteredActions = new ArrayList<>();
        for(Action action : actions)
        {
            String actionID = action.get(Tags.AbstractID);
            if(StrategyManager.actionIsExecuted(actionID)) //action is in executedActions list
                filteredActions.add(action);
        }
        return filteredActions;
    }

    @Override
    public abstract String toString();
}