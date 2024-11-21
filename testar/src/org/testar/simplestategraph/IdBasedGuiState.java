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
