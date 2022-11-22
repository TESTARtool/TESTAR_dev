package parsing.treenodes;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.basenodes.BaseStrategyNode;

import java.util.Map;
import java.util.Set;

public class Sut_Node extends BaseStrategyNode<Boolean>
{
    private Filter  FILTER;
    private SutType SUTTYPE;
    
    public Sut_Node(Filter filter, SutType sutType)
    {
        this.FILTER = filter;
        this.SUTTYPE = sutType;
    }
    
    @Override
    public Boolean GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        return null; //todo
    }
    
    @Override
    public String toString() {return "sut " + FILTER.toString() + " " + SUTTYPE.toString();}
}
