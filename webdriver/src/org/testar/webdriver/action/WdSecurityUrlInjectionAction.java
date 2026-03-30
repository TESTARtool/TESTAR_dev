/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.action;

import org.testar.core.action.*;
import org.testar.core.alayer.*;
import org.testar.core.state.*;
import org.testar.core.tag.*;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.webdriver.state.WdDriver;

/** Enables TESTAR to navigate to a new URL **/
public class WdSecurityUrlInjectionAction extends TaggableBase implements Action {
    private static final long serialVersionUID = 3391295084409798793L;

    private String text;

    public WdSecurityUrlInjectionAction(String text)
    {
        super();
        this.set(Tags.Role, WdActionRoles.ExecuteScript);
        this.set(Tags.Desc, "Execute Webdriver script to redirect to different url");
        this.text = text;
    }

    public String getText() {
    	return text;
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException
    {
        System.out.println("Executing UrlInjectionAction to new location:");
        System.out.println(this.text);
        WdDriver.executeScript("window.location = \'"+this.text+"\'");
    }

    @Override
    public String toShortString() {
        return "Inject text in url";
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
