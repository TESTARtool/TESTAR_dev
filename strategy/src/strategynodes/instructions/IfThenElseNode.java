package strategynodes.instructions;

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseNode;

import java.util.ArrayList;
import java.util.Set;
import java.util.StringJoiner;

public class IfThenElseNode extends BaseNode<Action> implements ActionNode
{
    private final Weight weight;
    private final BaseNode<Boolean>     ifChild;
    private final ListNode              thenChild;
    private final ListNode              elseChild;

    public IfThenElseNode(int weight, BaseNode<Boolean> ifChild, ListNode thenChild)
    {
        this.weight = new Weight(weight);
        this.ifChild =      ifChild;
        this.thenChild =    thenChild;
        this.elseChild =    null;
    }
    
    public IfThenElseNode(int weight, BaseNode<Boolean> ifChild,  ListNode thenChild, ListNode elseChild)
    {
        this.weight = new Weight(weight);
        this.ifChild =      ifChild;
        this.thenChild =    thenChild;
        this.elseChild =    elseChild;
    }
    
    @Override
    public Action getResult(State state, Set<Action> actions, MultiMap<String, Object> actionsExecuted, ArrayList<String> operatingSystems)
    {
            if (ifChild.getResult(state, actions, actionsExecuted, operatingSystems))
                return thenChild.getResult(state, actions, actionsExecuted, operatingSystems);
            else if (elseChild != null)
                return elseChild.getResult(state, actions, actionsExecuted, operatingSystems);
            else return selectRandomAction(actions);
    }

    @Override
    public int GetWeight()
    {
        return weight.GetWeight();
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(weight + " IF " + ifChild.toString());
        joiner.add("THEN " + thenChild.toString());
        if(elseChild != null)
            joiner.add("ELSE " + elseChild.toString());
        return joiner.toString();
    }
}