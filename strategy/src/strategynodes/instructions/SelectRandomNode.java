package strategynodes.instructions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.data.VisitStatus;
import strategynodes.enums.Relation;
import strategynodes.enums.VisitType;
import strategynodes.filters.VisitFilter;
import strategynodes.BaseNode;
import strategynodes.enums.ActionType;
import strategynodes.enums.Filter;
import strategynodes.filters.ActionTypeFilter;
import strategynodes.filters.RelationFilter;
import strategynodes.data.Weight;

import java.util.*;

public class SelectRandomNode extends BaseNode implements ActionNode
{
    public final Weight weight;
    private VisitFilter visitFilter;
    private ActionTypeFilter actionTypeFilter;
    private RelationFilter relationFilter;
    ArrayList<Action> filteredActions;

    public SelectRandomNode(Integer weight, VisitStatus visitStatus, Filter filter, ActionType actionType)
    {
        this.weight = new Weight(weight);
        if(visitStatus != null)
            visitFilter = new VisitFilter(visitStatus);
        if(filter != null && actionType != null)
            actionTypeFilter = new ActionTypeFilter(filter, actionType);
        filteredActions = new ArrayList<>();
    }
    public SelectRandomNode(Integer weight, VisitStatus visitStatus, Filter filter, Relation relation)
    {
        this.weight = new Weight(weight);
        if(visitStatus != null)
            visitFilter = new VisitFilter(visitStatus);
        if(filter != null && relation != null)
            relationFilter = new RelationFilter(filter, relation);
    }

    //todo: check if it works correctly
    public Action getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems)
    {
        if(actions.size() == 1) //if there's only one action, pick that one
            return new ArrayList<>(actions).get(0);

        filteredActions = new ArrayList<>(actions); // copy the actions list

        if(visitFilter != null)
            filteredActions = visitFilter.filter(filteredActions, actionsExecuted);

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
        if(visitFilter != null)
            joiner.add(visitFilter.toString());
        if(actionTypeFilter != null)
            joiner.add(actionTypeFilter.toString());
        else if (relationFilter != null)
            joiner.add(relationFilter.toString());
        return joiner.toString();
    }
}
