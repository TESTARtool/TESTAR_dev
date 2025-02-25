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

package org.testar.statemodel.util;

import org.testar.statemodel.AbstractAction;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ActionHelper {

    /**
     * This helper method extracts the abstract ids for a set of actions
     * @param actions
     * @return
     */
    public static Set<String> getAbstractIds(Set<AbstractAction> actions) {
        Set<String> actionIds = new HashSet<>();
        for(AbstractAction action:actions) {
            actionIds.add(action.getActionId());
        }
        return actionIds;
    }

    /**
     * This helper method converts alayer actions to abstract actions for use in the state model
     * @param actions
     * @return
     */
    public static Set<AbstractAction> convertActionsToAbstractActions(Set<Action> actions) {
        Set<AbstractAction> abstractActions = new HashSet<>();
        // group the actions by the abstract action id
        Map<String, List<Action>> actionMap = actions.stream().collect(Collectors.groupingBy(a -> a.get(Tags.AbstractID)));
        // create the actions
        for (String abstractActionId : actionMap.keySet()) {
            AbstractAction abstractAction = new AbstractAction(abstractActionId);
            for (Action action : actionMap.get(abstractActionId)) {
                abstractAction.addConcreteActionId(action.get(Tags.ConcreteID));
            }
            abstractActions.add(abstractAction);
        }
        return abstractActions;
    }

}
