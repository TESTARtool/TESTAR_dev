/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import java.util.List;
import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.action.ResolvedAction;
import org.testar.core.state.SUT;
import org.testar.core.state.State;

final class DefaultPlatformSession implements PlatformSession {

    private final PlatformServices services;
    private final SUT system;

    DefaultPlatformSession(PlatformServices services, SUT system) {
        this.services = Assert.notNull(services);
        this.system = Assert.notNull(system);
        this.services.stateModelService().notifyTestSequencedStarted();
    }

    @Override
    public SUT system() {
        return system;
    }

    @Override
    public State getState() {
        return services.stateService().getState(system);
    }

    @Override
    public Set<Action> getDerivedActions() {
        State state = services.stateService().getState(system);
        Set<Action> actions = services.actionDerivationService().deriveActions(system, state);
        services.stateModelService().notifyNewStateReached(state, actions);
        return actions;
    }

    @Override
    public ResolvedAction resolveAction(List<String> arguments) {
        return services.actionResolver().resolve(getDerivedActions(), Assert.notNull(arguments));
    }

    @Override
    public boolean executeAction(Action action) {
        services.stateModelService().notifyActionExecution(action);
        return services.actionExecutionService().executeAction(system, getState(), Assert.notNull(action));
    }

    @Override
    public void stopSystem() {
        services.stateModelService().notifyTestSequenceStopped();
        services.systemService().stopSystem(system);
    }

    @Override
    public void close() {
        closeStateModelService();
        closeStateService();
    }

    private void closeStateService() {
        if (services.stateService() instanceof AutoCloseable) {
            try {
                ((AutoCloseable) services.stateService()).close();
            } catch (Exception exception) {
                throw new IllegalStateException("Unable to close state service", exception);
            }
        }
    }

    private void closeStateModelService() {
        try {
            services.stateModelService().notifyTestingEnded();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to close state model service", exception);
        }
    }
}
