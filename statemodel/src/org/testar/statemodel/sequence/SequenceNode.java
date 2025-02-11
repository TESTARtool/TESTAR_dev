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

import org.testar.statemodel.ConcreteState;
import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.statemodel.event.StateModelEventType;
import org.testar.statemodel.persistence.Persistable;

import java.time.Instant;
import java.util.Set;
import java.util.StringJoiner;

public class SequenceNode implements Persistable {

    /**
     * The date and time of creation for this node.
     */
    private Instant timestamp;

    /**
     * The unique node identifier
     */
    private String nodeId;

    /**
     * The id of the sequence this node is part of.
     */
    private String sequenceId;

    /**
     * The concrete state accessed in this node of the sequence.
     */
    private ConcreteState concreteState;

    /**
     * The ordering nr of this node in the sequence
     */
    private int nodeNr;

    /**
     * A sequence of error messages that may apply to this node.
     */
    private StringJoiner errorMessages;

    /**
     * The sequence this node is part of.
     */
    private Sequence sequence;

    /**
     * A set of event listeners to notify of changes in the sequence.
     */
    private Set<StateModelEventListener> eventListeners;

    public SequenceNode(String sequenceId, int nodeNr, ConcreteState concreteState, Sequence sequence, Set<StateModelEventListener> eventListeners) {
        timestamp = Instant.now();
        this.nodeNr = nodeNr;
        nodeId = sequenceId + '-' + nodeNr;
        this.sequenceId = sequenceId;
        this.concreteState = concreteState;
        this.sequence = sequence;
        errorMessages = new StringJoiner(", ");
        this.eventListeners = eventListeners;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getNodeId() {
        return nodeId;
    }

    public int getNodeNr() {
        return nodeNr;
    }

    public ConcreteState getConcreteState() {
        return concreteState;
    }

    @Override
    public boolean canBeDelayed() {
        return true;
    }

    /**
     * Method returns true if this is the first node in the run.
     * @return
     */
    public boolean isFirstNode() {
        return nodeNr == 1;
    }

    /**
     * Method returns the sequence id for this node.
     * @return
     */
    public String getSequenceId() {
        return sequenceId;
    }

    /**
     * Method returns the sequence for this node.
     * @return
     */
    public Sequence getSequence() {
        return sequence;
    }

    /**
     * Add a new error message to this node.
     * @param message
     */
    public void addErrorMessage(String message) {
        errorMessages.add(message);
        emitEvent(new StateModelEvent(StateModelEventType.SEQUENCE_NODE_UPDATED, this));
    }

    /**
     * This method returns true if errors were detected in the current sequence node.
     * @return
     */
    public boolean containsErrors() {
        return errorMessages.length() > 0;
    }

    /**
     * Returns the error message for this sequence node.
     * @return
     */
    public String getErrorMessage() {
        return errorMessages.toString();
    }

    /**
     * Notify our listeners of emitted events
     * @param event
     */
    private void emitEvent(StateModelEvent event) {
        for (StateModelEventListener eventListener: eventListeners) {
            eventListener.eventReceived(event);
        }
    }

}
