package strategynodes.conditions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.StrategyNode;

import java.util.Set;

public interface IntegerNode extends StrategyNode<Integer>
{
    Integer getResult(State state, Set<Action> actions);
}
