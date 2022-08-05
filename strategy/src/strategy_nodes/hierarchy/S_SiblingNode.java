package strategy_nodes.hierarchy;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import strategy_nodes.base_nodes.BaseActionNode;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class S_SiblingNode extends BaseActionNode
{
    public S_SiblingNode(int weight, String name)
    {
        this.name = name;
        this.WEIGHT = weight;
    }
    
    protected boolean WidgetsAreSiblings(Widget widget1, Widget widget2)
    {
        return widget1.parent().equals(widget2.parent());
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
            if(WidgetsAreSiblings(prevWidget, widget))
                filteredActions.add(action);
        }
        
        if(filteredActions.size() > 0)
            return selectRandomAction(filteredActions);
        else
            return selectRandomAction(actions);
    }
}
