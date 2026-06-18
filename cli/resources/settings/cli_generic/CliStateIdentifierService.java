/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

import org.testar.core.Assert;
import org.testar.core.service.StateIdentifierService;
import org.testar.core.state.State;

public final class CliStateIdentifierService implements StateIdentifierService {

    private final StateIdentifierService delegate;

    public CliStateIdentifierService(StateIdentifierService delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public State identifyState(State state) {
        return delegate.identifyState(Assert.notNull(state));
    }
}
