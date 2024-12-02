package strategynodes.conditions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.BaseNode;
import strategynodes.data.ActionStatus;
import strategynodes.data.RelationStatus;
import strategynodes.data.VisitStatus;
import strategynodes.filters.ActionTypeFilter;
import strategynodes.filters.RelationFilter;
import strategynodes.filters.VisitFilter;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringJoiner;

public class AnyExistNode extends BaseNode implements BooleanNode
{
    private final VisitStatus visitStatus;
    private final ActionStatus actionStatus;
    private final RelationStatus relationStatus;
    private ArrayList<Action> filteredActions = new ArrayList<Action>();
    
    public AnyExistNode(VisitStatus visitStatus, ActionStatus actionStatus)
    {
        this.visitStatus = visitStatus;
        this.actionStatus = actionStatus;
        this.relationStatus = null;
    }

    public AnyExistNode(VisitStatus visitStatus, RelationStatus relationStatus)
    {
        this.visitStatus = visitStatus;
        this.actionStatus = null;
        this.relationStatus = relationStatus;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions)
    {
        if (filteredActions.isEmpty()) //and empty list means no possible valid actions
            return false;
        else if (visitStatus == null && actionStatus == null)
            return true; //if there are no filters to apply, any action is valid

        filteredActions = new ArrayList<>(actions); // copy the actions list

        if(visitStatus != null)
            filteredActions = VisitFilter.filter(visitStatus, filteredActions);

        if(actionStatus != null)
            filteredActions = ActionTypeFilter.filter(actionStatus, filteredActions);
        else if(relationStatus != null)
            filteredActions = RelationFilter.filter(relationStatus, state.get(Tags.PreviousAction, null), filteredActions);

        return (!filteredActions.isEmpty());
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("any-exist");
        if(visitStatus != null)
            joiner.add(visitStatus.toString());
        if(actionStatus != null)
            joiner.add(actionStatus.toString());
        else if(relationStatus != null)
            joiner.add(relationStatus.toString());
        return joiner.toString();
    }
}