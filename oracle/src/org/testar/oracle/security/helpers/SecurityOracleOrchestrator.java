/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.security.helpers;

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.verdict.Verdict;
import org.testar.oracle.security.ActiveSecurityOracle;
import org.testar.oracle.security.BaseSecurityOracle;
import org.testar.oracle.security.HeaderAnalysisSecurityOracle;
import org.testar.oracle.security.SecurityResultWriter;
import org.testar.oracle.security.SqlInjectionSecurityOracle;
import org.testar.oracle.security.TokenInvalidationSecurityOracle;
import org.testar.oracle.security.XssSecurityOracle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** The SecurityOracleOrchestrator connects the oracles to the protocol.
 * The protocol calls the SecurityOracleOrchestrator for certain actions and these actions are passed on to the oracles.
 */
public class SecurityOracleOrchestrator {
    private Set<BaseSecurityOracle> securityOracles = new HashSet<>();
    private ActiveSecurityOracle activeSecurityOracle;
    private SecurityResultWriter securityResultWriter;

    public SecurityOracleOrchestrator(SecurityResultWriter securityResultWriter, List<String> securityOracleNames, RemoteWebDriver webDriver, DevTools devTools)
    {
        this.securityResultWriter = securityResultWriter;
        setSecurityOracles(securityOracleNames, webDriver);
        addListeners(devTools);
    }

    public boolean hasActiveOracle()
    {
        return (activeSecurityOracle != null);
    }

    public Set<Action> getActions(State state)
    {
        if (activeSecurityOracle != null)
            return activeSecurityOracle.getActions(state);

        return new HashSet<>();
    }

    public Set<Action> preSelect(Set<Action> actions)
    {
        if (activeSecurityOracle != null)
            return activeSecurityOracle.preSelect(actions);

        return actions;
    }

    public void actionSelected(Action action)
    {
        if (activeSecurityOracle != null)
            activeSecurityOracle.actionSelected(action);
    }

    //region configuration
    private void setSecurityOracles(List<String> securityOracleNames, RemoteWebDriver webDriver)
    {
        for (String name : securityOracleNames)
        {
            if (name.compareToIgnoreCase("HeaderAnalysisSecurityOracle") == 0)
                securityOracles.add(new HeaderAnalysisSecurityOracle(securityResultWriter));
            if (name.compareToIgnoreCase("SqlInjectionSecurityOracle") == 0)
                setActiveOracle(new SqlInjectionSecurityOracle(securityResultWriter, webDriver));
            if (name.compareToIgnoreCase("XssSecurityOracle") == 0)
                setActiveOracle(new XssSecurityOracle(securityResultWriter, webDriver));
            if (name.compareToIgnoreCase("TokenInvalidationSecurityOracle") == 0)
                setActiveOracle(new TokenInvalidationSecurityOracle(securityResultWriter, webDriver));
        }
    }

    private void setActiveOracle(ActiveSecurityOracle securityOracle)
    {
        if (activeSecurityOracle == null)
            activeSecurityOracle = securityOracle;
        else
            throw new UnsupportedOperationException("Only one active security oracle at a time please!");
    }
    //endregion

    private void addListeners(DevTools devTools)
    {
        if (activeSecurityOracle != null)
            activeSecurityOracle.addListener(devTools);

        for (BaseSecurityOracle securityOracle : securityOracles)
            securityOracle.addListener(devTools);
    }

    public List<Verdict> getVerdicts()
    {
        List<Verdict> verdicts = new ArrayList<>();
        for (BaseSecurityOracle securityOracle : securityOracles) {
            Verdict newVerdict = securityOracle.getVerdict();
            if (newVerdict != null && newVerdict.severity() > Verdict.Severity.OK.getValue()) {
                verdicts.add(newVerdict);
            }
        }
        if (activeSecurityOracle != null) {
            Verdict activeVerdict = activeSecurityOracle.getVerdict();
            if (activeVerdict != null && activeVerdict.severity() > Verdict.Severity.OK.getValue()) {
                verdicts.add(activeVerdict);
            }
        }
        return verdicts;
    }
}
