package strategynodes.instructions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.BaseNode;
import strategynodes.data.VisitStatus;
import strategynodes.enums.ActionType;
import strategynodes.enums.Filter;
import strategynodes.enums.VisitType;
import strategynodes.filters.ActionTypeFilter;
import strategynodes.filters.VisitFilter;
import strategynodes.data.Weight;

import java.util.ArrayList;
import java.util.Set;
import java.util.StringJoiner;

public class SelectPreviousNode extends BaseNode implements ActionNode
{
    private final Weight weight;
    private VisitFilter visitFilter;
    private ActionTypeFilter actionTypeFilter;
    MultiMap<String, Object> filteredPastActions;

    public SelectPreviousNode(Integer weight, VisitStatus visitStatus, Filter filter, ActionType actionType)
    {
        this.weight = new Weight(weight);
        if(visitStatus != null)
            visitFilter = new VisitFilter(visitStatus);
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

        if(visitFilter != null)
            filteredPastActions = visitFilter.filter(actions, actionsExecuted, true);

        if(actionTypeFilter != null)
            filteredPastActions = actionTypeFilter.filter(actions, actionsExecuted, true);

        if(filteredPastActions.isEmpty()) //if nothing has made it through the filters
            return selectRandomAction(actions); //default to picking randomly
        else
            return selectRandomPastAction(actions, actionsExecuted);
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
        joiner.add("select-previous");
        if(visitFilter != null)
            joiner.add(visitFilter.toString());
        if(actionTypeFilter != null)
            joiner.add(actionTypeFilter.toString());
        return joiner.toString();
    }
}