package strategynodes.hierarchy;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import strategynodes.basenodes.BaseActionNode;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class S_ChildNode extends BaseActionNode
{
    public S_ChildNode(int weight, String name)
    {
        this.name = name;
        this.WEIGHT = weight;
    }
    
    protected boolean WidgetIsChildOfOther(Widget parent, Widget child)
    {
        return child.parent().equals(parent);
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        if(actions.size() == 1)
            return new ArrayList<Action>(actions).get(0);
        
        Widget prevWidget = state.get(Tags.PreviousAction).get(Tags.OriginWidget);
        ArrayList<Action> filteredActions = new ArrayList<>();
        
        for(Action action : actions)
        {
            Widget widget = action.get(Tags.OriginWidget);
            if(WidgetIsChildOfOther(prevWidget, widget))
                filteredActions.add(action);
        }
        
        if(filteredActions.size() > 0)
            return selectRandomAction(filteredActions);
        else
            return selectRandomAction(actions);
    }
}
