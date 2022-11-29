package strategynodes.action_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import strategynodes.RelatedAction;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class SelectByRelationNode extends BaseActionNode
{
    private final RelatedAction RELATED_ACTION;
    
    public SelectByRelationNode(Integer weight, RelatedAction relatedAction)
    {
        this.WEIGHT         = (weight != null || weight > 0) ? weight : 1;
        this.RELATED_ACTION = relatedAction;
    }
    
    private boolean WidgetsAreParentAndChild(Widget parent, Widget child)
    { return child.parent().equals(parent); }
    
    private boolean WidgetsAreSiblings(Widget widget1, Widget widget2)
    { return widget1.parent().equals(widget2.parent()); }
    
    @Override
    public Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: check if it works
    {
        if(actions.size() == 1)
            return new ArrayList<Action>(actions).get(0);
        
        Widget            prevWidget      = state.get(Tags.PreviousAction).get(Tags.OriginWidget);
        ArrayList<Action> filteredActions = new ArrayList<>();
        
        for(Action action : actions)
        {
            Widget widget = action.get(Tags.OriginWidget);
            if((RELATED_ACTION == RelatedAction.CHILD && WidgetsAreParentAndChild(prevWidget, widget)) ||
               (RELATED_ACTION == RelatedAction.SIBLING && WidgetsAreSiblings(prevWidget, widget)) ||
               (RELATED_ACTION == RelatedAction.SIBLING_OR_CHILD && (WidgetsAreParentAndChild(prevWidget, widget) || WidgetsAreSiblings(prevWidget, widget))))
                filteredActions.add(action);
        }
        
        if(filteredActions.size() > 0)
            return selectRandomAction(filteredActions);
        else
            return selectRandomAction(actions);
    }
    
    @Override
    public String toString()
    { return WEIGHT + " select-by-relation " + RELATED_ACTION.toString(); }
}