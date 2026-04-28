/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.action.derivation;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.testar.core.action.Action;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.policy.SelectablePolicy;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.engine.action.TextInputProvider;
import org.testar.engine.action.derivation.WidgetActionDeriver;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.core.policy.TypeablePolicy;
import org.testar.webdriver.action.WebdriverSelectListSupport;
import org.testar.webdriver.action.WdRemoteScrollClickAction;
import org.testar.webdriver.action.WdRemoteScrollTypeAction;
import org.testar.webdriver.state.WdWidget;

/**
 * Returns WebDriver actions for one widget using remote scroll-aware actions
 * by default.
 */
public final class WebdriverWidgetActionDeriver implements WidgetActionDeriver {

    private final TextInputProvider textInputProvider;

    public WebdriverWidgetActionDeriver(TextInputProvider textInputProvider) {
        this.textInputProvider = textInputProvider;
    }

    @Override
    public Set<Action> derive(SUT system,
                              State state,
                              Widget widget,
                              SessionPolicyContext context) {
        Set<Action> actions = new LinkedHashSet<>();
        if (!(widget instanceof WdWidget)) {
            return Collections.emptySet();
        }

        WdWidget wdWidget = (WdWidget) widget;
        TypeablePolicy typeablePolicy = context.require(TypeablePolicy.class);
        ClickablePolicy clickablePolicy = context.require(ClickablePolicy.class);
        SelectablePolicy selectablePolicy = context.require(SelectablePolicy.class);
        if (selectablePolicy.isSelectable(widget)) {
            Action selectListAction = WebdriverSelectListSupport.createSelectAction(widget);
            if (selectListAction != null) {
                actions.add(selectListAction);
            }
        }
        if (typeablePolicy.isTypeable(widget)) {
            String text = textInputProvider.textFor(widget);
            if (text != null && !text.isEmpty()) {
                actions.add(new WdRemoteScrollTypeAction(wdWidget, text));
            }
        }
        if (clickablePolicy.isClickable(widget)) {
            actions.add(new WdRemoteScrollClickAction(wdWidget));
        }
        return actions;
    }
}
