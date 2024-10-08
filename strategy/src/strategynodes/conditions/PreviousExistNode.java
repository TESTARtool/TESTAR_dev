package strategynodes.conditions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import parsing.StrategyManager;
import strategynodes.data.ActionStatus;
import strategynodes.data.VisitStatus;
import strategynodes.filters.VisitFilter;
import strategynodes.BaseNode;
import strategynodes.filters.ActionTypeFilter;

import java.util.*;

public class PreviousExistNode extends BaseNode implements BooleanNode
{
    private VisitStatus visitStatus;
    private ActionStatus actionStatus;
    private MultiMap<String, Object> filteredPastActions;

    public PreviousExistNode(VisitStatus visitStatus, ActionStatus actionStatus)
    {
        this.visitStatus = visitStatus;
        this.actionStatus = actionStatus;
        filteredPastActions = new MultiMap<>();
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions)
    {
        Action prevAction = state.get(Tags.PreviousAction, null);

        if(prevAction == null)
            return false;

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
            filteredPastActions = VisitFilter.filterAvailableActions(visitStatus, actions, false);

        if(actionStatus != null)
            filteredPastActions = ActionTypeFilter.filter(actionStatus, actions, false);

        return !filteredPastActions.isEmpty();
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("previous-exist");
        if(visitStatus != null)
            joiner.add(visitStatus.toString());
        if(actionStatus != null)
            joiner.add(actionStatus.toString());
        return joiner.toString();
    }
}