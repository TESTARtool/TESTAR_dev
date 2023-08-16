package strategynodes.condition;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.filtering.ActionType;
import strategynodes.BaseIntegerNode;
import strategynodes.filtering.Filter;
import strategynodes.filtering.Modifier;

import java.util.*;

public class NumberOfActionsNode extends BaseIntegerNode
{
    private final Modifier   VISIT_MODIFIER;
    private final Filter     FILTER;
    private final ActionType ACTION_TYPE;
    
    public NumberOfActionsNode(Modifier modifier, Filter filter, ActionType actionType)
    {
        this.VISIT_MODIFIER = modifier;
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
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("n-actions");
        if(VISIT_MODIFIER != null)
            joiner.add(VISIT_MODIFIER.toString());
        if(FILTER != null)
        {
            joiner.add(FILTER.toString());
            joiner.add(ACTION_TYPE.toString());
        }
        return joiner.toString();
    }
}