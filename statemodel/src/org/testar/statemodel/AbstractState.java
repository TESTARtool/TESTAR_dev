/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.statemodel;

import org.testar.statemodel.exceptions.ActionNotFoundException;
import org.testar.statemodel.persistence.Persistable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AbstractState extends AbstractEntity implements Persistable {

    // list of possible actions that can be executed from this state
    private final Map<String, AbstractAction> actions;
    // list of possible actions that have not yet been executed from this state
    private final Map<String, AbstractAction> unvisitedActions;
    // a list of actions that have been visited in this state
    private final Map<String, AbstractAction> visitedActions;
    // a set of strings containing the concrete state ids that correspond to this abstract state
    private final Set<String> concreteStateIds;
    // is this an initial state?
    private boolean isInitial = false;

    /**
     * Constructor
     * @param stateId
     * @param actions
     */
    public AbstractState(String stateId, Set<AbstractAction> actions) {
        super(Objects.requireNonNull(stateId, "AbstractState ID cannot be null"));
        if (stateId.trim().isEmpty()) {
            throw new IllegalArgumentException("AbstractState ID cannot be empty or blank");
        }
        this.actions = new HashMap<>();
        unvisitedActions = new HashMap<>();
        visitedActions = new HashMap<>();
        if (actions != null) {
            for (AbstractAction action : actions) {
                Objects.requireNonNull(action, "AbstractAction in actions set cannot be null");
                this.actions.put(action.getActionId(), action);
                unvisitedActions.put(action.getActionId(), action);
            }
        }
        concreteStateIds = new HashSet<>();
    }

    /**
     * Adds a concrete state id that corresponds to this abstract state.
     * @param concreteStateId the concrete id to add
     */
    public void addConcreteStateId(String concreteStateId) {
        Objects.requireNonNull(concreteStateId, "ConcreteState ID cannot be null");
        if (concreteStateId.trim().isEmpty()) {
            throw new IllegalArgumentException("ConcreteState ID cannot be empty or blank");
        }
        this.concreteStateIds.add(concreteStateId);
    }

    /**
     * This method returns the id for this abstract state
     * @return String
     */
    public String getStateId() {
        return getId();
    }

    /**
     * This method sets the given action id to status visited
     * @param action the visited action
     */
    public void addVisitedAction(AbstractAction action) {
        Objects.requireNonNull(action, "Visited AbstractAction cannot be null");
        unvisitedActions.remove(action.getActionId());
        visitedActions.put(action.getActionId(), action);
    }

    /**
     * This method returns all the possible actions that can be executed in this state
     * @return executable actions for this state
     */
    public Set<String> getActionIds() {
        return actions.keySet();
    }

    /**
     * This method returns all the abstract actions that are executable from this abstract state
     * @return
     */
    public Set<AbstractAction> getActions() {
        return Collections.unmodifiableSet(new HashSet<>(actions.values()));
    }

    /**
     * This method returns the action for a given action identifier
     * @param actionId
     * @return
     * @throws ActionNotFoundException
     */
    public AbstractAction getAction(String actionId) throws ActionNotFoundException{
        Objects.requireNonNull(actionId, "AbstractAction ID cannot be null");
        if (actionId.trim().isEmpty()) {
            throw new IllegalArgumentException("AbstractAction ID cannot be empty or blank");
        }
        if (!actions.containsKey(actionId)) {
            throw new ActionNotFoundException();
        }
        return actions.get(actionId);
    }

    /**
     * This method returns the concrete state ids that correspond to this abstract state.
     * @return
     */
    public Set<String> getConcreteStateIds() {
        return Collections.unmodifiableSet(concreteStateIds);
    }

    /**
     * This method returns the actions that have not yet been visited from this state
     * @return
     */
    public Set<AbstractAction> getUnvisitedActions() {
        return Collections.unmodifiableSet(new HashSet<>(unvisitedActions.values()));
    }

    /**
     * This method returns all the actions for this abstract state that have been visited
     * @return
     */
    public Set<AbstractAction> getVisitedActions() {
        return Collections.unmodifiableSet(new HashSet<>(visitedActions.values()));
    }

    /**
     * Returns true if this is one of the starting states of the model. False otherwise.
     */
    public boolean isInitial() {
        return isInitial;
    }

    /**
     * Set to true if this is one of the starting states of the model
     * @param initial
     */
    public void setInitial(boolean initial) {
        isInitial = initial;
    }

    @Override
    public void setModelIdentifier(String modelIdentifier) {
        super.setModelIdentifier(modelIdentifier);
        // set the identifier on the abstract actions for this state
        for (String key : actions.keySet()) {
            actions.get(key).setModelIdentifier(modelIdentifier);
            if (unvisitedActions.containsKey(key)) {
                unvisitedActions.get(key).setModelIdentifier(modelIdentifier);
            }
        }
    }

    /**
     * Add a new abstract action to the abstract state.
     * @param action
     */
    public void addNewAction(AbstractAction action) {
        Objects.requireNonNull(action, "Added AbstractAction cannot be null");
        if (!this.actions.containsKey(action.getActionId())) {
            action.setModelIdentifier(this.getModelIdentifier());
            actions.put(action.getActionId(), action);
            unvisitedActions.put(action.getActionId(), action);
        }
    }

    @Override
    public boolean canBeDelayed() {
        return false;
    }
}
