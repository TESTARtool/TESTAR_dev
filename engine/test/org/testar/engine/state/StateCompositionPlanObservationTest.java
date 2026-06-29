/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;
import org.testar.config.CliStateProjectionMode;
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
import org.testar.core.tag.Tags;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class StateCompositionPlanObservationTest {

    @Test
    public void fullStateReturnsOriginalStateInstance() {
        StateStub state = populatedState();
        StateCompositionPlan plan = semanticPlan();

        State projectedState = plan.projectState(
                state,
                CliStateProjectionMode.FULL_STATE,
                policyContext()
        );

        assertSame(state, projectedState);
    }

    @Test
    public void leafWidgetsIncludeOnlyWidgetsWithoutChildren() {
        StateStub state = new StateStub();
        WidgetStub parentWidget = addWidget(state, "parent", true, false);
        addChildWidget(state, parentWidget, "leaf-child", true, false);
        addWidget(state, "leaf-sibling", true, false);

        StateCompositionPlan plan = semanticPlan();
        State projectedState = plan.projectState(
                state,
                CliStateProjectionMode.LEAF_WIDGETS,
                policyContext()
        );

        Set<String> descriptions = widgetDescriptions(projectedState);
        assertEquals(2, descriptions.size());
        assertTrue(descriptions.contains("leaf-child"));
        assertTrue(descriptions.contains("leaf-sibling"));
        assertFalse(descriptions.contains("parent"));
    }

    @Test
    public void semanticWidgetsIncludeOnlyDescriptorSelectedWidgets() {
        StateStub state = populatedState();
        StateCompositionPlan plan = semanticPlan();

        State projectedState = plan.projectState(
                state,
                CliStateProjectionMode.SEMANTIC_WIDGETS,
                policyContext()
        );

        Set<String> descriptions = widgetDescriptions(projectedState);
        assertEquals(1, descriptions.size());
        assertTrue(descriptions.stream().anyMatch(description -> description.contains("semantic-message")));
    }

    @Test
    public void textualContextUsesSemanticProjection() {
        StateStub state = populatedState();
        StateCompositionPlan plan = semanticPlan();

        State projectedState = plan.projectState(
                state,
                CliStateProjectionMode.TEXTUAL_CONTEXT,
                policyContext()
        );

        Set<String> descriptions = widgetDescriptions(projectedState);
        assertEquals(1, descriptions.size());
        assertTrue(descriptions.stream().anyMatch(description -> description.contains("semantic-message")));
    }

    @Test
    public void interactiveWidgetsIncludeInteractionCapableWidgetsEvenWhenNotActionEligible() {
        StateStub state = new StateStub();
        addWidget(state, "click-visible", true, false);
        addWidget(state, "click-hidden", true, false);
        addWidget(state, "semantic-message", true, false);

        StateCompositionPlan plan = semanticPlan();
        State interactiveState = plan.projectState(
                state,
                CliStateProjectionMode.INTERACTIVE_WIDGETS,
                policyContext()
        );

        Set<String> descriptions = widgetDescriptions(interactiveState);
        assertEquals(2, descriptions.size());
        assertTrue(descriptions.contains("click-visible"));
        assertTrue(descriptions.contains("click-hidden"));
        assertFalse(descriptions.contains("semantic-message"));
    }

    @Test
    public void interactiveWidgetsIncludeAllInteractionPolicyKinds() {
        StateStub state = new StateStub();
        addWidget(state, "click-visible", true, false);
        addWidget(state, "type-visible", true, false);
        addWidget(state, "scroll-visible", true, false);
        addWidget(state, "select-visible", true, false);
        addWidget(state, "semantic-message", true, false);

        StateCompositionPlan plan = semanticPlan();
        State projectedState = plan.projectState(
                state,
                CliStateProjectionMode.INTERACTIVE_WIDGETS,
                policyContext()
        );

        Set<String> descriptions = widgetDescriptions(projectedState);
        assertEquals(4, descriptions.size());
        assertTrue(descriptions.contains("click-visible"));
        assertTrue(descriptions.contains("type-visible"));
        assertTrue(descriptions.contains("scroll-visible"));
        assertTrue(descriptions.contains("select-visible"));
        assertFalse(descriptions.contains("semantic-message"));
    }

    @Test
    public void actionableWidgetsRequireInteractionCapabilityAndActionEligibility() {
        StateStub state = new StateStub();
        addWidget(state, "click-visible", true, false);
        addWidget(state, "click-hidden", true, false);
        addWidget(state, "click-disabled", false, false);
        addWidget(state, "click-blocked", true, true);
        addWidget(state, "click-filtered", true, false);
        addWidget(state, "click-covered", true, false);
        addWidget(state, "semantic-message", true, false);

        StateCompositionPlan plan = semanticPlan();
        State actionableState = plan.projectState(
                state,
                CliStateProjectionMode.ACTIONABLE_WIDGETS,
                policyContext()
        );

        Set<String> descriptions = widgetDescriptions(actionableState);
        assertEquals(1, descriptions.size());
        assertTrue(descriptions.contains("click-visible"));
    }

    @Test
    public void interactiveSemanticWidgetsIncludeHiddenInteractiveWidgetsAndSemanticWidgets() {
        StateStub state = new StateStub();
        addWidget(state, "click-visible", true, false);
        addWidget(state, "click-hidden", true, false);
        addWidget(state, "semantic-message", true, false);

        StateCompositionPlan plan = semanticPlan();
        State projectedState = plan.projectState(
                state,
                CliStateProjectionMode.INTERACTIVE_SEMANTIC_WIDGETS,
                policyContext()
        );

        Set<String> descriptions = widgetDescriptions(projectedState);
        assertEquals(3, descriptions.size());
        assertTrue(descriptions.stream().anyMatch(description -> description.contains("click-visible")));
        assertTrue(descriptions.stream().anyMatch(description -> description.contains("click-hidden")));
        assertTrue(descriptions.stream().anyMatch(description -> description.contains("semantic-message")));
    }

    @Test
    public void actionableSemanticWidgetsIncludeSemanticWidgetsAndEligibleInteractiveWidgets() {
        StateStub state = new StateStub();
        addWidget(state, "click-visible", true, false);
        addWidget(state, "click-hidden", true, false);
        addWidget(state, "semantic-message", true, false);

        StateCompositionPlan plan = semanticPlan();
        State projectedState = plan.projectState(
                state,
                CliStateProjectionMode.ACTIONABLE_SEMANTIC_WIDGETS,
                policyContext()
        );

        Set<String> descriptions = widgetDescriptions(projectedState);
        assertEquals(2, descriptions.size());
        assertTrue(descriptions.stream().anyMatch(description -> description.contains("click-visible")));
        assertTrue(descriptions.stream().anyMatch(description -> description.contains("semantic-message")));
        assertFalse(descriptions.stream().anyMatch(description -> description.contains("click-hidden")));
    }

    @Test(expected = IllegalStateException.class)
    public void semanticObservationRequiresSemanticDescriptor() {
        StateStub state = populatedState();
        StateCompositionPlan nonSemanticPlan = StateCompositionPlan.fullState(system -> new StateStub());

        nonSemanticPlan.projectState(
                state,
                CliStateProjectionMode.SEMANTIC_WIDGETS,
                policyContext()
        );
    }

    @Test(expected = IllegalStateException.class)
    public void actionableSemanticObservationRequiresSemanticDescriptor() {
        StateStub state = populatedState();
        StateCompositionPlan nonSemanticPlan = StateCompositionPlan.fullState(system -> new StateStub());

        nonSemanticPlan.projectState(
                state,
                CliStateProjectionMode.ACTIONABLE_SEMANTIC_WIDGETS,
                policyContext()
        );
    }

    private StateStub populatedState() {
        StateStub state = new StateStub();
        addWidget(state, "click-visible", true, false);
        addWidget(state, "click-hidden", true, false);
        addWidget(state, "semantic-message", true, false);
        return state;
    }

    private StateCompositionPlan semanticPlan() {
        return StateCompositionPlan.semanticWidgets(system -> new StateStub(), semanticDescriptor());
    }

    private SemanticWidgetDescriptor semanticDescriptor() {
        return new SemanticWidgetDescriptor() {

            @Override
            public boolean shouldInclude(Widget widget) {
                return description(widget).startsWith("semantic");
            }

            @Override
            public String roleOf(Widget widget) {
                return "test";
            }

            @Override
            public String labelOf(Widget widget) {
                return description(widget);
            }

            @Override
            public String valueOf(Widget widget) {
                return "";
            }
        };
    }

    private SessionPolicyContext policyContext() {
        ClickablePolicy clickablePolicy = widget -> description(widget).startsWith("click");
        TypeablePolicy typeablePolicy = widget -> description(widget).startsWith("type");
        ScrollablePolicy scrollablePolicy = widget -> description(widget).startsWith("scroll");
        SelectablePolicy selectablePolicy = widget -> description(widget).startsWith("select");
        EnabledPolicy enabledPolicy = widget -> widget.get(Tags.Enabled, true);
        BlockedPolicy blockedPolicy = widget -> widget.get(Tags.Blocked, false);
        WidgetFilterPolicy widgetFilterPolicy = widget -> !description(widget).contains("filtered");
        VisiblePolicy visiblePolicy = widget -> !description(widget).contains("hidden");
        TopLevelPolicy topLevelPolicy = widget -> !description(widget).contains("covered");

        return new SessionPolicyContext(
                clickablePolicy,
                typeablePolicy,
                scrollablePolicy,
                selectablePolicy,
                enabledPolicy,
                blockedPolicy,
                widgetFilterPolicy,
                visiblePolicy,
                topLevelPolicy
        );
    }

    private WidgetStub addWidget(StateStub state, String description, boolean enabled, boolean blocked) {
        WidgetStub widget = new WidgetStub();
        widget.setRoot(state);
        widget.setParent(state);
        widget.set(Tags.Desc, description);
        widget.set(Tags.Enabled, enabled);
        widget.set(Tags.Blocked, blocked);
        state.addChild(widget);
        return widget;
    }

    private WidgetStub addChildWidget(StateStub state,
                                      WidgetStub parentWidget,
                                      String description,
                                      boolean enabled,
                                      boolean blocked) {
        WidgetStub widget = new WidgetStub();
        widget.setRoot(state);
        widget.setParent(parentWidget);
        widget.set(Tags.Desc, description);
        widget.set(Tags.Enabled, enabled);
        widget.set(Tags.Blocked, blocked);
        parentWidget.addChild(widget);
        return widget;
    }

    private Set<String> widgetDescriptions(State state) {
        Set<String> descriptions = new LinkedHashSet<>();
        for (Widget widget : state) {
            if (widget != state) {
                descriptions.add(description(widget));
            }
        }
        return descriptions;
    }

    private String description(Widget widget) {
        return widget.get(Tags.Desc, "");
    }
}
