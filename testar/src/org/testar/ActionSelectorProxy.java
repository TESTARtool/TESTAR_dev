package org.testar;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Set;

public class ActionSelectorProxy implements IActionSelector, IActionExecutor, IActionDerive{

    private final Object selector;

    public ActionSelectorProxy(Object selector) {
        this.selector = selector;
    }

    @Override
    public Set<Action> deriveActions(Set<Action> actions) {
        if (selector instanceof IActionDerive){
            return ((IActionDerive) selector).deriveActions(actions);
        }
        return actions;
    }

    @Override
    public void executeAction(Action action) {
        if (selector instanceof IActionExecutor){
            ((IActionExecutor) selector).executeAction(action);
        }
    }

    @Override
    public Action selectAction(State state, Set<Action> actions) {
        if (selector instanceof IActionSelector){
            return ((IActionSelector) selector).selectAction(state, actions);
        }
        return null;
    }
}
