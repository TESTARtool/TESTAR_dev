package strategynodes.action_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import strategynodes.RelatedAction;
import strategynodes.VisitedModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectRelationNode extends BaseActionNode
{
    private final VisitedModifier   VISITED_MODIFIER;
    private final RelatedAction RELATED_ACTION;
    
    public SelectRelationNode(Integer weight, VisitedModifier visitedModifier, RelatedAction relatedAction)
    {
        this.WEIGHT = (weight != null || weight > 0) ? weight : 1;
        this.VISITED_MODIFIER = visitedModifier;
        this.RELATED_ACTION = relatedAction;
    }
    
    @Override
    public Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works
    {
        if(actions.size() == 1) //if there's only one action, pick that one
            return new ArrayList<>(actions).get(0);
        
        List<Action> filteredActions = new ArrayList(actions);
        
        Action prevAction = state.get(Tags.PreviousAction, null);
        if(prevAction != null)
        {
            filteredActions = filterByRelation(prevAction, filteredActions, RELATED_ACTION);
            
            if(VISITED_MODIFIER != null)
                filteredActions = filterByVisitedModifier(VISITED_MODIFIER, filteredActions, actionsExecuted);
        }
        else
            return selectRandomAction(actions);
        
        if(filteredActions.isEmpty()) //if nothing has made it through the filters
            return selectRandomAction(actions); //default to picking randomly
        else if (filteredActions.size() == 1)
            return new ArrayList<>(actions).get(0);
        else
            return selectRandomAction(filteredActions); //pick randomly from the filtered list
    }
    
    @Override
    public String toString()
    { return WEIGHT + " select-random " + RELATED_ACTION.toString(); }
}