package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import strategynodes.RelatedAction;
import strategynodes.BaseStrategyNode;

import java.util.Map;
import java.util.Set;

public class RelatedActionExistsNode extends BaseStrategyNode<Boolean>
{
    private final RelatedAction RELATED_ACTION;
    
    public RelatedActionExistsNode(RelatedAction relatedAction)
    {
        this.RELATED_ACTION = relatedAction;
    }
    
    private boolean WidgetsAreParentAndChild(Widget parent, Widget child)
    { return child.parent().equals(parent); }
    
    private boolean WidgetsAreSiblings(Widget widget1, Widget widget2)
    { return widget1.parent().equals(widget2.parent()); }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works
    {
        Action prevAction = state.get(Tags.PreviousAction, null);
        if(prevAction != null)
        {
            Widget prevWidget = prevAction.get(Tags.OriginWidget, null);
            for (Action action : actions)
            {
                Widget widget = action.get(Tags.OriginWidget);
                if ((RELATED_ACTION == RelatedAction.CHILD && WidgetsAreParentAndChild(prevWidget, widget)) ||
                        (RELATED_ACTION == RelatedAction.SIBLING && WidgetsAreSiblings(prevWidget, widget)) ||
                        (RELATED_ACTION == RelatedAction.SIBLING_OR_CHILD && (WidgetsAreParentAndChild(prevWidget, widget) || WidgetsAreSiblings(prevWidget, widget))))
                    return true; //if one is found, no need to search further
            }
        }
        return false;
    }
    
    @Override
    public String toString()
    { return RELATED_ACTION.toString() + " exist"; }
}