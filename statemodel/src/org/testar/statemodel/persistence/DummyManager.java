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

package org.testar.statemodel.persistence;


import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.statemodel.sequence.Sequence;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.statemodel.sequence.SequenceNode;
import org.testar.statemodel.sequence.SequenceStep;
import org.testar.statemodel.*;

/**
 * This class serves as a black hole for when persistance is not enabled
 */
public class DummyManager implements PersistenceManager, StateModelEventListener {

    @Override
    public void eventReceived(StateModelEvent event) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void persistAbstractState(AbstractState abstractState) {

    }

    @Override
    public void persistAbstractAction(AbstractAction abstractAction) {

    }

    @Override
    public void persistAbstractStateTransition(AbstractStateTransition abstractStateTransition) {

    }

    @Override
    public void initAbstractStateModel(AbstractStateModel abstractStateModel) {

    }

    @Override
    public void persistConcreteState(ConcreteState concreteState) {

    }

    @Override
    public void persistConcreteStateTransition(ConcreteStateTransition concreteStateTransition) {

    }

    @Override
    public void persistSequence(Sequence sequence) {

    }

    @Override
    public void setListening(boolean listening) {

    }

    @Override
    public void initSequenceManager(SequenceManager sequenceManager) {

    }

    @Override
    public void persistSequenceNode(SequenceNode sequenceNode) {

    }

    @Override
    public void persistSequenceStep(SequenceStep sequenceStep) {

    }

    @Override
    public boolean modelIsDeterministic(AbstractStateModel abstractStateModel) {
        return true;
    }

    @Override
    public int getNrOfNondeterministicActions(AbstractStateModel abstractStateModel) {
        return 0;
    }
}
