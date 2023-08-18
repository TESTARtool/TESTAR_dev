package strategynodes.instruction;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.BaseActionNode;
import strategynodes.filtering.Modifier;
import strategynodes.filtering.Relation;

import java.util.*;

public class SelectRandomRelationNode extends BaseActionNode
{
    private final Modifier MODIFIER;
    private final Relation RELATION;
    
    public SelectRandomRelationNode(Integer weight, Modifier modifier, Relation relation)
    {
        assignWeight(weight);
        this.MODIFIER = modifier;
        this.RELATION = relation;
    }
    
    @Override
    public Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted, ArrayList<String> operatingSystems)
    {
        if(actions.size() == 1) //if there's only one action, pick that one
            return new ArrayList<>(actions).get(0);
        
        List<Action> filteredActions = new ArrayList(actions);
        Action prevAction = state.get(Tags.PreviousAction, null);

        if(prevAction == null)
            return selectRandomAction(actions);

        //filter by relation
        filteredActions = filterByRelation(prevAction, filteredActions, RELATION);

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
        joiner.add(RELATION.toString());
        return joiner.toString();
    }
}