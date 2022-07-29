package strategy_nodes.terminals;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.base_nodes.BaseIntegerNode;

import java.util.Map;
import java.util.Set;

public class IntegerNode extends BaseIntegerNode
{
    private Integer NUMBER;
    
    public IntegerNode(int number) {this.NUMBER = number; }
    
    @Override
    public Integer GetResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted) {return NUMBER;} //add enum code later
    
    @Override
    public String toString() { return String.valueOf(NUMBER); }
    
    public int GetNumber() {return NUMBER;}
}