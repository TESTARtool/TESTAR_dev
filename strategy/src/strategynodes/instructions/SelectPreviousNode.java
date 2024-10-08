package strategynodes.instructions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.BaseNode;
import strategynodes.data.ActionStatus;
import strategynodes.data.VisitStatus;
import strategynodes.filters.ActionTypeFilter;
import strategynodes.filters.VisitFilter;
import strategynodes.data.Weight;

import java.util.Set;
import java.util.StringJoiner;

public class SelectPreviousNode extends BaseNode implements ActionNode
{
    private final Weight weight;
    private final VisitStatus visitStatus;
    private final ActionStatus actionStatus;
    MultiMap<String, Object> filteredPastActions = new MultiMap<>();

    public SelectPreviousNode(Integer weight, VisitStatus visitStatus, ActionStatus actionStatus)
    {
        this.weight = new Weight(weight);
        this.visitStatus = visitStatus;
        this.actionStatus = actionStatus;
    }

    //todo: check if it works correctly
    @Override
    public Action getResult(State state, Set<Action> actions)
    {
        Action prevAction = state.get(Tags.PreviousAction, null);

        if(prevAction == null)
            return selectRandomAction(actions);

        filteredPastActions.clear();

        if(visitStatus != null)
            filteredPastActions = VisitFilter.filterAvailableActions(visitStatus, actions, filteredPastActions, true);

        if(actionStatus != null)
            filteredPastActions = ActionTypeFilter.filter(actionStatus, actions, filteredPastActions, true);

        if(filteredPastActions.isEmpty()) //if nothing has made it through the filters
            return selectRandomAction(actions); //default to picking randomly
        else
            return selectRandomPastAction(actions);
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