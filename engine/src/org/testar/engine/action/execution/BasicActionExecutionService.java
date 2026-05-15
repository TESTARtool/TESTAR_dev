/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.execution;

import org.testar.core.action.Action;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.util.Util;

/**
 * Default reusable action execution service.
 */
public final class BasicActionExecutionService implements ActionExecutionService {

    private static final double DEFAULT_ACTION_DURATION = 0.0d;
    private static final double DEFAULT_TIME_TO_WAIT_AFTER_ACTION = 0.0d;
    private static final double MINIMUM_PRE_ACTION_WAIT = 0.1d;

    private final double actionDuration;
    private final double timeToWaitAfterAction;

    public BasicActionExecutionService() {
        this(DEFAULT_ACTION_DURATION, DEFAULT_TIME_TO_WAIT_AFTER_ACTION);
    }

    public BasicActionExecutionService(double actionDuration, double timeToWaitAfterAction) {
        this.actionDuration = actionDuration;
        this.timeToWaitAfterAction = timeToWaitAfterAction;
    }

    @Override
    public boolean executeAction(SUT system, State state, Action action) {
        try {
            Util.pause(calculatePreActionWait());
            action.run(system, state, actionDuration);
            return true;
        } catch (ActionFailedException exception) {
            return false;
        }
    }

    private double calculatePreActionWait() {
        if (timeToWaitAfterAction <= 0.0d) {
            return MINIMUM_PRE_ACTION_WAIT;
        }

        return timeToWaitAfterAction / 2.0d;
    }
}
