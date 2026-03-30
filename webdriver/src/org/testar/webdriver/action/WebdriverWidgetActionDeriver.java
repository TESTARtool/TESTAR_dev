/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.action;

import java.util.Set;

import org.testar.core.action.Action;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.engine.action.ActionDerivationContext;
import org.testar.engine.action.TextInputProvider;
import org.testar.engine.action.WidgetActionDeriver;
import org.testar.webdriver.state.WdWidget;

/**
 * WebDriver-specific widget action derivation using remote scroll-aware actions
 * by default.
 */
public final class WebdriverWidgetActionDeriver implements WidgetActionDeriver {

    private final TextInputProvider textInputProvider;

    public WebdriverWidgetActionDeriver(TextInputProvider textInputProvider) {
        this.textInputProvider = textInputProvider;
    }

    @Override
    public void derive(SUT system,
                       State state,
                       Widget widget,
                       ActionDerivationContext context,
                       Set<Action> actions) {
        if (!(widget instanceof WdWidget)) {
            return;
        }

        WdWidget wdWidget = (WdWidget) widget;
        if (context.typeablePolicy().isTypeable(widget)) {
            String text = textInputProvider.textFor(widget);
            if (text != null && !text.isEmpty()) {
                actions.add(new WdRemoteScrollTypeAction(wdWidget, text));
            }
        }
        if (context.clickablePolicy().isClickable(widget)) {
            actions.add(new WdRemoteScrollClickAction(wdWidget));
        }
    }
}
