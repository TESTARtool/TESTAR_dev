package strategynodes.actions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.actions.WdSubmitAction;
import strategynodes.basenodes.BaseActionNode;

import java.util.Map;
import java.util.Set;

public class S_SubmitAction extends BaseActionNode
{
    public S_SubmitAction(int weight, String name)
    {
        this.name = name;
        this.WEIGHT = weight;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo
    {
        for(Action action : actions)
        {
            if(action instanceof WdSubmitAction) //if it is a submit action
                return action;
        }
        return selectRandomAction(actions); // if there is no submit action, pick randomly
    }
}