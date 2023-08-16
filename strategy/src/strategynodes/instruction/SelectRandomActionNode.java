package strategynodes.instruction;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseActionNode;
import strategynodes.filtering.ActionType;
import strategynodes.filtering.Filter;
import strategynodes.filtering.Modifier;

import java.util.*;

public class SelectRandomActionNode extends BaseActionNode
{
    private final Modifier MODIFIER;
    private final Filter   FILTER;
    private final ActionType ACTION_TYPE;

    public SelectRandomActionNode(Integer weight, Modifier modifier, Filter filter, ActionType actionType)
    {
        assignWeight(weight);
        this.MODIFIER = modifier;
        this.FILTER   = filter;
        this.ACTION_TYPE = actionType;
    }

    @Override
    public Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works
    {
        if(actions.size() == 1) //if there's only one action, pick that one
            return new ArrayList<>(actions).get(0);

        //filter by action type
        List<Action> filteredActions = (FILTER != null && ACTION_TYPE != null) ?
                filterByActionType(actions, FILTER, ACTION_TYPE) :
                new ArrayList<>(actions);

        //filter by modifier
        if(MODIFIER != null)
            filteredActions = filterByVisitModifier(MODIFIER, filteredActions, actionsExecuted);

        if(filteredActions.isEmpty()) //if nothing has made it through the filters
            return selectRandomAction(actions); //default to picking randomly
        else if (filteredActions.size() == 1)
            return new ArrayList<>(filteredActions).get(0);
        else
            return selectRandomAction(filteredActions); //pick randomly from the filtered list
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(Integer.toString(WEIGHT));
        joiner.add("select-random");
        if(MODIFIER != null)
            joiner.add(MODIFIER.toString());
        if(FILTER != null)
        {
            joiner.add(FILTER.toString());
            joiner.add(ACTION_TYPE.toString());
        }
        return joiner.toString();
    }
}