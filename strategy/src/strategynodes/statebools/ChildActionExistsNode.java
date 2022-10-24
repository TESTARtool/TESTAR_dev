package strategynodes.statebools;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import strategynodes.basenodes.BaseBooleanNode;

import java.util.Map;
import java.util.Set;

public class ChildActionExistsNode extends BaseBooleanNode
{
    private String name;
    
    public ChildActionExistsNode(String name)
    {
        this.name = name;
    }
    
    protected boolean WidgetIsAChild(Widget parent, Widget child)
    {
        return child.parent().equals(parent);
    }
    @Override
    public Boolean GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        Widget prevWidget = state.get(Tags.PreviousAction).get(Tags.OriginWidget);
        
        for(Action action : actions)
        {
            Widget widget = action.get(Tags.OriginWidget);
            if(WidgetIsAChild(prevWidget, widget))
                return true;
        }
        return false;
    }
    
    @Override
    public String toString() {return name;}
}