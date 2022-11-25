package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import strategynodes.ActionRelation;
import strategynodes.BaseStrategyNode;

import java.util.Map;
import java.util.Set;

public class RelatedActionExists_Node extends BaseStrategyNode<Boolean>
{
    private final ActionRelation ACTION_RELATION;
    
    public RelatedActionExists_Node(ActionRelation actionRelation)
    {
        this.ACTION_RELATION = actionRelation;
    }
    
    private boolean WidgetsAreParentAndChild(Widget parent, Widget child)
    { return child.parent().equals(parent); }
    
    private boolean WidgetsAreSiblings(Widget widget1, Widget widget2)
    { return widget1.parent().equals(widget2.parent()); }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works
    {
        Widget prevWidget = state.get(Tags.PreviousAction).get(Tags.OriginWidget);
        
        for(Action action : actions)
        {
            Widget widget = action.get(Tags.OriginWidget);
            if((ACTION_RELATION == ActionRelation.CHILD && WidgetsAreParentAndChild(prevWidget, widget)) ||
               (ACTION_RELATION == ActionRelation.SIBLING && WidgetsAreSiblings(prevWidget, widget)) ||
               (ACTION_RELATION == ActionRelation.SIBLING_OR_CHILD && (WidgetsAreParentAndChild(prevWidget, widget) || WidgetsAreSiblings(prevWidget, widget))))
                return true; //if one is found, no need to search further
        }
        return false;
    }
    
    @Override
    public String toString()
    { return ACTION_RELATION.toString() + " exist"; }
}