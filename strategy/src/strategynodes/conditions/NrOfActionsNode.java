package strategynodes.conditions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.filters.VisitedStatusFilter;
import strategynodes.BaseNode;
import strategynodes.enums.ActionType;
import strategynodes.enums.Filter;
import strategynodes.enums.VisitStatus;
import strategynodes.filters.ActionTypeFilter;

import java.util.*;

public class NrOfActionsNode extends BaseNode<Integer>
{
    private VisitedStatusFilter visitedStatusFilter;
    private ActionTypeFilter actionTypeFilter;
    ArrayList<Action> filteredActions;
    
    public NrOfActionsNode(VisitStatus visitStatus, Filter filter, ActionType actionType)
    {
        if(visitStatus != null)
            this.visitedStatusFilter = new VisitedStatusFilter(visitStatus);
        if(filter != null && actionType != null)
            this.actionTypeFilter = new ActionTypeFilter(filter, actionType);
        filteredActions = new ArrayList<>();
    }
    
    @Override
    public Integer getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems)
    {
        filteredActions.clear();

        if(visitedStatusFilter != null)
            filteredActions = visitedStatusFilter.filter(filteredActions, actionsExecuted);

        if(actionTypeFilter != null)
            filteredActions = actionTypeFilter.filter(filteredActions);

        return filteredActions.size();
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("n-actions");
        if(visitedStatusFilter != null)
            joiner.add(visitedStatusFilter.toString());
        if(actionTypeFilter != null)
            joiner.add(actionTypeFilter.toString());
        return joiner.toString();
    }
}