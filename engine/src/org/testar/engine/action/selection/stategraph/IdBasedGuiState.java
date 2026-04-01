/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.selection.stategraph;

import java.util.HashSet;
import java.util.Set;

public class IdBasedGuiState {
    protected String abstractStateId;
    protected Set<String> abstractActionIds;
    protected Set<String> unvisitedActionIds;
    protected Set<GuiStateTransition> stateTransitions;

    public IdBasedGuiState(String abstractStateId, Set<String> abstractActionIds) {
        this.abstractStateId = abstractStateId;
        this.abstractActionIds = abstractActionIds;
        this.unvisitedActionIds = abstractActionIds; // all are unvisited when creating
        stateTransitions = new HashSet<GuiStateTransition>();
    }

    public void addStateTransition(GuiStateTransition newTransition){
        if(stateTransitions.size()>0){
            //if existing transitions, checking for identical ones:
            for(GuiStateTransition guiStateTransition:stateTransitions){
                if(guiStateTransition.getSourceStateAbstractId().equals(newTransition.getSourceStateAbstractId())){
                    // the same source state, as it should be:
                    if(guiStateTransition.getActionAbstractId().equals(newTransition.getActionAbstractId())){
                        // also the action is the same:
                        if(guiStateTransition.getTargetStateAbstractId().equals(newTransition.getTargetStateAbstractId())){
                            // also the target state is the same -> identical transition
                            System.out.println(this.getClass()+": addStateTransition: identical transition found - no need to save again");
                            return;
                        }else{
                            // same source state and same action, but different target state -> some external factor or the data values affect the behaviour
                            System.out.println(this.getClass()+": addStateTransition: WARNING: same source state, same action, but different target state!");
                        }
                    }
                }else{
                    System.out.println(this.getClass()+": ERROR, source state is NOT same as in other state transitions from the same state!");
                }
            }
        }
        // otherwise adding the new state transition:
        stateTransitions.add(newTransition);
    }

    public void addVisitedAction(String abstractActionId){
        if(unvisitedActionIds.contains(abstractActionId)){
            System.out.println(this.getClass()+": addVisitedAction: action removed from the unvisited actions");
            unvisitedActionIds.remove(abstractActionId);
        }else{
            System.out.println(this.getClass()+": addVisitedAction: action not found from the unvisited actions");
        }
    }

    public Set<GuiStateTransition> getStateTransitions() {
        return stateTransitions;
    }

    public String getAbstractStateId() {
        return abstractStateId;
    }

    public void setAbstractStateId(String abstractStateId) {
        this.abstractStateId = abstractStateId;
    }

    public Set<String> getAbstractActionIds() {
        return abstractActionIds;
    }

    public void setAbstractActionIds(Set<String> abstractActionIds) {
        this.abstractActionIds = abstractActionIds;
    }

    public Set<String> getUnvisitedActionIds() {
        return unvisitedActionIds;
    }

    public void setUnvisitedActionIds(Set<String> unvisitedActionIds) {
        this.unvisitedActionIds = unvisitedActionIds;
    }
}
