/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.action;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.core.action.*;
import org.testar.core.alayer.*;
import org.testar.core.state.*;
import org.testar.core.tag.*;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.webdriver.tag.WdTags;

/** Enables TESTAR to use special characters in input fields **/
public class WdSecurityInjectionAction extends TaggableBase implements Action {
    private static final long serialVersionUID = 754070773402916305L;

    private WebElement element;
    private String text;

    public WdSecurityInjectionAction(RemoteWebDriver webDriver, Widget widget, String text)
    {
    	//TODO: This only works if the web element contains the id property
        element = webDriver.findElement(new By.ById(widget.get(WdTags.WebId, "")));
        this.set(Tags.Role, WdActionRoles.FormFillingAction);
        this.set(Tags.Desc, "Inject text that contains special characters");
        this.text = text;
        this.mapOriginWidget(widget);
    }

    public String getText() {
    	return text;
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        element.clear();
        element.sendKeys(this.text);
    }

    @Override
    public String toShortString() {
        return "Inject text";
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
