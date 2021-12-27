/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2021 Universitat Politecnica de Valencia - www.upv.es
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

package nl.ou.testar.SimpleGuiStateGraph;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.*;

public class GuiStateGraphForQlearning {
    protected Set<QlearningGuiState> qlearningGuiStates;
    protected String startingStateAbstractCustomId;
    protected String previousStateAbstractCustomId;
    protected String previousActionAbstractCustomId;
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
        previousActionAbstractCustomId=null;
        previousStateAbstractCustomId=null;
    }

    /**
     * returns null if action with given ID is not found
     *
     * @param actions
     * @param abstractCustomActionId
     * @return
     */
    protected Action getActionWithAbstractCustomId(Set<Action> actions, String abstractCustomActionId){
        System.out.println("DEBUG: trying to find action with a matching ID:");
        for(Action action:actions){
            System.out.println("DEBUG: action.AbstractIDCustom="+action.get(Tags.AbstractIDCustom)+", idToMatch="+abstractCustomActionId);
            // find the action with abstractCustomId:
            if(action.get(Tags.AbstractIDCustom).equals(abstractCustomActionId)){
                System.out.println("DEBUG: match found!");
                return action;
            }
        }
        System.out.println("ERROR: matching action ID not found!");
        return null;
    }

    /**
     * Returns null if not found
     *
     * @param abstractCustomStateId
     * @return
     */
    protected QlearningGuiState getStateByAbstractCustomId(String abstractCustomStateId){
        for(QlearningGuiState state: qlearningGuiStates){
            if(state.getAbstractCustomStateId().equals(abstractCustomStateId)){
                return state;
            }
        }
        //System.out.println("DEBUG: state with ID="+abstractCustomStateId+" was not found - a new state.");
        return null;
    }

    protected QlearningGuiState createQlearningGuiState(State state, Set<Action> actions){
        HashMap<String, Double> actionIds = new HashMap<String, Double>();
        for(Action action:actions){
            actionIds.put(action.get(Tags.AbstractIDCustom), R_MAX);
        }
        return new QlearningGuiState(state.get(Tags.AbstractIDCustom),actionIds);
    }

    protected boolean containsStateId(String stateId){
        for(QlearningGuiState state: qlearningGuiStates){
            if(state.getAbstractCustomStateId().equals(stateId)){
                return true;
            }
        }
        return false;
    }

}
