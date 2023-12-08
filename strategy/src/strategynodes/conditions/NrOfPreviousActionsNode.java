package strategynodes.conditions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseNode;
import strategynodes.enums.ActionType;
import strategynodes.enums.Filter;
import strategynodes.enums.VisitStatus;
import strategynodes.filters.ActionTypeFilter;
import strategynodes.filters.VisitedStatusFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class NrOfPreviousActionsNode extends BaseNode<Integer>
{
    private VisitedStatusFilter visitedStatusFilter;
    private ActionTypeFilter actionTypeFilter;
    MultiMap<String, Object> filteredPastActions;

    public NrOfPreviousActionsNode(VisitStatus visitStatus, Filter filter, ActionType actionType)
    {
        if(visitStatus != null)
            this.visitedStatusFilter = new VisitedStatusFilter(visitStatus);
        if(filter != null && actionType != null)
            this.actionTypeFilter = new ActionTypeFilter(filter, actionType);
        filteredPastActions = new MultiMap<>();
    }
    
    @Override
    public Integer getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems)
    {
        filteredPastActions.clear();
        for(String pastActionID : actionsExecuted.keySet())
        {
            List<Object> entry = actionsExecuted.get(pastActionID); //should return empty collection if nonexistent
            ArrayList<Object> copiedEntry = new ArrayList<Object>();
            copiedEntry.add(entry.get(0));
            copiedEntry.add(entry.get(1));
            filteredPastActions.put(pastActionID, copiedEntry);
        }

        if(visitedStatusFilter != null)
            filteredPastActions = visitedStatusFilter.filter(actions, filteredPastActions, false);

        if(actionTypeFilter != null)
            filteredPastActions = actionTypeFilter.filter(actions, filteredPastActions, false);

        return filteredPastActions.size();
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("n-previous");
        if(visitedStatusFilter != null)
            joiner.add(visitedStatusFilter.toString());
        if(actionTypeFilter != null)
            joiner.add(actionTypeFilter.toString());
        return joiner.toString();
    }
}