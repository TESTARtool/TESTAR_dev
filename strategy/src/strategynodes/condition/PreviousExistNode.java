package strategynodes.condition;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.filtering.ActionType;
import strategynodes.BaseBooleanNode;
import strategynodes.filtering.Filter;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class PreviousExistNode extends BaseBooleanNode
{
    private final Filter     FILTER;
    private final ActionType ACTION_TYPE;

    public PreviousExistNode(Filter filter, ActionType actionType)
    {
        this.FILTER = filter;
        this.ACTION_TYPE = actionType;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted, ArrayList<String> operatingSystems)
    {
        Action prevAction = state.get(Tags.PreviousAction, null);

        if(prevAction == null)
            return false;

        if(FILTER != null && ACTION_TYPE != null)
            return filterAllowsAction(prevAction, FILTER, ACTION_TYPE);

        return false; //default is false
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("previous-exist");
        if(FILTER != null)
        {
            joiner.add(FILTER.toString());
            joiner.add(ACTION_TYPE.toString());
        }
        return joiner.toString();
    }
}