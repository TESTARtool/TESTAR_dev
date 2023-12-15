package strategynodes.conditions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseNode;
import strategynodes.enums.SutType;
import strategynodes.enums.Filter;

import java.util.ArrayList;
import java.util.Set;
import java.util.StringJoiner;

public class SutNode extends BaseNode implements BooleanNode
{
    private final boolean include;
    private final SutType SUT_TYPE;

    public SutNode(Filter filter, SutType sutType)
    {
        include = (filter == Filter.INCLUDE);
        this.SUT_TYPE = sutType;
    }

    @Override
    public Boolean getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems)
    { return (include == SUT_TYPE.sutIsThisType(operatingSystems)); }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("sut");
        if(include)
            joiner.add(Filter.INCLUDE.toString());
        else
            joiner.add(Filter.EXCLUDE.toString());
        joiner.add(SUT_TYPE.toString());
        return joiner.toString();
    }
}
