/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.state;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.testar.core.Assert;
import org.testar.core.policy.BlockedPolicy;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.policy.EnabledPolicy;
import org.testar.core.policy.ScrollablePolicy;
import org.testar.core.policy.SelectablePolicy;
import org.testar.core.policy.TopLevelPolicy;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.policy.VisiblePolicy;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.tag.Taggable;
import org.testar.engine.policy.SessionPolicyContext;
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

        List<StateSemanticDisambiguator.SemanticWidgetProjection> semanticProjections = new ArrayList<>();
        for (Widget widget : state) {
            if (widget == state || !descriptor.shouldInclude(widget)) {
                continue;
            }

            String semanticDescription = SemanticStateFormatter.describe(widget, descriptor);
            if (semanticDescription.isBlank()) {
                continue;
            }
            semanticProjections.add(new StateSemanticDisambiguator.SemanticWidgetProjection(widget, semanticDescription));
        }

        new StateSemanticDisambiguator().disambiguate(semanticProjections);
        Set<String> uniqueDescriptions = new LinkedHashSet<>();
        for (StateSemanticDisambiguator.SemanticWidgetProjection projection : semanticProjections) {
            if (!uniqueDescriptions.add(projection.description())) {
                continue;
            }

            WidgetStub copiedWidget = copyWidget(projection.widget(), projectedState);
            copiedWidget.set(Tags.Desc, projection.description());
            projectedState.addChild(copiedWidget);
        }

        return projectedState;
    }

    static State projectInteractiveWidgets(State state, SessionPolicyContext context) {
        Assert.notNull(context);
        return projectWidgets(
                state,
                widget -> isInteractionCapableWidget(widget, context)
        );
    }

    static State projectInteractiveSemanticWidgets(State state,
                                                   SemanticWidgetDescriptor descriptor,
                                                   SessionPolicyContext context) {
        Assert.notNull(state);
        Assert.notNull(descriptor);
        Assert.notNull(context);

        return projectSemanticWidgets(
                state,
                widget -> isInteractionCapableWidget(widget, context) || descriptor.shouldInclude(widget),
                descriptor
        );
    }

    static State projectActionableWidgets(State state, SessionPolicyContext context) {
        Assert.notNull(context);
        return projectWidgets(
                state,
                widget -> isActionableWidget(widget, context)
        );
    }

    static State projectActionableSemanticWidgets(State state,
                                                  SemanticWidgetDescriptor descriptor,
                                                  SessionPolicyContext context) {
        Assert.notNull(state);
        Assert.notNull(descriptor);
        Assert.notNull(context);

        return projectSemanticWidgets(
                state,
                widget -> isActionableWidget(widget, context) || descriptor.shouldInclude(widget),
                descriptor
        );
    }

    private static State projectSemanticWidgets(State state,
                                                Predicate<Widget> widgetMatcher,
                                                SemanticWidgetDescriptor descriptor) {
        Assert.notNull(state);
        Assert.notNull(widgetMatcher);
        Assert.notNull(descriptor);

        StateStub projectedState = new StateStub();
        copyTags(state, projectedState);

        List<StateSemanticDisambiguator.SemanticWidgetProjection> semanticProjections = new ArrayList<>();
        List<Widget> nonSemanticWidgets = new ArrayList<>();
        for (Widget widget : state) {
            if (widget == state || !widgetMatcher.test(widget)) {
                continue;
            }

            String semanticDescription = SemanticStateFormatter.describe(widget, descriptor);
            if (semanticDescription.isBlank()) {
                nonSemanticWidgets.add(widget);
                continue;
            }
            semanticProjections.add(new StateSemanticDisambiguator.SemanticWidgetProjection(widget, semanticDescription));
        }

        new StateSemanticDisambiguator().disambiguate(semanticProjections);
        Set<String> uniqueDescriptions = new LinkedHashSet<>();
        Set<Widget> projectedWidgets = new LinkedHashSet<>();
        for (StateSemanticDisambiguator.SemanticWidgetProjection projection : semanticProjections) {
            if (!uniqueDescriptions.add(projection.description())) {
                continue;
            }

            WidgetStub copiedWidget = copyWidget(projection.widget(), projectedState);
            copiedWidget.set(Tags.Desc, projection.description());
            projectedState.addChild(copiedWidget);
            projectedWidgets.add(projection.widget());
        }

        for (Widget widget : nonSemanticWidgets) {
            if (projectedWidgets.add(widget)) {
                projectedState.addChild(copyWidget(widget, projectedState));
            }
        }

        return projectedState;
    }

    private static boolean isInteractionCapableWidget(Widget widget, SessionPolicyContext context) {
        if (widget.root() == widget) {
            return false;
        }

        return context.require(ClickablePolicy.class).isClickable(widget)
                || context.require(TypeablePolicy.class).isTypeable(widget)
                || context.require(ScrollablePolicy.class).isScrollable(widget)
                || context.require(SelectablePolicy.class).isSelectable(widget);
    }

    private static boolean isActionableWidget(Widget widget, SessionPolicyContext context) {
        return isInteractionCapableWidget(widget, context) && isActionEligibleWidget(widget, context);
    }

    private static boolean isActionEligibleWidget(Widget widget, SessionPolicyContext context) {
        if (widget.root() == widget) {
            return false;
        }

        EnabledPolicy enabledPolicy = context.require(EnabledPolicy.class);
        BlockedPolicy blockedPolicy = context.require(BlockedPolicy.class);
        WidgetFilterPolicy widgetFilterPolicy = context.require(WidgetFilterPolicy.class);
        VisiblePolicy visiblePolicy = context.require(VisiblePolicy.class);
        TopLevelPolicy topLevelPolicy = context.require(TopLevelPolicy.class);
        return enabledPolicy.isEnabled(widget)
                && !blockedPolicy.isBlocked(widget)
                && widgetFilterPolicy.allows(widget)
                && visiblePolicy.isVisible(widget)
                && topLevelPolicy.isTopLevel(widget);
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
