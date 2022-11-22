package parsing.treenodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Map;
import java.util.Set;

public class SelectRandomAction_Node extends BaseAction_Node
{
    private Visited    VISITED;
    private Filter     FILTER;
    private ActionType ACTIONTYPE;
    
    public SelectRandomAction_Node(Integer weight, Visited visited, Filter filter, ActionType actionType)
    {
        this.WEIGHT = (weight != null || weight > 0) ? weight : 1;
        this.VISITED = visited;
        this.FILTER = filter;
        this.ACTIONTYPE = actionType;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: redo code
    {
//        if(actions.size() == 1)
//            return new ArrayList<>(actions).get(0);
//
//        ArrayList filteredActions = new ArrayList();
//        if(actions.size() > actionsExecuted.size()) // there are some unexecuted actions
//        {
//            for(Action action : actions)
//            {
//                if(!actionsExecuted.containsKey(action.get(Tags.AbstractIDCustom)) && ActionType.RoleMatchesType(action, actionType))
//                    filteredActions.add(action);
//            }
//        }
//        if(filteredActions.size() == 0)
//            return selectRandomAction(actions);
//        else if (filteredActions.size() == 1)
//            return new ArrayList<>(actions).get(0);
//        else
//            return selectRandomAction(filteredActions);
        return null;
    }
    
    @Override
    public String toString()
    {
        String string = WEIGHT + " select-random ";
        if(VISITED != null) string += VISITED.toString();
        if(FILTER != null) string += FILTER.toString() + " " + ACTIONTYPE.toString();
        return string;
    }
}