package strategynodes.conditions;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.StrategyNode;

import java.util.Set;

public interface BooleanNode extends StrategyNode<Boolean>
{
    Boolean getResult(State state, Set<Action> actions);
}
