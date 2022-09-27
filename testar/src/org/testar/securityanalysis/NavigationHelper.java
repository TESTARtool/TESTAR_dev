/***************************************************************************************************
 *
 * Copyright (c) 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.securityanalysis;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.WdSecurityUrlInjectionAction;
import org.testar.monkey.alayer.webdriver.WdDriver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NavigationHelper {
    private HashMap<String, HashMap<String, Integer>> previousActions = new HashMap<>();
    private int maxNumberOfExecutions = (new SecurityConfiguration()).maxBenchmarkExecutionTimes;

    /** Returns actions that have been executed the least **/
    public Set<Action> filterActions(Set<Action> actions)
    {
        int leastExecutions = maxNumberOfExecutions;
        Set<Action> retActions = new HashSet<>();

        for (Action action : actions) {
            int timesExecuted = getTimesExecuted(action);

            if (action.getClass() == WdSecurityUrlInjectionAction.class)
            {
                /** Always return navigation actions when they are never executed **/
                if (timesExecuted == 0)
                    retActions.add(action);
            }
            else if (timesExecuted == 0)
            {
                /** No action has been performed on widget yet **/
                if (leastExecutions != 0)
                {
                    leastExecutions = 0;
                    retActions = new HashSet<>();
                }
                retActions.add(action);
            }
            else if (timesExecuted < maxNumberOfExecutions)
            {
                /** Action is already performed on widget **/
                if (timesExecuted == leastExecutions) {
                    retActions.add(action);
                } else if (timesExecuted < leastExecutions) {
                    leastExecutions = timesExecuted;
                    retActions = new HashSet<>();
                    retActions.add(action);
                }
            }
        }

        return retActions;
    }

    /** Bumps the execution count of an action by one **/
    public void setExecution(Action action)
    {
        if (getActionId(action) == "")
            return;

        /** Resets previous actions for url so the page will get scanned again **/
        if (action.getClass() == WdSecurityUrlInjectionAction.class)
            previousActions.remove(getUrl());

        int timesExecuted = getTimesExecuted(action);
        setTimesExecuted(action, timesExecuted + 1);
    }

    private String getUrl()
    {
        return WdDriver.getCurrentUrl().split("\\?")[0];
    }

    /** Returns a unique action id based on ConcreteID and URL **/
    private String getActionId(Action action)
    {
        Widget widget = action.get(Tags.OriginWidget, null);

        if (widget != null)
            return (widget.get(Tags.ConcreteID, ""));
        else
            return (action.get(Tags.ConcreteID, ""));
    }

    private int getTimesExecuted(Action action)
    {
        HashMap<String, Integer> urlHashMap = previousActions.get(getUrl());
        if (urlHashMap == null)
            return 0;

        Integer timesExecuted = urlHashMap.get(getActionId(action));
        if (timesExecuted == null)
            return 0;

        return timesExecuted;
    }

    private void setTimesExecuted(Action action, int timesExecuted)
    {
        if (previousActions.get(getUrl()) == null)
            previousActions.put(getUrl(), new HashMap<>());

        previousActions.get(getUrl()).put(getActionId(action), timesExecuted);
    }
}
