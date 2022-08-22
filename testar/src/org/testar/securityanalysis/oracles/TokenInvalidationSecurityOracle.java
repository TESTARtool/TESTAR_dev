package org.testar.securityanalysis.oracles;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.*;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.securityanalysis.SecurityConfiguration;
import org.testar.securityanalysis.SecurityResultWriter;

import java.util.HashSet;
import java.util.Set;

public class TokenInvalidationSecurityOracle extends ActiveSecurityOracle {
    private Set<Action> preferredActions = new HashSet<>();
    private int stage = 0;
    private String loggedInUrl = "";
    private Set<Cookie> cookies;
    private SecurityConfiguration securityConfiguration = new SecurityConfiguration();

    public TokenInvalidationSecurityOracle(SecurityResultWriter securityResultWriter, RemoteWebDriver webDriver)
    {
        super(securityResultWriter, webDriver);
    }

    @Override
    public Set<Action> getActions(State state)
    {
        Set<Action> actions = new HashSet<>();
        String url = WdDriver.getCurrentUrl();
        if (url.compareToIgnoreCase(securityConfiguration.loginUrl) == 0 && stage == 0)
        {
            actions.add(login(state));
        }
        else if (stage == 1)
        {
            loggedInUrl = WdDriver.getCurrentUrl();
            takeCookieSnapshot(webDriver);

            actions.add(logout(state));
        }
        else if (stage == 2)
        {
            restoreCookieSnapshot(webDriver);
            actions.add(new WdSecurityUrlInjectionAction(loggedInUrl));
        }
        else if (stage == 3)
        {
            try {
                Thread.sleep(securityConfiguration.tokenInvalidationWaitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            actions.add(new WdSecurityUrlInjectionAction(loggedInUrl));
        }

        preferredActions.addAll(actions);
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
    public void actionSelected(Action action)
    {
        /** bump the stage when proposed action will be executed **/
        if (preferredActions.contains(action) || preferredActions.isEmpty()) {
            stage++;
        }

        preferredActions.clear();
    }

    @Override
    public Verdict getVerdict()
    {
        if (stage == 3 || stage == 4)
        {
            if (webDriver.getCurrentUrl().compareToIgnoreCase(loggedInUrl) != 0)
            {
                /** Vulnerability found **/
                securityResultWriter.WriteResult(webDriver.getCurrentUrl(), "0", "Session tokens were not invalidated");

                /** end oracle **/
                stage = 10;
                return Verdict.FAIL;
            }
            else if (stage == 4)
            {
                System.out.println("No vulnerability found");
            }
        }

        return Verdict.OK;
    }

    private Action login(State state)
    {
        CompoundAction.Builder builder = new CompoundAction.Builder();
        Action submitAction = null;

        for (Widget widget : state ) {
            if (widget.get(Tags.Title).toLowerCase().contains(securityConfiguration.usernameField)) {
                builder.add(new WdSecurityInjectionAction(webDriver, (WdWidget)widget, securityConfiguration.username), 0.1) ;
            }
            else if (widget.get(Tags.Title).toLowerCase().contains (securityConfiguration.passwordField)) {
                builder.add(new WdSecurityInjectionAction(webDriver, (WdWidget)widget, securityConfiguration.password), 0.1);
            }
            else if (widget.get(Tags.Path).contains(securityConfiguration.submitButton)) {
                StdActionCompiler ac = new AnnotatingActionCompiler();
                submitAction = ac.leftClickAt(widget);
            }
        }
        return builder.add(submitAction, 0.1).build();
    }

    private Action logout(State state)
    {
        for (Widget widget : state) {
            if (widget.get(Tags.Title).toLowerCase().contains(securityConfiguration.logoutButton)) {
                StdActionCompiler ac = new AnnotatingActionCompiler();
                return ac.leftClickAt(widget);
            }
        }

        return null;
    }

    public void takeCookieSnapshot(WebDriver webDriver)
    {
        cookies = webDriver.manage().getCookies();
    }

    public void restoreCookieSnapshot(WebDriver webDriver)
    {
        webDriver.manage().deleteAllCookies();
        for (Cookie cookie : cookies) {
            webDriver.manage().addCookie(new Cookie(cookie.getName(), cookie.getValue()));
        }
    }
}
