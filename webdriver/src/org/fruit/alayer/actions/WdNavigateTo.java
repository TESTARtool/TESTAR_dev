package org.fruit.alayer.actions;

import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.webdriver.WdDriver;

public class WdNavigateTo extends TaggableBase implements Action {
    String URL = "";
    public WdNavigateTo(String URL) {
        this.set(Tags.Role, WdActionRoles.ExecuteScript);
        this.set(Tags.Desc, "Execute Webdriver script to navigate to a URL");
        this.URL = URL;
    }

    @Override
    public void run(SUT system, State state, double duration)
            throws ActionFailedException {
        String script = "window.location = '" + URL + "';";
        WdDriver.executeScript(script);
    }

    @Override
    public String toShortString() {
        return "Navigate To";
    }

    @Override
    public String toParametersString() {
        return toShortString();
    }

    @Override
    public String toString(Role... discardParameters) {
        return toShortString();
    }
}