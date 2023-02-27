package strategynodes.action_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.RelatedAction;
import strategynodes.VisitModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectRelationNode extends BaseActionNode
{
    private final VisitModifier VISITED_MODIFIER;
    private final RelatedAction RELATED_ACTION;
    
    public SelectRelationNode(Integer weight, VisitModifier visitModifier, RelatedAction relatedAction)
    {
        this.WEIGHT = (weight != null || weight > 0) ? weight : 1;
        this.VISITED_MODIFIER = visitModifier;
        this.RELATED_ACTION = relatedAction;
    }
    
    @Override
    public Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        if(actions.size() == 1) //if there's only one action, pick that one
            return new ArrayList<>(actions).get(0);
        
        List<Action> filteredActions = new ArrayList(actions);
        Action prevAction = state.get(Tags.PreviousAction, null);

        if(prevAction == null)
            return selectRandomAction(actions);

        //filter by relation
        filteredActions = filterByRelation(prevAction, filteredActions, RELATED_ACTION);

        //filter by visit modifier
        if(VISITED_MODIFIER != null)
            filteredActions = filterByVisitModifier(VISITED_MODIFIER, filteredActions, actionsExecuted);
        
        if(filteredActions.isEmpty()) //if nothing has made it through the filters
            return selectRandomAction(actions); //default to picking randomly
        else if (filteredActions.size() == 1)
            return new ArrayList<>(filteredActions).get(0);
        else
            return selectRandomAction(filteredActions); //pick randomly from the filtered list
    }

    @Override
    public String toString()
    { return WEIGHT + " select-random " + RELATED_ACTION.toString(); }
}