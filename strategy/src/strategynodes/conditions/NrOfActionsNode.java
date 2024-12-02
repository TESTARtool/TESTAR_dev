package strategynodes.conditions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.data.ActionStatus;
import strategynodes.data.VisitStatus;
import strategynodes.filters.VisitFilter;
import strategynodes.BaseNode;
import strategynodes.filters.ActionTypeFilter;

import java.util.*;

public class NrOfActionsNode extends BaseNode implements IntegerNode
{
    private final VisitStatus visitStatus;
    private final ActionStatus actionStatus;
    ArrayList<Action> filteredActions = new ArrayList<>();
    
    public NrOfActionsNode(VisitStatus visitStatus, ActionStatus actionStatus)
    {
        this.visitStatus = visitStatus;
        this.actionStatus = actionStatus;
    }
    
    @Override
    public Integer getResult(State state, Set<Action> actions)
    {
        filteredActions = new ArrayList<>(actions); // copy the actions list

        if(visitStatus != null)
            filteredActions = VisitFilter.filter(visitStatus, filteredActions);

        if(actionStatus != null)
            filteredActions = ActionTypeFilter.filter(actionStatus, filteredActions);

        return filteredActions.size();
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("n-actions");
        if(visitStatus != null)
            joiner.add(visitStatus.toString());
        if(actionStatus != null)
            joiner.add(actionStatus.toString());
        return joiner.toString();
    }
}