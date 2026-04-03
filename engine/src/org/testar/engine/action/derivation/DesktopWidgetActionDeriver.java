/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.derivation;

import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.Drag;
import org.testar.core.action.Action;
import org.testar.core.action.AnnotatingActionCompiler;
import org.testar.core.alayer.AbsolutePosition;
import org.testar.core.alayer.Point;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.engine.action.TextInputProvider;

/**
 * Desktop-style widget action derivation.
 */
public final class DesktopWidgetActionDeriver implements WidgetActionDeriver {

    private final AnnotatingActionCompiler actionCompiler;
    private final TextInputProvider textInputProvider;
    private final double scrollArrowSize;
    private final double scrollThick;

    public DesktopWidgetActionDeriver(TextInputProvider textInputProvider) {
        this(new AnnotatingActionCompiler(), textInputProvider, 36.0, 16.0);
    }

    DesktopWidgetActionDeriver(AnnotatingActionCompiler actionCompiler,
                                      TextInputProvider textInputProvider,
                                      double scrollArrowSize,
                                      double scrollThick) {
        this.actionCompiler = Assert.notNull(actionCompiler);
        this.textInputProvider = Assert.notNull(textInputProvider);
        this.scrollArrowSize = scrollArrowSize;
        this.scrollThick = scrollThick;
    }

    @Override
    public void derive(SUT system,
                       State state,
                       Widget widget,
                       ActionDerivationContext context,
                       Set<Action> actions) {
        Assert.notNull(widget, context, actions);
        if (context.typeablePolicy().isTypeable(widget)) {
            String text = textInputProvider.textFor(widget);
            if (text != null && !text.isEmpty()) {
                actions.add(actionCompiler.clickTypeInto(widget, text, true));
            }
        }
        if (context.clickablePolicy().isClickable(widget)) {
            actions.add(actionCompiler.leftClickAt(widget));
        }
        if (context.scrollablePolicy().isScrollable(widget)) {
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
    }
}
