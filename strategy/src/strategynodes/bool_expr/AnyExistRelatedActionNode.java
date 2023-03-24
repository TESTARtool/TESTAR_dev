package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import strategynodes.RelatedAction;
import strategynodes.BaseStrategyNode;
import strategynodes.VisitModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnyExistRelatedActionNode extends BaseStrategyNode<Boolean>
{
    private final VisitModifier VISIT_MODIFIER;
    private final     RelatedAction   RELATED_ACTION;
    
    public AnyExistRelatedActionNode(VisitModifier visitModifier, RelatedAction relatedAction)
    {
        this.VISIT_MODIFIER = visitModifier;
        this.RELATED_ACTION = relatedAction;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        Action prevAction = state.get(Tags.PreviousAction, null);
        boolean validActionFound = false;

        if(prevAction != null)
        {
//            if(VISIT_MODIFIER == null)
//                validActionFound = !actions.isEmpty(); //if there are no filters to apply, any action is valid

            List<Action> filteredActions = new ArrayList<>(actions);

            if(VISIT_MODIFIER != null) //filter by visit modifier
                filteredActions = filterByVisitModifier(VISIT_MODIFIER, filteredActions, actionsExecuted);

            //check by relation
            validActionFound = validRelationExists(prevAction, VISIT_MODIFIER, RELATED_ACTION, filteredActions, actionsExecuted);
        }
        return validActionFound;
    }
    
    @Override
    public String toString()
    {
        String string = "any-exist ";
        if(VISIT_MODIFIER != null) string += " " + VISIT_MODIFIER;
        string += RELATED_ACTION.toString();
        return string;
    }
}