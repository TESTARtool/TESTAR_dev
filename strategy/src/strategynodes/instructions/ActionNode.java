package strategynodes.instructions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.StrategyNode;

import java.util.Set;

public interface ActionNode extends StrategyNode<Action>
{
    int GetWeight();
    Action getResult(State state, Set<Action> actions);
}
