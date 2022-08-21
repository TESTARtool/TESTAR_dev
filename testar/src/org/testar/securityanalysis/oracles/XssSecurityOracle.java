package org.testar.securityanalysis.oracles;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.WdSecurityInjectionAction;
import org.testar.monkey.alayer.actions.WdSecurityUrlInjectionAction;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.securityanalysis.SecurityResultWriter;

import java.util.HashSet;
import java.util.Set;

public class XssSecurityOracle extends ActiveSecurityOracle {
    private Set<Action> preferredActions = new HashSet<>();

    public XssSecurityOracle(SecurityResultWriter securityResultWriter, RemoteWebDriver webDriver)
    {
        super(securityResultWriter, webDriver);
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
        preferredActions.addAll(actions);

        Action urlInjection = getUrlInjectionOrDefault();
        if (urlInjection != null)
            actions.add(urlInjection);

        return actions;
    }

    @Override
    public Set<Action> preSelect(Set<Action> actions)
    {
        Set<Action> intersectingSet = new HashSet<>();
        for (Action action : preferredActions) {
            if (actions.contains(action))
                intersectingSet.add(action);
        }

        if (!intersectingSet.isEmpty())
            return intersectingSet;
        return actions;
    }

    @Override
    public Verdict getVerdict()
    {
        LogEntries logs = webDriver.manage().logs().get(LogType.BROWSER);
        for (LogEntry entry : logs) {
            if (entry.getMessage().contains("XSS"))
                securityResultWriter.WriteResult(WdDriver.getCurrentUrl(), "79", "XSS detected");
        }

        return Verdict.OK;
    }

    private String getInjectionText()
    {
        return "<script> console.log('XSS detected!'); </script>";
    }

    private Action getUrlInjectionOrDefault()
    {
        String url = WdDriver.getCurrentUrl();

        String injection = "<script>console.log(%27XSS%20detected!%27);</script>";
        if (url.contains("?"))
        {
            String newUrl = url.replaceAll("=.*" + "&", injection + "&");
            newUrl = newUrl.replaceFirst("[^=]*$", injection);

            System.out.println("newUrl: " + newUrl);

            if (!newUrl.equals(url)) {
                System.out.println("UrlInjection added");
                return new WdSecurityUrlInjectionAction(newUrl);
            }
        }
        return null;
    }
}
