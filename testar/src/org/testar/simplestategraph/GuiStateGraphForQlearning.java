/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2024 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2024 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.simplestategraph;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;

import java.util.*;

public class GuiStateGraphForQlearning {
    protected Set<QlearningGuiState> qlearningGuiStates;
    protected String startingStateAbstractId;
    protected String previousStateAbstractId;
    protected String previousActionAbstractId;
    private double R_MAX;
    private double gammaDiscount;

    public GuiStateGraphForQlearning(double R_MAX, double gammaDiscount) {
        this.R_MAX = R_MAX;
        this.gammaDiscount=gammaDiscount;

        qlearningGuiStates = new HashSet<QlearningGuiState>();
    }

    /**
     * Resetting the last action and last state to null for a new test sequence
     */
    public void startANewTestSequence(){
        previousActionAbstractId=null;
        previousStateAbstractId=null;
    }

    /**
     * returns null if action with given ID is not found
     *
     * @param actions
     * @param abstractActionId
     * @return
     */
    protected Action getActionWithAbstractId(Set<Action> actions, String abstractActionId){
        for(Action action:actions){
            // find the action with abstractId:
            if(action.get(Tags.AbstractID).equals(abstractActionId)){
                return action;
            }
        }
        System.out.println("ERROR: matching action ID not found!");
        return null;
    }

    /**
     * Returns null if not found
     *
     * @param abstractStateId
     * @return
     */
    protected QlearningGuiState getStateByAbstractId(String abstractStateId){
        for(QlearningGuiState state: qlearningGuiStates){
            if(state.getAbstractStateId().equals(abstractStateId)){
                return state;
            }
        }
        return null;
    }

    protected QlearningGuiState createQlearningGuiState(State state, Set<Action> actions){
        HashMap<String, Double> actionIds = new HashMap<String, Double>();
        for(Action action:actions){
            actionIds.put(action.get(Tags.AbstractID), R_MAX);
        }
        return new QlearningGuiState(state.get(Tags.AbstractID),actionIds);
    }

    protected boolean containsStateId(String stateId){
        for(QlearningGuiState state: qlearningGuiStates){
            if(state.getAbstractStateId().equals(stateId)){
                return true;
            }
        }
        return false;
    }

}
