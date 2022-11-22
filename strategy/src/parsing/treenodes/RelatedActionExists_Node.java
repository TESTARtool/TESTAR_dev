package parsing.treenodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.basenodes.BaseStrategyNode;

import java.util.Map;
import java.util.Set;

public class RelatedActionExists_Node extends BaseStrategyNode<Action>
{
    private Relation relation;
    
    public RelatedActionExists_Node(Relation relation)
    {
        this.relation = relation;
    }
    
    @Override
    public Action GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        //todo: redo code
        return null;
    }
    
    @Override
    public String toString()
    { return relation.toString() + " exist"; }
}