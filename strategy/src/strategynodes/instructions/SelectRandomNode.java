package strategynodes.instructions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.enums.Relation;
import strategynodes.filters.VisitedStatusFilter;
import strategynodes.BaseNode;
import strategynodes.enums.ActionType;
import strategynodes.enums.Filter;
import strategynodes.enums.VisitStatus;
import strategynodes.filters.ActionTypeFilter;
import strategynodes.filters.RelationFilter;

import java.util.*;

public class SelectRandomNode extends BaseNode<Action> implements ActionNode
{
    public final Weight weight;
    private VisitedStatusFilter visitedStatusFilter;
    private ActionTypeFilter actionTypeFilter;
    private RelationFilter relationFilter;
    ArrayList<Action> filteredActions;

    public SelectRandomNode(Integer weight, VisitStatus visitStatus, Filter filter, ActionType actionType)
    {
        this.weight = new Weight(weight);
        if(visitStatus != null)
            visitedStatusFilter = new VisitedStatusFilter(visitStatus);
        if(filter != null && actionType != null)
            actionTypeFilter = new ActionTypeFilter(filter, actionType);
        filteredActions = new ArrayList<>();
    }
    public SelectRandomNode(Integer weight, VisitStatus visitStatus, Filter filter, Relation relation)
    {
        this.weight = new Weight(weight);
        if(visitStatus != null)
            visitedStatusFilter = new VisitedStatusFilter(visitStatus);
        if(filter != null && relation != null)
            relationFilter = new RelationFilter(filter, relation);
    }

    //todo: check if it works correctly
    public Action getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems)
    {
        if(actions.size() == 1) //if there's only one action, pick that one
            return new ArrayList<>(actions).get(0);

        filteredActions.clear();

        if(visitedStatusFilter != null)
            filteredActions = visitedStatusFilter.filter(filteredActions, actionsExecuted);

        if(actionTypeFilter != null)
            filteredActions = actionTypeFilter.filter(filteredActions);
        else if(relationFilter != null)
            filteredActions = relationFilter.filter(state.get(Tags.PreviousAction, null), filteredActions);

        if(filteredActions.isEmpty()) //if nothing has made it through the filters
            return selectRandomAction(actions); //default to picking randomly
        else if (filteredActions.size() == 1)
            return new ArrayList<>(filteredActions).get(0);
        else
            return selectRandomAction(filteredActions); //pick randomly from the filtered list
    }

    @Override
    public int GetWeight()
    {
        return weight.GetWeight();
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(weight.toString());
        joiner.add("select-random");
        if(visitedStatusFilter != null)
            joiner.add(visitedStatusFilter.toString());
        if(actionTypeFilter != null)
            joiner.add(actionTypeFilter.toString());
        else if (relationFilter != null)
            joiner.add(relationFilter.toString());
        return joiner.toString();
    }
}
