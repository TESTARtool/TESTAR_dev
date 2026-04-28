/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.action;

import org.openqa.selenium.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.core.action.Action;
import org.testar.core.alayer.Role;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.core.state.Widget;
import org.testar.webdriver.state.WdDriver;
import org.testar.webdriver.tag.WdTags;

public class WdSelectListAction extends TaggableBase implements Action {
    private static final long serialVersionUID = -5522966388178892530L;
    protected static final Logger logger = LogManager.getLogger();

    private String target;
    private String value;
    private JsTargetMethod targetMethod;

    public enum JsTargetMethod {
        ID,
        NAME
    }

    public WdSelectListAction(String target, String value, Widget widget, JsTargetMethod targetMethod) {
        this.target = target;
        this.value = value;
        this.targetMethod = targetMethod;
        this.set(Tags.Role, WdActionRoles.SelectListAction);
        this.set(Tags.Desc, "Set Webdriver select list script to set into " + targetMethod.toString() + " " + target + " : " + value);
        this.mapOriginWidget(widget);
    }

    @Override
    public void run(SUT system, State state, double duration) {
        Widget originWidget = get(Tags.OriginWidget, null);
        WebElement fallbackElement = originWidget == null ? null : originWidget.get(WdTags.WebElementSelenium, null);

        switch(targetMethod) {
            case ID:
                WdDriver.executeScript(
                        "const field = document.getElementById(arguments[0]);"
                                + " const targetField = field || arguments[1];"
                                + " if (!targetField) { throw new Error('Unable to locate select field by id or fallback element'); }"
                                + " targetField.value = arguments[2];"
                                + " const event = new Event('change', { bubbles: true });"
                                + " targetField.dispatchEvent(event);",
                        target,
                        fallbackElement,
                        value
                );
                break;
            case NAME:
                // Problematic if multiple widgets match the same name, should only be used as last resort.
                WdDriver.executeScript(
                        "const field = document.getElementsByName(arguments[0])[0];"
                                + " const targetField = field || arguments[1];"
                                + " if (!targetField) { throw new Error('Unable to locate select field by name or fallback element'); }"
                                + " targetField.value = arguments[2];"
                                + " const event = new Event('change', { bubbles: true });"
                                + " targetField.dispatchEvent(event);",
                        target,
                        fallbackElement,
                        value
                );
                break;
            default:
                logger.warn("WdSelectListAction targetMethod is null!");
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
    public String toString(Role... discardParameters) {
        return toShortString();
    }

    public String getValue() {
        return value;
    }

    public String getTarget() {
        return target;
    }
}
