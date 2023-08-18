package strategynodes.instruction;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseActionNode;
import strategynodes.BaseNode;
import strategynodes.StrategyNode;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class IfThenElseNode extends BaseActionNode
{
    private BaseNode<Boolean> ifChild;
    private StrategyNode      thenChild;
    private StrategyNode      elseChild;

    public IfThenElseNode(int weight, BaseNode ifChild, StrategyNode thenChild)
    {
        assignWeight(weight);
        this.ifChild =      ifChild;
        this.thenChild =    thenChild;
        this.elseChild =    null;
    }
    
    public IfThenElseNode(int weight, BaseNode ifChild, StrategyNode thenChild, StrategyNode elseChild)
    {
        assignWeight(weight);
        this.ifChild =      ifChild;
        this.thenChild =    thenChild;
        this.elseChild =    elseChild;
    }
    
    @Override
    public Action getResult(State state, Set<Action> actions, Map<String, Integer> actionsExecuted, ArrayList<String> operatingSystems)
    {
            if (ifChild.getResult(state, actions, actionsExecuted, operatingSystems))
                return thenChild.getResult(state, actions, actionsExecuted, operatingSystems);
            else if (elseChild != null)
                return elseChild.getResult(state, actions, actionsExecuted, operatingSystems);
            else return selectRandomAction(actions);
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(Integer.toString(WEIGHT));
        joiner.add("IF " + ifChild.toString());
        joiner.add("THEN " + thenChild.toString());
        if(elseChild != null)
            joiner.add("ELSE " + elseChild.toString());
        return joiner.toString();
    }
}