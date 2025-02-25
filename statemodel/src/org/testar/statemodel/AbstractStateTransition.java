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

import org.testar.statemodel.persistence.Persistable;

public class AbstractStateTransition implements Persistable {

    // a transition is a trinity consisting of two states as endpoints and an action to tie these together
    private AbstractState sourceState;
    private AbstractState targetState;
    private AbstractAction action;

    /**
     * Constructor
     * @param sourceState
     * @param targetState
     * @param action
     */
    public AbstractStateTransition(AbstractState sourceState, AbstractState targetState, AbstractAction action) {
        this.sourceState = sourceState;
        this.targetState = targetState;
        this.action = action;
    }

    /**
     * Get the id for the source state of this transition
     * @return
     */
    public String getSourceStateId() {
        return sourceState.getStateId();
    }

    /**
     * Get the id for the target state of this transition
     * @return
     */
    public String getTargetStateId() {
        return targetState.getStateId();
    }

    /**
     * Get the id for the executed action in this transition
     * @return
     */
    public String getActionId() {
        return action.getActionId();
    }

    /**
     * Get the source state for this transition
     * @param sourceState
     */
    public void setSourceState(AbstractState sourceState) {
        this.sourceState = sourceState;
    }

    /**
     * Get the target state for this transition
     * @param targetState
     */
    public void setTargetState(AbstractState targetState) {
        this.targetState = targetState;
    }

    /**
     * Get the action for this transition
     * @param action
     */
    public void setAction(AbstractAction action) {
        this.action = action;
    }

    /**
     * Get the source state for this transition
     * @return
     */
    public AbstractState getSourceState() {
        return sourceState;
    }

    /**
     * Get the target state for this transition
     * @return
     */
    public AbstractState getTargetState() {
        return targetState;
    }

    /**
     * Get the executed action for this transition
     * @return
     */
    public AbstractAction getAction() {
        return action;
    }

    @Override
    public boolean canBeDelayed() {
        return false;
    }
}
