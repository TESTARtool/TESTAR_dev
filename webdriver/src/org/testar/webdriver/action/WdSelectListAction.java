/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.action;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.StaleElementReferenceException;
import org.testar.core.action.Action;
import org.testar.core.action.ActionIdentity;
import org.testar.core.alayer.Role;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.core.state.Widget;
import org.testar.webdriver.state.WdDriver;
import org.testar.webdriver.tag.WdTags;

public class WdSelectListAction extends TaggableBase implements Action {
    private static final long serialVersionUID = -5522966388178892530L;
    private static final String SELECT_SCRIPT = loadSelectScript();

    private String target;
    private String value;
    private JsTargetMethod targetMethod;

    public enum JsTargetMethod {
        ID,
        NAME,
        CSS,
        ELEMENT
    }

    public WdSelectListAction(String target, String value, Widget widget, JsTargetMethod targetMethod) {
        this.target = target;
        this.value = value;
        this.targetMethod = targetMethod;
        this.set(Tags.Role, WdActionRoles.SelectListAction);
        this.set(Tags.InputText, value);
        this.set(Tags.Desc, "Set Webdriver select list script to set into " + targetMethod.toString() + " " + target + " : " + value);
        this.mapOriginWidget(widget);
    }

    @Override
    public void run(SUT system, State state, double duration) {
        Widget originWidget = get(Tags.OriginWidget, null);
        WebElement capturedElement = originWidget == null ? null : originWidget.get(WdTags.WebElementSelenium, null);
        Object result;
        try {
            result = executeSelection(capturedElement);
        } catch (RuntimeException exception) {
            throw new ActionFailedException("Unable to execute " + toShortString(), exception);
        }
        if (!Boolean.TRUE.equals(result)) {
            String reason = result instanceof String ? (String) result : "WebDriver did not execute the selection";
            throw new ActionFailedException(toShortString() + ": " + reason);
        }
    }

    private Object executeSelection(WebElement capturedElement) {
        try {
            return WdDriver.executeScript(SELECT_SCRIPT, target, capturedElement, value, targetMethod.name());
        } catch (StaleElementReferenceException exception) {
            if (targetMethod == JsTargetMethod.ELEMENT) {
                throw exception;
            }
            return WdDriver.executeScript(SELECT_SCRIPT, target, null, value, targetMethod.name());
        }
    }

    private static String loadSelectScript() {
        try (InputStream stream = WdSelectListAction.class.getResourceAsStream("/select-list.js")) {
            if (stream == null) {
                throw new IllegalStateException("Missing WebDriver select-list.js resource");
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load WebDriver select-list.js", exception);
        }
    }

    @Override
    public String toShortString() {
        return "Set select list on '" + target + "' to '" + value + "'";
    }

    @Override
    public String toParametersString() {
        return toShortString();
    }

    @Override
    public String getIdentityParameters(State state, boolean abstractIdentity) {
        return abstractIdentity ? value : ActionIdentity.encode(targetMethod.name(), target, value);
    }

    @Override
    public String toString(Role... discardParameters) {
        return toShortString();
    }

    public String getValue() {
        return value;
    }

    public String getTarget() {
        return target;
    }

    public JsTargetMethod getTargetMethod() {
        return targetMethod;
    }
}
