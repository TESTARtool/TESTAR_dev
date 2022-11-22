package parsing.treenodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Map;
import java.util.Set;

public class SelectByRelation_Node extends BaseAction_Node
{
    private Relation RELATION;
    
    public SelectByRelation_Node(Integer weight, Relation relation)
    {
        this.WEIGHT   = (weight != null || weight > 0) ? weight : 1;
        this.RELATION = relation;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) //todo: redo code
    {
        return null;
    }
    
    @Override
    public String toString()
    { return WEIGHT + " select-by-relation " + RELATION.toString(); }
}