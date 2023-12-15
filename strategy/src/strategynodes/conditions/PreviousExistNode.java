package strategynodes.conditions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.data.VisitStatus;
import strategynodes.enums.VisitType;
import strategynodes.filters.VisitFilter;
import strategynodes.BaseNode;
import strategynodes.enums.ActionType;
import strategynodes.enums.Filter;
import strategynodes.filters.ActionTypeFilter;

import java.util.*;

public class PreviousExistNode extends BaseNode implements BooleanNode
{
    private VisitFilter visitFilter;
    private ActionTypeFilter actionTypeFilter;
    private MultiMap<String, Object> filteredPastActions;

    public PreviousExistNode(VisitStatus visitStatus, Filter filter, ActionType actionType)
    {
        if(visitStatus != null)
            this.visitFilter = new VisitFilter(visitStatus);
        if(filter != null && actionType != null)
            this.actionTypeFilter = new ActionTypeFilter(filter, actionType);
        filteredPastActions = new MultiMap<>();
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems)
    {
        Action prevAction = state.get(Tags.PreviousAction, null);

        if(prevAction == null)
            return false;

        filteredPastActions.clear();
        for(String pastActionID : actionsExecuted.keySet())
        {
            List<Object> entry = actionsExecuted.get(pastActionID); //should return empty collection if nonexistent
            ArrayList<Object> copiedEntry = new ArrayList<Object>();
            copiedEntry.add(entry.get(0));
            copiedEntry.add(entry.get(1));
            filteredPastActions.put(pastActionID, copiedEntry);
        }

        if(visitFilter != null)
            filteredPastActions = visitFilter.filter(actions, actionsExecuted, false);

        if(actionTypeFilter != null)
            filteredPastActions = actionTypeFilter.filter(actions, actionsExecuted, false);

        return !filteredPastActions.isEmpty();
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("previous-exist");
        if(visitFilter != null)
            joiner.add(visitFilter.toString());
        if(actionTypeFilter != null)
            joiner.add(actionTypeFilter.toString());
        return joiner.toString();
    }
}