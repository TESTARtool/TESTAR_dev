package org.testar.securityanalysis.helpers;

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.securityanalysis.SecurityResultWriter;
import org.testar.securityanalysis.oracles.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public Verdict getVerdict(Verdict verdict)
    {
        for (BaseSecurityOracle securityOracle : securityOracles) {
            Verdict newVerdict = securityOracle.getVerdict();
            if (newVerdict != null)
                verdict.join(newVerdict);
        }
        if (activeSecurityOracle != null)
            verdict.join(activeSecurityOracle.getVerdict());

        return verdict;
    }
}
