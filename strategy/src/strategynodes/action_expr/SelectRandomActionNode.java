package strategynodes.action_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.ActionType;
import strategynodes.Filter;
import strategynodes.VisitedModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectRandomActionNode extends BaseActionNode
{
    private final VisitedModifier   VISITED_MODIFIER;
    private final Filter            FILTER;
    private final ActionType        ACTION_TYPE;

    public SelectRandomActionNode(Integer weight, VisitedModifier visitedModifier, Filter filter, ActionType actionType)
    {
        this.WEIGHT             = (weight != null || weight > 0) ? weight : 1;
        this.VISITED_MODIFIER   = visitedModifier;
        this.FILTER             = filter;
        this.ACTION_TYPE        = actionType;
    }

    @Override
    public Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works
    {
        if(actions.size() == 1) //if there's only one action, pick that one
            return new ArrayList<>(actions).get(0);
        
        List<Action> filteredActions = new ArrayList(actions);
        
        if(VISITED_MODIFIER != null)
            filteredActions = filterByVisitedModifier(VISITED_MODIFIER, filteredActions, actionsExecuted);
        
        if(FILTER != null && ACTION_TYPE != null)
            filteredActions = filterByActionType(filteredActions, FILTER, ACTION_TYPE);
        
        
        if(filteredActions.isEmpty()) //if nothing has made it through the filters
            return selectRandomAction(actions); //default to picking randomly
        else if (filteredActions.size() == 1)
            return new ArrayList<>(actions).get(0);
        else
            return selectRandomAction(filteredActions); //pick randomly from the filtered list
    }

    @Override
    public String toString()
    {
        String string = WEIGHT + " select-random";
        if(VISITED_MODIFIER != null) string += " " + VISITED_MODIFIER;
        if(FILTER != null) string += " " + FILTER + " " + ACTION_TYPE.toString();
        return string;
    }
}