package strategynodes.conditions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import parsing.StrategyManager;
import strategynodes.BaseNode;
import strategynodes.data.ActionStatus;
import strategynodes.data.VisitStatus;
import strategynodes.filters.ActionTypeFilter;
import strategynodes.filters.VisitFilter;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class NrOfPreviousActionsNode extends BaseNode implements IntegerNode
{
    private final VisitStatus visitStatus;
    private final ActionStatus actionStatus;
    MultiMap<String, Object> filteredPastActions = new MultiMap<>();

    public NrOfPreviousActionsNode(VisitStatus visitStatus, ActionStatus actionStatus)
    {
        this.visitStatus = visitStatus;
        this.actionStatus = actionStatus;
        filteredPastActions = new MultiMap<>();
    }
    
    @Override
    public Integer getResult(State state, Set<Action> actions)
    {
        filteredPastActions.clear();
        for(String pastActionID : StrategyManager.getActionsExecutedIDs())
        {
//            List<Object> entry = actionsExecuted.get(pastActionID); //should return empty collection if nonexistent
//            ArrayList<Object> copiedEntry = new ArrayList<Object>();
//            copiedEntry.add(entry.get(0));
//            copiedEntry.add(entry.get(1));

            List<Object> copiedEntry = StrategyManager.getEntryCopy(pastActionID);

            filteredPastActions.put(pastActionID, copiedEntry);
        }

        if(visitStatus != null)
            filteredPastActions = VisitFilter.filterAvailableActions(visitStatus, actions, filteredPastActions, false);

        if(actionStatus != null)
            filteredPastActions = ActionTypeFilter.filter(actionStatus, actions, filteredPastActions, false);

        return filteredPastActions.size();
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("n-previous");
        if(visitStatus != null)
            joiner.add(visitStatus.toString());
        if(actionStatus != null)
            joiner.add(actionStatus.toString());
        return joiner.toString();
    }
}