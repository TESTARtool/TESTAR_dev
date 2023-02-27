package strategynodes.action_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.ActionType;
import strategynodes.Filter;
import strategynodes.VisitModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectRandomActionNode extends BaseActionNode
{
    private final VisitModifier VISIT_MODIFIER;
    private final Filter            FILTER;
    private final ActionType        ACTION_TYPE;

    public SelectRandomActionNode(Integer weight, VisitModifier visitModifier, Filter filter, ActionType actionType)
    {
        this.WEIGHT             = (weight != null || weight > 0) ? weight : 1;
        this.VISIT_MODIFIER = visitModifier;
        this.FILTER             = filter;
        this.ACTION_TYPE        = actionType;
    }

    @Override
    public Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works
    {
        if(actions.size() == 1) //if there's only one action, pick that one
            return new ArrayList<>(actions).get(0);

        //filter by action type
        List<Action> filteredActions = (FILTER != null && ACTION_TYPE != null) ?
                filterByActionType(actions, FILTER, ACTION_TYPE) :
                new ArrayList<>(actions);

        //filter by visit modifier
        if(VISIT_MODIFIER != null)
            filteredActions = filterByVisitModifier(VISIT_MODIFIER, filteredActions, actionsExecuted);

        if(filteredActions.isEmpty()) //if nothing has made it through the filters
            return selectRandomAction(actions); //default to picking randomly
        else if (filteredActions.size() == 1)
            return new ArrayList<>(filteredActions).get(0);
        else
            return selectRandomAction(filteredActions); //pick randomly from the filtered list
    }

    @Override
    public String toString()
    {
        String string = WEIGHT + " select-random";
        if(VISIT_MODIFIER != null) string += " " + VISIT_MODIFIER;
        if(FILTER != null) string += " " + FILTER + " " + ACTION_TYPE.toString();
        return string;
    }
}