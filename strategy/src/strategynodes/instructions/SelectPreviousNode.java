package strategynodes.instructions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.BaseNode;
import strategynodes.enums.ActionType;
import strategynodes.enums.Filter;
import strategynodes.enums.VisitStatus;
import strategynodes.filters.ActionTypeFilter;
import strategynodes.filters.VisitedStatusFilter;

import java.util.ArrayList;
import java.util.Set;
import java.util.StringJoiner;

public class SelectPreviousNode extends BaseNode<Action>
{
    private final Weight weight;
    private VisitedStatusFilter visitedStatusFilter;
    private ActionTypeFilter actionTypeFilter;
    MultiMap<String, Object> filteredPastActions;

    public SelectPreviousNode(Integer weight, VisitStatus visitStatus, Filter filter, ActionType actionType)
    {
        this.weight = new Weight(weight);
        if(visitStatus != null)
            visitedStatusFilter = new VisitedStatusFilter(visitStatus);
        if(filter != null && actionType != null)
            actionTypeFilter = new ActionTypeFilter(filter, actionType);
        filteredPastActions = new MultiMap<>();
    }

    @Override
    public Action getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems) //todo: check if it works correctly
    {
        Action prevAction = state.get(Tags.PreviousAction, null);

        if(prevAction == null)
            return selectRandomAction(actions);

        filteredPastActions.clear();

        if(visitedStatusFilter != null)
            filteredPastActions = visitedStatusFilter.filter(actions, actionsExecuted, true);

        if(actionTypeFilter != null)
            filteredPastActions = actionTypeFilter.filter(actions, actionsExecuted, true);

        if(filteredPastActions.isEmpty()) //if nothing has made it through the filters
            return selectRandomAction(actions); //default to picking randomly
        else
            return selectRandomPastAction(actions, actionsExecuted);
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(weight.toString());
        joiner.add("select-previous");
        if(visitedStatusFilter != null)
            joiner.add(visitedStatusFilter.toString());
        if(actionTypeFilter != null)
            joiner.add(actionTypeFilter.toString());
        return joiner.toString();
    }
}