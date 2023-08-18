package strategynodes.condition;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.filtering.ActionType;
import strategynodes.BaseBooleanNode;
import strategynodes.filtering.Filter;
import strategynodes.filtering.Modifier;

import java.util.*;

public class AnyExistNode extends BaseBooleanNode
{
    private final Modifier   MODIFIER;
    private final Filter     FILTER;
    private final ActionType ACTION_TYPE;
    
    public AnyExistNode(Modifier modifier, Filter filter, ActionType actionType)
    {
        this.MODIFIER = modifier;
        this.FILTER   = filter;
        this.ACTION_TYPE = actionType;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted, ArrayList<String> operatingSystems)
    {
        //if there are no filters to apply, any action is valid
        if (MODIFIER == null && FILTER == null && ACTION_TYPE == null && !actions.isEmpty())
            return true;

        //filter by action type
        List<Action> filteredActions = (FILTER != null && ACTION_TYPE != null) ?
                filterByActionType(actions, FILTER, ACTION_TYPE) :
                new ArrayList<>(actions);

        boolean validActionFound = !filteredActions.isEmpty();

        //check by modifier
        if(MODIFIER != null)
            validActionFound = validActionExists(MODIFIER, filteredActions, actionsExecuted);

        return validActionFound;
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("any-exist");
        if(MODIFIER != null)
            joiner.add(MODIFIER.toString());
        if(FILTER != null)
        {
            joiner.add(FILTER.toString());
            joiner.add(ACTION_TYPE.toString());
        }
        return joiner.toString();
    }
}