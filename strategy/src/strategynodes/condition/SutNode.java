package strategynodes.condition;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseBooleanNode;
import strategynodes.filtering.Filter;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class SutNode extends BaseBooleanNode
{
    private final Filter  FILTER;
    private final SutType SUT_TYPE;
    
    public SutNode(Filter filter, SutType sutType)
    {
        this.FILTER = filter;
        this.SUT_TYPE = sutType;
    }
    
    @Override
    public Boolean getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted, ArrayList<String> operatingSystems)
    { return SUT_TYPE.sutIsThisType(operatingSystems); }
    
    @Override
    public String toString() {return "sut " + FILTER.toString() + " " + SUT_TYPE.toString();}
}
