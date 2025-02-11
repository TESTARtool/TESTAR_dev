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
import org.testar.statemodel.ConcreteState;
import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.statemodel.event.StateModelEventType;

import java.util.Set;

public class SequenceManager {

    /**
     * For the current abstraction level, this indicates the nr of the last sequence that has run or is running.
     */
    private int currentSequenceNr = 0;

    /**
     * The current test sequence that is being run.
     */
    private Sequence currentSequence;

    /**
     * The abstraction level identifier that indicates the state model that we are testing against.
     */
    private String modelIdentifier;

    /**
     * A set of event listeners to notify of changes in the sequence.
     */
    private Set<StateModelEventListener> eventListeners;

    /**
     * Constructor
     * @param eventListeners
     */
    public SequenceManager(Set<StateModelEventListener> eventListeners, String modelIdentifier) {
        this.eventListeners = eventListeners;
        this.modelIdentifier = modelIdentifier;
        init();
    }

    private void init() {
        // initialization code here
        emitEvent(new StateModelEvent(StateModelEventType.SEQUENCE_MANAGER_INITIALIZED, this));
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

    /**
     * Start a new test sequence.
     */
    public void startNewSequence() {
        if (currentSequence != null && currentSequence.isRunning()) {
            // sequence wasn't terminated, so terminate it now
            currentSequence.stop();
        }

        currentSequence = new Sequence(++currentSequenceNr, eventListeners, modelIdentifier);
        currentSequence.start();
    }

    /**
     * Stop the currently executing test sequence. This particular sequence can no longer be restarted after it has been stopped.
     */
    public void stopSequence() {
        currentSequence.setSequenceVerdict(SequenceVerdict.COMPLETED_SUCCESFULLY);
        currentSequence.stop();
    }

    public void notifyInterruptionByUser() {
        currentSequence.setSequenceVerdict(SequenceVerdict.INTERRUPTED_BY_USER);
        currentSequence.stop();
    }

    public void notifyInterruptionBySystem(String message) {
        currentSequence.setSequenceVerdict(SequenceVerdict.INTERRUPTED_BY_ERROR);
        currentSequence.setTerminationMessage(message);
        currentSequence.stop();
    }

    /**
     * Use this method to notify the sequence manager that a new state was reached in the sequence.
     * @param concreteState the concrete state reached
     * @param concreteAction (Optionally) a concrete action that was executed.
     */
    public void notifyStateReached(ConcreteState concreteState, ConcreteAction concreteAction, SequenceError ...sequenceErrors) {
        if (concreteState == null || currentSequence == null || !currentSequence.isRunning()) {
            return;
        }

        currentSequence.addNode(concreteState, concreteAction, sequenceErrors);
    }

    /**
     * Notify that an error has occurred in the current state, which will have to be stored on the current sequence node.
     * @param errorMessage
     */
    public void notifyErrorInCurrentState(String errorMessage) {
        SequenceNode lastNode = currentSequence.getLastNode();
        if (lastNode != null) {
            lastNode.addErrorMessage(errorMessage);
        }
    }

}
