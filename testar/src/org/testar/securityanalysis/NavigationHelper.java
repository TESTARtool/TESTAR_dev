package org.testar.securityanalysis;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.UID;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.WdSecurityUrlInjectionAction;
import org.testar.monkey.alayer.webdriver.WdDriver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NavigationHelper {
    private HashMap<String, Action> previousActions = new HashMap<>();
    private int maxNumberOfExecutions = 2;

    public Set<Action> filterActions(Set<Action> actions)
    {
        int leastExecuted = maxNumberOfExecutions;
        Set<Action> retActions = new HashSet<>();

        for (Action action : actions) {
            /*if (action.getClass() == WdSecurityUrlInjectionAction.class)
                continue;
*/
            Action previousAction = previousActions.get(getActionId(action));

            if (previousAction == null)
            {
                if (leastExecuted != 0)
                {
                    leastExecuted = 0;
                    retActions = new HashSet<>();
                }
                retActions.add(action);
                /* System.out.println("Action never executed");
                System.out.println(getActionId(action));*/
            }
            else if (previousAction.get(Tags.TimesExecuted) < maxNumberOfExecutions)
            {
                //System.out.println("known: " + previousAction.get(Tags.TimesExecuted) + " " + leastExecuted);
                int times = previousAction.get(Tags.TimesExecuted);

                if (times == leastExecuted) {
                    retActions.add(action);
                } else if (times < leastExecuted) {
                    leastExecuted = times;
                    retActions = new HashSet<>();
                    retActions.add(action);
                }
            }
        }
/*
        if (retActions.isEmpty())
        {
            for(Action action2 : actions)
            {
                if (action2.getClass() == WdSecurityUrlInjectionAction.class) {

                    Action previousUrlAction = previousActions.get(getActionId(action2));
                    if (previousUrlAction != null && previousUrlAction.get(Tags.TimesExecuted) > 0) {
                        System.out.println("Returning empty set");
                        return new HashSet<>();
                    }
                    else
                    {
                        System.out.println("ActionId: " + getActionId(action2));
                        if (previousUrlAction != null)
                            System.out.println(previousUrlAction.get(Tags.TimesExecuted));
                        System.out.println("Adding url injection to empty set");
                        // If URL injection is the only remaning action, make sure all other actions are reset to 0
                        retActions.add(action2);

                        for (Action action : actions) {
                            if (action.getClass() == WdSecurityUrlInjectionAction.class)
                                continue;

                            Action previousAction = previousActions.get(getActionId(action));

                            if (previousAction != null)
                                previousAction.set(Tags.TimesExecuted, 0);
                        }
                    }
                }
            }
        }*/

        return retActions;
    }

    private String getActionId(Action action)
    {
        Widget widget = action.get(Tags.OriginWidget, null);
        String url = WdDriver.getCurrentUrl();

        url = url.split("\\?")[0];

        if (widget != null)
            return (widget.get(Tags.ConcreteID, "")+url);
        else
            return (action.get(Tags.ConcreteID, "")+url);
    }

    public void setExecution(Action action)
    {
        if (action.getClass() == WdSecurityUrlInjectionAction.class)
            System.out.println("Url injection action executed and counted: " + getActionId(action));

        if (getActionId(action) == "") {
            /*System.out.println("Action with empty string executed");*/
            return;
        }

        Action existingAction = previousActions.get(getActionId(action));
        if (existingAction == null) {
            /*System.out.println("New exection times: " + getActionId(action));*/
            action.set(Tags.TimesExecuted, 1);
            previousActions.put(getActionId(action), action);
        } else {
            /*System.out.println("BUMP exection times: " + getActionId(action));
            System.out.println("BUMP exection from: " + existingAction.get(Tags.TimesExecuted));*/
            existingAction.set(Tags.TimesExecuted, existingAction.get(Tags.TimesExecuted) + 1);
            if (action.getClass() == WdSecurityUrlInjectionAction.class)
                System.out.println("TimesExecuted: " + existingAction.get(Tags.TimesExecuted));
            /*System.out.println("BUMP exection to: " + existingAction.get(Tags.TimesExecuted));*/
        }
    }
}
