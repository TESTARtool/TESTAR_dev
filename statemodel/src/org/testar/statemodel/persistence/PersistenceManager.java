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

import org.testar.statemodel.sequence.Sequence;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.statemodel.sequence.SequenceNode;
import org.testar.statemodel.sequence.SequenceStep;
import org.testar.statemodel.*;
import org.testar.statemodel.persistence.orientdb.entity.EntityManager;

public interface PersistenceManager {

    // the data will be stored instantly as requests reach the  persistence manager
    String DATA_STORE_MODE_INSTANT = "instant";

    // the data will not be stored until a test sequence has finished
    String DATA_STORE_MODE_DELAYED = "delayed";

    // some data will be stored instantly and some will be stored after the sequence has finished
    String DATA_STORE_MODE_HYBRID = "hybrid";

    // sometimes we do not want to persist data
    String DATA_STORE_MODE_NONE = "none";

    /**
     * This method persists an entire state model.
     */
    void shutdown();

    /**
     * This method persists an abstract state.
     * @param abstractState
     */
    void persistAbstractState(AbstractState abstractState);

    /**
     * This method persists an abstract action
     * @param abstractAction
     */
    void persistAbstractAction(AbstractAction abstractAction);

    /**
     * This method persists an abstract state transition
     * @param abstractStateTransition
     */
    void persistAbstractStateTransition(AbstractStateTransition abstractStateTransition);

    /**
     * This method persists a concrete state.
     * @param concreteState
     */
    void persistConcreteState(ConcreteState concreteState);

    /**
     * This method persists a concrete action.
     * @param concreteStateTransition
     */
    void persistConcreteStateTransition(ConcreteStateTransition concreteStateTransition);

    /**
     * This method initializes and abstract state model before use in Testar.
     * @param abstractStateModel
     */
    void initAbstractStateModel(AbstractStateModel abstractStateModel);

    /**
     * This method persists a sequence to the orient data store.
     * @param sequence
     */
    void persistSequence(Sequence sequence);

    /**
     * This method initializes a sequence manager implementation with data from the data store.
     * @param sequenceManager
     */
    void initSequenceManager(SequenceManager sequenceManager);

    /**
     * This method persists a sequence node to the data store.
     * @param sequenceNode
     */
    void persistSequenceNode(SequenceNode sequenceNode);

    /**
     * This method persists a sequence step to the data store.
     * @param sequenceStep
     */
    void persistSequenceStep(SequenceStep sequenceStep);

    /**
     * This method returns true if the model is deterministic, meaning no transitions lead to more than one target state.
     * @param abstractStateModel
     * @return
     */
    boolean modelIsDeterministic(AbstractStateModel abstractStateModel);

    /**
     * This method returns the nr of non-deterministic actions in the model, meaning the same actions ends in more
     * than one unique abstract states.
     * @param abstractStateModel
     * @return
     */
    public int getNrOfNondeterministicActions(AbstractStateModel abstractStateModel);

    EntityManager getEntityManager();

}
