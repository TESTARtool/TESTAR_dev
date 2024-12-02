package strategynodes.instructions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.BaseNode;
import strategynodes.data.ActionStatus;
import strategynodes.data.VisitStatus;
import strategynodes.data.Weight;
import strategynodes.filters.ActionTypeFilter;
import strategynodes.filters.VisitFilter;

import java.util.*;

public class SelectPreviousNode extends BaseNode implements ActionNode
{
    private final Weight weight;
    private final VisitStatus visitStatus;
    private final ActionStatus actionStatus;

    public SelectPreviousNode(Integer weight, VisitStatus visitStatus, ActionStatus actionStatus)
    {
        this.weight = new Weight(weight);
        this.visitStatus = visitStatus;
        this.actionStatus = actionStatus;
    }

    @Override
    public Action getResult(State state, Set<Action> actions)
    {
        Action prevAction = state.get(Tags.PreviousAction, null);

        if(prevAction == null)
            return selectRandomAction(actions);

        Collection<Action> filteredActions = filterActionsByExecution(actions); //only keep actions that are present in the executedActions list
        if(filteredActions.isEmpty()) //if nothing has made it through the filters
            return selectRandomAction(actions); //default to picking randomly

        if(visitStatus != null)
            filteredActions = VisitFilter.filter(visitStatus, filteredActions);

        if(actionStatus != null)
            filteredActions = ActionTypeFilter.filter(actionStatus, filteredActions);

        if(filteredActions.isEmpty()) //if nothing has made it through the filters
            return selectRandomAction(filterActionsByExecution(actions)); //default to picking a random past action
        else
            return selectRandomAction(filteredActions); //everything is okay, pick from the list of options
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
        if(visitStatus != null)
            joiner.add(visitStatus.toString());
        if(actionStatus != null)
            joiner.add(actionStatus.toString());
        return joiner.toString();
    }
}