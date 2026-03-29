/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel;

import java.util.Objects;

public class ConcreteAction extends ModelWidget {

    /**
     * The concrete action id.
     */
    private final String actionId;

    /**
     * The abstract action that abstracts this concrete action.
     */
    private final AbstractAction abstractAction;

    /**
     * Constructor.
     * @param actionId
     */
    public ConcreteAction(String actionId, AbstractAction abstractAction) {
        super(Objects.requireNonNull(actionId, "ConcreteAction ID cannot be null"));
        if (actionId.trim().isEmpty()) {
            throw new IllegalArgumentException("ConcreteAction ID cannot be empty or blank");
        }
        this.actionId = actionId;
        this.abstractAction = Objects.requireNonNull(abstractAction, "AbstractAction cannot be null");
    }

    public String getActionId() {
        return actionId;
    }

    public AbstractAction getAbstractAction() {
        return abstractAction;
    }
}
