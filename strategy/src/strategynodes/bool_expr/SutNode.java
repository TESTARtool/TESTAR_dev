package strategynodes.bool_expr;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseStrategyNode;
import strategynodes.Filter;

import java.util.Map;
import java.util.Set;

public class SutNode extends BaseStrategyNode<Boolean>
{
    private final Filter  FILTER;
    private final SutType SUT_TYPE;
    
    public SutNode(Filter filter, SutType sutType)
    {
        this.FILTER = filter;
        this.SUT_TYPE = sutType;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        return SUT_TYPE.sutIsThisType();
    }
    
    @Override
    public String toString() {return "sut " + FILTER.toString() + " " + SUT_TYPE.toString();}
}
