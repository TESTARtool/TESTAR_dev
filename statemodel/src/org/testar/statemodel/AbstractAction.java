/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel;

import org.testar.statemodel.persistence.Persistable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AbstractAction extends AbstractEntity implements Persistable {

    // collection of concrete actions that are abstracted by this action
    private final Set<String> concreteActionIds;

    /**
     * Constructor
     * @param actionId
     */
    public AbstractAction(String actionId) {
        super(Objects.requireNonNull(actionId, "AbstractAction ID cannot be null"));
        if (actionId.trim().isEmpty()) {
            throw new IllegalArgumentException("AbstractAction ID cannot be empty or blank");
        }
        concreteActionIds = new HashSet<>();
    }

    /**
     * This method returns the action id
     * @return id for this action
     */
    public String getActionId() {
        return getId();
    }

    /**
     * This method adds a concrete action id to this abstract action
     * @param concreteActionId the concrete action id to add
     */
    public void addConcreteActionId(String concreteActionId) {
        Objects.requireNonNull(concreteActionId, "ConcreteAction ID cannot be null");
        if (concreteActionId.trim().isEmpty()) {
            throw new IllegalArgumentException("ConcreteAction ID cannot be empty or blank");
        }
        concreteActionIds.add(concreteActionId);
    }

    /**
     * This method returns the set of concrete action ids that are abstracted by this action
     * @return concrete action ids
     */
    public Set<String> getConcreteActionIds() {
        return Collections.unmodifiableSet(concreteActionIds);
    }

    @Override
    public boolean canBeDelayed() {
        return false;
    }
}
