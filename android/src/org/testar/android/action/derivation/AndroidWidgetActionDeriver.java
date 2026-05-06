/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.action.derivation;

import java.util.LinkedHashSet;
import java.util.Set;

import org.testar.android.action.AndroidActionClick;
import org.testar.android.action.AndroidActionScroll;
import org.testar.android.action.AndroidActionType;
import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.policy.ScrollablePolicy;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.engine.action.TextInputProvider;
import org.testar.engine.action.derivation.WidgetActionDeriver;
import org.testar.engine.policy.SessionPolicyContext;

/**
 * Returns Android widget actions for one widget.
 */
public final class AndroidWidgetActionDeriver implements WidgetActionDeriver {

    private final TextInputProvider textInputProvider;

    public AndroidWidgetActionDeriver(TextInputProvider textInputProvider) {
        this.textInputProvider = Assert.notNull(textInputProvider);
    }

    @Override
    public Set<Action> derive(SUT system,
                              State state,
                              Widget widget,
                              SessionPolicyContext context) {
        Assert.notNull(state, widget, context);
        Set<Action> actions = new LinkedHashSet<>();
        TypeablePolicy typeablePolicy = context.require(TypeablePolicy.class);
        ClickablePolicy clickablePolicy = context.require(ClickablePolicy.class);
        ScrollablePolicy scrollablePolicy = context.require(ScrollablePolicy.class);

        if (typeablePolicy.isTypeable(widget)) {
            String text = textInputProvider.textFor(widget);
            if (text != null && !text.isEmpty()) {
                actions.add(new AndroidActionType(state, widget, text));
            }
        }

        if (clickablePolicy.isClickable(widget)) {
            actions.add(new AndroidActionClick(state, widget));
        }

        if (scrollablePolicy.isScrollable(widget)) {
            actions.add(new AndroidActionScroll(state, widget));
        }

        return actions;
    }
}
