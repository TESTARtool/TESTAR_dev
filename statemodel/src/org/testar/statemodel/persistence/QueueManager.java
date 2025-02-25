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
import org.testar.statemodel.exceptions.InvalidEventException;
import org.testar.statemodel.persistence.orientdb.entity.EntityManager;
import org.testar.statemodel.sequence.Sequence;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.statemodel.sequence.SequenceNode;
import org.testar.statemodel.sequence.SequenceStep;
import org.testar.statemodel.util.EventHelper;
import org.testar.statemodel.*;

import java.util.ArrayDeque;

public class QueueManager implements PersistenceManager, StateModelEventListener {

    /**
     * A queue holding the commands to execute
     */
    private ArrayDeque<Runnable> queue;

    /**
     * Composite persistencemanager that will do the actual work for us.
     */
    private PersistenceManager delegateManager;

    /**
     * Helper class for dealing with events
     */
    private EventHelper eventHelper;

    /**
     * Is the event listener processing events?
     */
    private boolean listening = true;

    /**
     * Is the queue manager running in hybrid mode?
     */
    private boolean hybridMode;

    public QueueManager(PersistenceManager persistenceManager, EventHelper eventHelper, boolean hybridMode) {
        delegateManager = persistenceManager;
        queue = new ArrayDeque<>();
        this.eventHelper = eventHelper;
        this.hybridMode = hybridMode;
    }

    private void processRequest(Runnable runnable, Persistable persistable) {
        if (!hybridMode || persistable.canBeDelayed()) {
            queue.add(runnable);
        }
        else {
            runnable.run();
        }
    }

    @Override
    public void shutdown() {
        if (!queue.isEmpty()) {
            int nrOfItemsProcessed = 0;
            int totalNrOfItems = queue.size();
            QueueVisualizer visualizer = new QueueVisualizer("Processing persistence queue");
            visualizer.updateMessage("Processing persistence queue : " + nrOfItemsProcessed + " / " + totalNrOfItems + " processed");
            while (!queue.isEmpty()) {
                queue.remove().run();
                nrOfItemsProcessed++;
                visualizer.updateMessage("Processing persistence queue : " + nrOfItemsProcessed + " / " + totalNrOfItems + " processed");
            }
            visualizer.stop();
        }
        delegateManager.shutdown();
    }

    @Override
    public void persistAbstractState(AbstractState abstractState) {
        processRequest(() -> delegateManager.persistAbstractState(abstractState), abstractState);
    }

    @Override
    public void persistAbstractAction(AbstractAction abstractAction) {
        processRequest(() -> delegateManager.persistAbstractAction(abstractAction), abstractAction);
    }

    @Override
    public void persistAbstractStateTransition(AbstractStateTransition abstractStateTransition) {
        processRequest(() -> delegateManager.persistAbstractStateTransition(abstractStateTransition), abstractStateTransition);
    }

    @Override
    public void persistAbstractActionAttributeUpdated(AbstractStateTransition abstractStateTransition) {
    	processRequest(() -> delegateManager.persistAbstractActionAttributeUpdated(abstractStateTransition), abstractStateTransition);
    }

    @Override
    public void persistConcreteState(ConcreteState concreteState) {
        processRequest(() -> delegateManager.persistConcreteState(concreteState), concreteState);
    }

    @Override
    public void persistConcreteStateTransition(ConcreteStateTransition concreteStateTransition) {
        processRequest(() -> delegateManager.persistConcreteStateTransition(concreteStateTransition), concreteStateTransition);
    }

    @Override
    public void initAbstractStateModel(AbstractStateModel abstractStateModel) {
        setListening(false);
        delegateManager.initAbstractStateModel(abstractStateModel);
        setListening(true);
    }

    @Override
    public void persistSequence(Sequence sequence) {
        processRequest(() -> delegateManager.persistSequence(sequence), sequence);
    }

    @Override
    public void initSequenceManager(SequenceManager sequenceManager) {
        setListening(false);
        delegateManager.initSequenceManager(sequenceManager);
        setListening(true);
    }

    @Override
    public void persistSequenceNode(SequenceNode sequenceNode) {
        processRequest(() -> delegateManager.persistSequenceNode(sequenceNode), sequenceNode);
    }

    @Override
    public void persistSequenceStep(SequenceStep sequenceStep) {
        processRequest(() -> delegateManager.persistSequenceStep(sequenceStep), sequenceStep);
    }

    @Override
    public boolean modelIsDeterministic(AbstractStateModel abstractStateModel) {
        return delegateManager.modelIsDeterministic(abstractStateModel);
    }

    @Override
    public int getNrOfNondeterministicActions(AbstractStateModel abstractStateModel) {
        return delegateManager.getNrOfNondeterministicActions(abstractStateModel);
    }

    @Override
    public void eventReceived(StateModelEvent event) {
        if (!listening) return;

        try {
            eventHelper.validateEvent(event);
        } catch (InvalidEventException e) {
            // There is something wrong with the event. we do nothing and exit
            return;
        }

        switch (event.getEventType()) {
            case ABSTRACT_STATE_ADDED:
            case ABSTRACT_STATE_CHANGED:
                persistAbstractState((AbstractState) (event.getPayload()));
                break;

            case ABSTRACT_STATE_TRANSITION_ADDED:
            case ABSTRACT_ACTION_CHANGED:
                persistAbstractStateTransition((AbstractStateTransition) (event.getPayload()));
                break;

            case ABSTRACT_ACTION_ATTRIBUTE_UPDATED:
            	persistAbstractActionAttributeUpdated((AbstractStateTransition) (event.getPayload()));
            	break;  

            case ABSTRACT_STATE_MODEL_INITIALIZED:
                initAbstractStateModel((AbstractStateModel) (event.getPayload()));
                break;

            case SEQUENCE_STARTED:
                persistSequence((Sequence) event.getPayload());
                break;

            case SEQUENCE_MANAGER_INITIALIZED:
                initSequenceManager((SequenceManager) event.getPayload());
                break;

            case SEQUENCE_NODE_ADDED:
                persistSequenceNode((SequenceNode) event.getPayload());
                break;

            case SEQUENCE_STEP_ADDED:
                persistSequenceStep((SequenceStep) event.getPayload());
        }
    }

    @Override
    public void setListening(boolean listening) {
        this.listening = listening;
    }

    @Override
    public EntityManager getEntityManager() {
    	return null;
    }
}
