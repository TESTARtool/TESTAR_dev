/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2023-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2023-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.engine.action.selection;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.state.State;
import org.testar.engine.action.selection.random.RandomActionSelector;

import java.util.Set;

/**
 * Engine-side action selector service that uses the chosen selector strategy
 * with a fallback to the random selector.
 */
public class FallbackActionSelectorService implements ActionSelectorService {

    private final ActionSelectorService delegate;
    private final ActionSelectorService fallback;

    public FallbackActionSelectorService(ActionSelectorService delegate) {
        this(delegate, new RandomActionSelector());
    }

    public FallbackActionSelectorService(ActionSelectorService delegate, ActionSelectorService fallback) {
        this.delegate = Assert.notNull(delegate);
        this.fallback = Assert.notNull(fallback);
    }

    public static ActionSelectorService compose(ActionSelectorService delegate) {
        return new FallbackActionSelectorService(delegate);
    }

    public static ActionSelectorService compose(ActionSelectorService delegate, ActionSelectorService fallback) {
        return new FallbackActionSelectorService(delegate, fallback);
    }

    @Override
    public Action selectAction(State state, Set<Action> actions) {
        Action selectedAction = delegate.selectAction(state, actions);
        if (selectedAction != null) {
            return selectedAction;
        }
        return fallback.selectAction(state, actions);
    }
}
