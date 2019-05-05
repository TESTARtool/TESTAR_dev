package nl.ou.testar.genetic.programming.strategy.actions;

import nl.ou.testar.genetic.programming.strategy.StrategyGuiState;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeAction;
import org.fruit.alayer.Action;
import org.fruit.alayer.actions.AnnotatingActionCompiler;

import java.util.List;
import java.util.Optional;

import static java.awt.event.KeyEvent.VK_ESCAPE;

public class SnEscape extends StrategyNodeAction {

    public SnEscape(final List<StrategyNode> children) {
        super(children);
    }

    @Override
    public Optional<Action> getAction(final StrategyGuiState state) {
        return Optional.of(new AnnotatingActionCompiler().hitKey(VK_ESCAPE));
    }

}
