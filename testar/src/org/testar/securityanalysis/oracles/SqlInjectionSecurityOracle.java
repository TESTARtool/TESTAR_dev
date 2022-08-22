package org.testar.securityanalysis.oracles;

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v101.network.Network;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.WdSecurityInjectionAction;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.securityanalysis.SecurityResultWriter;

import java.util.HashSet;
import java.util.Set;

public class SqlInjectionSecurityOracle extends ActiveSecurityOracle {
    private boolean errorReceived = false;
    protected Set<Action> proposedActions = new HashSet<>();

    public SqlInjectionSecurityOracle(SecurityResultWriter securityResultWriter, RemoteWebDriver webDriver)
    {
        super(securityResultWriter, webDriver);
    }

    @Override
    public void addListener(DevTools devTools)
    {
        devTools.addListener(Network.responseReceivedExtraInfo(),
                responseReceived -> {
                    if (responseReceived.getStatusCode() == 500)
                        errorReceived = true;
                });
    }

    @Override
    public Set<Action> getActions(State state)
    {
        Set<Action> actions = new HashSet<>();
        for (Widget widget : state)
        {
            if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
                actions.add(new WdSecurityInjectionAction(webDriver, (WdWidget)widget, getInjectionText()));
            }
        }

        proposedActions = actions;
        return actions;
    }

    @Override
    public Verdict getVerdict()
    {
        if (errorReceived)
            securityResultWriter.WriteResult(WdDriver.getCurrentUrl(), "89", "SQL injection detected");

        return Verdict.OK;
    }

    private String getInjectionText()
    {
        return "'";
    }
}
