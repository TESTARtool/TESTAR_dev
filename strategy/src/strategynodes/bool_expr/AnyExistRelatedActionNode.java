package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import strategynodes.RelatedAction;
import strategynodes.BaseStrategyNode;
import strategynodes.VisitedModifier;

import java.util.Map;
import java.util.Set;

public class AnyExistRelatedActionNode extends BaseStrategyNode<Boolean>
{
    private final VisitedModifier VISITED_MODIFIER;
    private final     RelatedAction   RELATED_ACTION;
    
    public AnyExistRelatedActionNode(VisitedModifier visitedModifier, RelatedAction relatedAction)
    {
        this.VISITED_MODIFIER = visitedModifier;
        this.RELATED_ACTION = relatedAction;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: add modifier
    {
        Action prevAction = state.get(Tags.PreviousAction, null);
        
        if(prevAction != null)
            return validRelationExists(prevAction, actions, RELATED_ACTION);
        
        return false;
    }
    
    @Override
    public String toString()
    {
        String string = "any-exist";
        if(VISITED_MODIFIER != null) string += " " + VISITED_MODIFIER;
        string += RELATED_ACTION.toString();
        return string;
    }
}