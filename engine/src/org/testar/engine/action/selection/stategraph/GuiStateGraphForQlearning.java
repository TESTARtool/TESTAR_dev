/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.selection.stategraph;

import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;

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
