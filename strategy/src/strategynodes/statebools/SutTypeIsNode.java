package strategynodes.statebools;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.basenodes.BaseBooleanNode;
import strategynodes.terminals.SutType;

import java.util.Map;
import java.util.Set;

public class SutTypeIsNode extends BaseBooleanNode
{
    private String name;
    private SutType sutType;
    
    public SutTypeIsNode(String name, SutType operatingSystem)
    {
        this.name = name;
        this.sutType = operatingSystem;
    }
    
    @Override
    public Boolean GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        return null; //todo
    }
    
    @Override
    public String toString() {return name + " " + sutType.toString();}
}
