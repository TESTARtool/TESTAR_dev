/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.action.derivation;

import java.util.LinkedHashSet;
import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.Drag;
import org.testar.core.action.Action;
import org.testar.core.action.AnnotatingActionCompiler;
import org.testar.core.alayer.AbsolutePosition;
import org.testar.core.alayer.Point;
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
 * Returns Windows desktop actions for one widget.
 */
public final class WindowsWidgetActionDeriver implements WidgetActionDeriver {

    private final AnnotatingActionCompiler actionCompiler;
    private final TextInputProvider textInputProvider;
    private final double scrollArrowSize;
    private final double scrollThick;

    public WindowsWidgetActionDeriver(TextInputProvider textInputProvider) {
        this(new AnnotatingActionCompiler(), textInputProvider, 36.0, 16.0);
    }

    WindowsWidgetActionDeriver(AnnotatingActionCompiler actionCompiler,
                               TextInputProvider textInputProvider,
                               double scrollArrowSize,
                               double scrollThick) {
        this.actionCompiler = Assert.notNull(actionCompiler);
        this.textInputProvider = Assert.notNull(textInputProvider);
        this.scrollArrowSize = scrollArrowSize;
        this.scrollThick = scrollThick;
    }

    @Override
    public Set<Action> derive(SUT system,
                              State state,
                              Widget widget,
                              SessionPolicyContext context) {
        Assert.notNull(widget, context);
        Set<Action> actions = new LinkedHashSet<>();
        TypeablePolicy typeablePolicy = context.require(TypeablePolicy.class);
        ClickablePolicy clickablePolicy = context.require(ClickablePolicy.class);
        ScrollablePolicy scrollablePolicy = context.require(ScrollablePolicy.class);
        if (typeablePolicy.isTypeable(widget)) {
            String text = textInputProvider.textFor(widget);
            if (text != null && !text.isEmpty()) {
                actions.add(actionCompiler.clickTypeInto(widget, text, true));
            }
        }
        if (clickablePolicy.isClickable(widget)) {
            actions.add(actionCompiler.leftClickAt(widget));
        }
        if (scrollablePolicy.isScrollable(widget)) {
            Drag[] drags = widget.scrollDrags(scrollArrowSize, scrollThick);
            if (drags != null) {
                for (Drag drag : drags) {
                    actions.add(actionCompiler.slideFromTo(
                            new AbsolutePosition(Point.from(drag.getFromX(), drag.getFromY())),
                            new AbsolutePosition(Point.from(drag.getToX(), drag.getToY())),
                            widget
                    ));
                }
            }
        }
        return actions;
    }
}
