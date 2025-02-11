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

package org.testar.statemodel.actionselector;

import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.AbstractStateTransition;
import org.testar.statemodel.exceptions.ActionNotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class UnvisitedActionsSelector implements ActionSelector {

    // the maximum nr of transitions to look ahead for unvisited actions
    private final int MAX_HOPS = 4;

    @Override
    public void notifyNewSequence() {
    	//
    }

    @Override
    public AbstractAction selectAction(AbstractState currentState, AbstractStateModel abstractStateModel) throws ActionNotFoundException{
        Set<AbstractAction> unvisitedActions = getUnvisitedActions(currentState, abstractStateModel, MAX_HOPS);
        if (unvisitedActions.size() == 0) {
            throw new ActionNotFoundException();
        }
        else if (unvisitedActions.size() == 1) {
            return unvisitedActions.iterator().next();
        }
        else {
            // multiple actions
            // in this selection algorithm, we just choose a random one.
            long graphTime = System.currentTimeMillis();
            Random rnd = new Random(graphTime);
            return (new ArrayList<>(unvisitedActions)).get(rnd.nextInt(unvisitedActions.size()));
        }
    }

    /**
     * This helper method will recursively look for unvisited actions
     * @param state
     * @param abstractStateModel
     * @param nrOfHopsLeft
     * @return
     */
    private Set<AbstractAction> getUnvisitedActions(AbstractState state, AbstractStateModel abstractStateModel, int nrOfHopsLeft) {
        Set<AbstractAction> actions = state.getUnvisitedActions();
        if (!actions.isEmpty() || nrOfHopsLeft == 0) {
            return actions;
        }
        actions = new HashSet<>();

        // go one layer deeper
        // the object is to return the action for this state that will lead to other states with
        // unvisited actions
        // the fact that all the actions are visited means we have transitions for them
        //@todo ad the following algorithm isn't perfect, as it will follow transitions for each action until it reaches
        //@todo an unvisited action. For one transition this will be the next state, for some it will be 3 down.
        //@todo Ideally the algorithm should go one level at a time
        // it's meant as a demo of what is possible
        for (AbstractStateTransition transition:abstractStateModel.getOutgoingTransitionsForState(state.getStateId())) {
            AbstractState targetState = transition.getTargetState();
            if (getUnvisitedActions(targetState, abstractStateModel, nrOfHopsLeft - 1).size() > 0) {
                actions.add(transition.getAction());
            }
        }
        return actions;
    }


}
