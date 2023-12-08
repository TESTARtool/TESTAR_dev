package strategynodes.conditions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.filters.VisitedStatusFilter;
import strategynodes.BaseNode;
import strategynodes.enums.ActionType;
import strategynodes.enums.Filter;
import strategynodes.enums.Relation;
import strategynodes.enums.VisitStatus;
import strategynodes.filters.ActionTypeFilter;
import strategynodes.filters.RelationFilter;

import java.util.*;

public class AnyExistNode extends BaseNode<Boolean>
{
    private VisitedStatusFilter visitedStatusFilter;
    private ActionTypeFilter actionTypeFilter;
    private RelationFilter relationFilter;
    private ArrayList<Action> filteredActions;
    
    public AnyExistNode(VisitStatus visitStatus, Filter filter, ActionType actionType)
    {
        if(visitStatus != null)
            this.visitedStatusFilter = new VisitedStatusFilter(visitStatus);
        if(filter != null && actionType != null)
            this.actionTypeFilter = new ActionTypeFilter(filter, actionType);
        filteredActions = new ArrayList<Action>();
    }

    public AnyExistNode(VisitStatus visitStatus, Filter filter, Relation relation)
    {
        if(visitStatus != null)
            this.visitedStatusFilter = new VisitedStatusFilter(visitStatus);
        if(filter != null && relationFilter != null)
            this.relationFilter = new RelationFilter(filter, relation);
        filteredActions = new ArrayList<Action>();
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems)
    {
        if (filteredActions.isEmpty()) //and empty list means no possible valid actions
            return false;
        else if (visitedStatusFilter == null && actionTypeFilter == null)
            return true; //if there are no filters to apply, any action is valid

        filteredActions = new ArrayList<>(actions);

        if(visitedStatusFilter != null)
            filteredActions = visitedStatusFilter.filter(filteredActions, actionsExecuted);

        if(actionTypeFilter != null)
            filteredActions = actionTypeFilter.filter(filteredActions);
        else if(relationFilter != null)
            filteredActions = relationFilter.filter(state.get(Tags.PreviousAction, null), filteredActions);

        return (!filteredActions.isEmpty());
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("any-exist");
        if(visitedStatusFilter != null)
            joiner.add(visitedStatusFilter.toString());
        if(actionTypeFilter != null)
            joiner.add(actionTypeFilter.toString());
        else if(relationFilter != null)
            joiner.add(relationFilter.toString());
        return joiner.toString();
    }
}