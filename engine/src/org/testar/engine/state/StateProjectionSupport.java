/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.state;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.testar.core.Assert;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.tag.Taggable;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

/**
 * Shared helpers for projecting widget subsets into flat state views.
 */
final class StateProjectionSupport {

    private StateProjectionSupport() {
    }

    static State projectWidgets(State state, Predicate<Widget> widgetMatcher) {
        Assert.notNull(state);
        Assert.notNull(widgetMatcher);

        StateStub projectedState = new StateStub();
        copyTags(state, projectedState);

        for (Widget widget : state) {
            if (widget == state) {
                continue;
            }
            if (widgetMatcher.test(widget)) {
                projectedState.addChild(copyWidget(widget, projectedState));
            }
        }

        return projectedState;
    }

    static boolean isLeafWidget(Widget widget) {
        return widget.root() != widget && widget.childCount() == 0;
    }

    static boolean hasText(Widget widget, Tag<String> textTag) {
        if (widget.root() == widget) {
            return false;
        }
        String text = widget.get(textTag, null);
        return text != null && !text.isBlank();
    }

    static State projectSemanticWidgets(State state, SemanticWidgetDescriptor descriptor) {
        Assert.notNull(state);
        Assert.notNull(descriptor);

        StateStub projectedState = new StateStub();
        copyTags(state, projectedState);

        Set<String> uniqueDescriptions = new LinkedHashSet<>();
        for (Widget widget : state) {
            if (widget == state || !descriptor.shouldInclude(widget)) {
                continue;
            }

            String semanticDescription = SemanticStateFormatter.describe(widget, descriptor);
            if (semanticDescription.isBlank() || !uniqueDescriptions.add(semanticDescription)) {
                continue;
            }

            WidgetStub copiedWidget = copyWidget(widget, projectedState);
            copiedWidget.set(Tags.Desc, semanticDescription);
            projectedState.addChild(copiedWidget);
        }

        return projectedState;
    }

    private static WidgetStub copyWidget(Widget sourceWidget, State rootState) {
        WidgetStub copiedWidget = new WidgetStub();
        copiedWidget.setRoot(rootState);
        copiedWidget.setParent(rootState);
        copyTags(sourceWidget, copiedWidget);
        return copiedWidget;
    }

    private static void copyTags(Taggable source, Taggable target) {
        for (Tag<?> tag : source.tags()) {
            copyTagValue(source, target, tag);
        }
    }

    private static <T> void copyTagValue(Taggable source,
                                         Taggable target,
                                         Tag<T> tag) {
        T value = source.get(tag, null);
        if (value != null) {
            target.set(tag, value);
        }
    }
}
