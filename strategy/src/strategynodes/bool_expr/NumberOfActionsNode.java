package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.ActionType;
import strategynodes.BaseStrategyNode;
import strategynodes.Filter;
import strategynodes.VisitModifier;

import java.util.*;

public class NumberOfActionsNode extends BaseStrategyNode<Integer>
{
    private final VisitModifier VISIT_MODIFIER;
    private final Filter            FILTER;
    private final ActionType        ACTION_TYPE;
    
    public NumberOfActionsNode(VisitModifier visitModifier, Filter filter, ActionType actionType)
    {
        this.VISIT_MODIFIER = visitModifier;
        this.FILTER             = filter;
        this.ACTION_TYPE        = actionType;
    }
    
    @Override
    public Integer getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        //if there are no filters to apply, any action is valid
        if(VISIT_MODIFIER == null && FILTER == null && ACTION_TYPE == null && !actions.isEmpty())
            return actions.size();

        //filter by action type
        List<Action> filteredActions = (FILTER != null && ACTION_TYPE != null) ?
                filterByActionType(actions, FILTER, ACTION_TYPE) :
                new ArrayList<>(actions);

        //filter by visit modifier
        if(VISIT_MODIFIER != null)
            filteredActions = filterByVisitModifier(VISIT_MODIFIER, filteredActions, actionsExecuted);

        return filteredActions.size();
    }
    
    @Override
    public String toString()
    {
        String string = "n-actions";
        if(VISIT_MODIFIER != null) string += " " + VISIT_MODIFIER;
        if(FILTER != null) string += " " + FILTER + " " + ACTION_TYPE.toString();
        return string;
    }
}