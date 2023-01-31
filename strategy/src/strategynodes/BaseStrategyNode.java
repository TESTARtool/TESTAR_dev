package strategynodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;

import java.util.Map;
import java.util.Set;

public abstract class BaseStrategyNode<T>
{
    public abstract T getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted);
    
    @Override
    public abstract String toString();
    
    protected boolean actionAllowedByFilter(Action action, Filter filter, ActionType actionType)
    {
        boolean actionIsOfType = (actionType.actionIsThisType(action));
        
        return (
                (filter == Filter.INCLUDE && actionIsOfType) ||
                (filter == Filter.EXCLUDE && (!actionIsOfType))
        );
    }

    protected boolean actionMatchesVisitorModifier(Action action, VisitedModifier visitedModifier, Map<String, Integer> actionsExecuted)
    {
        boolean actionIsVisited = (actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)));

        return (
                (visitedModifier == VisitedModifier.VISITED && actionIsVisited) ||
                (visitedModifier == VisitedModifier.UNVISITED && (!actionIsVisited))
        );
    }
}