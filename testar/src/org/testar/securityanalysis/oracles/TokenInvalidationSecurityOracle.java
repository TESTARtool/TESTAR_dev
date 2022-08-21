package org.testar.securityanalysis.oracles;

import com.tigervnc.rdr.Exception;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.*;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.securityanalysis.SecurityResultWriter;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class TokenInvalidationSecurityOracle extends ActiveSecurityOracle {
    private Set<Action> preferredActions = new HashSet<>();
    private int stage = 0;
    private String loggedInUrl = "";
    private Set<Cookie> cookies;

    public TokenInvalidationSecurityOracle(SecurityResultWriter securityResultWriter, RemoteWebDriver webDriver)
    {
        super(securityResultWriter, webDriver);
    }

    @Override
    public Set<Action> getActions(State state)
    {
        Set<Action> actions = new HashSet<>();
        String url = WdDriver.getCurrentUrl();
        if (url.compareToIgnoreCase("http://localhost:41948/Account/Login") == 0 && stage == 0)
        {
            System.out.println("login");
            actions.add(login(state));
        }
        else if (stage == 1)
        {
            loggedInUrl = WdDriver.getCurrentUrl();
            takeCookieSnapshot(webDriver);

            System.out.println("logout");
            actions.add(logout(state));
        }
        else if (stage == 2)
        {
            System.out.println("STAGE 2");
            restoreCookieSnapshot(webDriver);
            actions.add(new WdSecurityUrlInjectionAction(loggedInUrl));
        }
        else if (stage == 3)
        {
            System.out.println("STAGE 3");
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("AWAKE");
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
        // bump the stage if proposed action is executed
        if (preferredActions.contains(action) || preferredActions.isEmpty()) {
            stage++;
            System.out.println("Stage to: " + stage);
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
                System.out.println("Vulnerability found!");

                /** end oracle **/
                stage = 10;
            }
            else if (stage == 4)
            {
                System.out.println("No vulnerability found");
            }
        }


        /*if (WdDriver.getCurrentUrl().compareToIgnoreCase(loggedInUrl) == 0)
            return Verdict.FAIL;
*/
        return Verdict.OK;
    }

    private Action login(State state)
    {
        CompoundAction.Builder builder = new CompoundAction.Builder();
        Action submitAction = null;

        for (Widget widget : state ) {
            if (widget.get(Tags.Title).toLowerCase().contains("username")) {
                builder.add(new WdSecurityInjectionAction(webDriver, (WdWidget)widget, "Admin"), 0.1) ;
            }
            else if (widget.get(Tags.Title).toLowerCase().contains ("password") ) {
                builder.add(new WdSecurityInjectionAction(webDriver, (WdWidget)widget, "123"), 0.1);
            }
            else if (widget.get(Tags.Path).contains("1, 0, 2, 0, 0, 3, 0")) {
                StdActionCompiler ac = new AnnotatingActionCompiler();
                submitAction = ac.leftClickAt(widget);
            }
        }
        return builder.add(submitAction, 0.1).build();
    }

    private Action logout(State state)
    {
        for (Widget widget : state) {
            if (widget.get(Tags.Title).toLowerCase().contains("logout")) {
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
