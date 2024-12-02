package strategynodes.instructions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.BaseNode;
import strategynodes.conditions.BooleanNode;
import strategynodes.data.Weight;

import java.util.Set;
import java.util.StringJoiner;

public class IfThenElseNode extends BaseNode implements ActionNode
{
    private final Weight weight;
    private final BooleanNode           ifChild;
    private final ListNode              thenChild;
    private final ListNode              elseChild;

    public IfThenElseNode(int weight, BooleanNode ifChild, ListNode thenChild)
    {
        this.weight = new Weight(weight);
        this.ifChild =      ifChild;
        this.thenChild =    thenChild;
        this.elseChild =    null;
    }
    
    public IfThenElseNode(int weight, BooleanNode ifChild,  ListNode thenChild, ListNode elseChild)
    {
        this.weight = new Weight(weight);
        this.ifChild =      ifChild;
        this.thenChild =    thenChild;
        this.elseChild =    elseChild;
    }
    
    @Override
    public Action getResult(State state, Set<Action> actions)
    {
            if (ifChild.getResult(state, actions))
                return thenChild.getResult(state, actions);
            else if (elseChild != null)
                return elseChild.getResult(state, actions);
            else return selectRandomAction(actions);
    }

    @Override
    public int GetWeight()
    { return weight.GetWeight(); }

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