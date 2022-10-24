package strategynodes.statebools;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import strategynodes.basenodes.BaseBooleanNode;

import java.util.Map;
import java.util.Set;

public class ChildOrSiblingActionExistsNode extends BaseBooleanNode
{
    private String name;
    
    public ChildOrSiblingActionExistsNode(String name)
    {
        this.name = name;
    }
    
    protected boolean WidgetIsChildOrSiblings(Widget widget1, Widget widget2)
    {
        return widget1.parent().equals(widget2) || widget2.parent().equals(widget1) || widget1.parent().equals(widget2.parent());
    }
    @Override
    public Boolean GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        Widget prevWidget = state.get(Tags.PreviousAction).get(Tags.OriginWidget);
        
        for(Action action : actions)
        {
            Widget widget = action.get(Tags.OriginWidget);
            if(WidgetIsChildOrSiblings(prevWidget, widget))
                return true;
        }
        return false;
    }
    
    @Override
    public String toString() {return name;}
}