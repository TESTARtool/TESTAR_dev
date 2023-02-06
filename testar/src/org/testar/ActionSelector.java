package org.testar;

import java.util.Set;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

/**
 * ActionSelector
 */
public interface ActionSelector<S, A> {
    A selectAction(S state, Set<A> actions);
}
