package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.devices.KBKeys;

import java.util.ArrayList;

public class SnEscape extends StrategyNodeAction {

    public SnEscape(ArrayList<StrategyNode> children) {
        super(children);
    }

    @Override
    public Action getAction(State state) {
        return new AnnotatingActionCompiler().hitKey(KBKeys.VK_ESCAPE);
    }

}
