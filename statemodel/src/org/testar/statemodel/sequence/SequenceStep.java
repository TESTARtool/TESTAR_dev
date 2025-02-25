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

package org.testar.statemodel.sequence;

import org.testar.statemodel.ConcreteAction;
import org.testar.statemodel.persistence.Persistable;

import java.time.Instant;

public class SequenceStep implements Persistable {

    /**
     * The action that was executed and is attached to this step.
     */
    private ConcreteAction concreteAction;

    /**
     * The source node for this step.
     */
    private SequenceNode sourceNode;

    /**
     * The target node for this step.
     */
    private SequenceNode targetNode;

    /**
     * A timestamp indicating the time of execution for this step.
     */
    private Instant timestamp;

    /**
     * A string offering a description of the concrete action that this step represents.
     */
    private String actionDescription;

    /**
     * A boolean value indicating whether this sequence step introduces non-determinism into the model.
     */
    private boolean nonDeterministic;

    public SequenceStep(ConcreteAction concreteAction, SequenceNode sourceNode, SequenceNode targetNode, String actionDescription) {
        this.concreteAction = concreteAction;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.actionDescription = actionDescription;
        timestamp = Instant.now();
        nonDeterministic = false;
    }

    public ConcreteAction getConcreteAction() {
        return concreteAction;
    }

    public SequenceNode getSourceNode() {
        return sourceNode;
    }

    public SequenceNode getTargetNode() {
        return targetNode;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean canBeDelayed() {
        return true;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    /**
     * This method sets whether or not the sequence step introduced non-determinism into the model.
     * @param nonDeterministic
     */
    public void setNonDeterministic(boolean nonDeterministic) {
        this.nonDeterministic = nonDeterministic;
    }

    /**
     * Returns true if the action associated with this sequence step introduced non-determinism into the model.
     * @return
     */
    public boolean isNonDeterministic() {
        return nonDeterministic;
    }
}
