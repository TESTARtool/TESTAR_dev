package org.testar.engine.action.derivation;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.policy.AtCanvasPolicy;
import org.testar.core.policy.BlockedPolicy;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.policy.EnabledPolicy;
import org.testar.core.policy.ScrollablePolicy;
import org.testar.core.policy.TopLevelPolicy;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.policy.VisiblePolicy;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.engine.policy.composite.CompositeAtCanvasPolicy;
import org.testar.engine.policy.composite.CompositeBlockedPolicy;
import org.testar.engine.policy.composite.CompositeClickablePolicy;
import org.testar.engine.policy.composite.CompositeEnabledPolicy;
import org.testar.engine.policy.composite.CompositeScrollablePolicy;
import org.testar.engine.policy.composite.CompositeTopLevelPolicy;
import org.testar.engine.policy.composite.CompositeTypeablePolicy;
import org.testar.engine.policy.composite.CompositeVisiblePolicy;
import org.testar.engine.policy.composite.CompositeWidgetFilterPolicy;
import org.testar.engine.service.ComposedActionDerivationService;
import org.testar.stub.StateStub;

public final class ComposedActionDerivationServiceTest {

    @Test
    public void derivesActionsThroughConfiguredDerivers() {
        State state = createStateStub();
        ComposedActionDerivationService service = new ComposedActionDerivationService(
                context(
                        Collections.singletonList(widget -> true),
                        Collections.singletonList(widget -> false),
                        Collections.singletonList(widget -> false),
                        Collections.singletonList(widget -> true),
                        Collections.singletonList(widget -> false),
                        Collections.singletonList(widget -> true)
                ),
                new ActionDerivationPlan(
                        Collections.emptyList(),
                        Collections.<ActionDeriver>singletonList(
                                (system, currentState, context) -> Collections.singleton(new TestAction())
                        ),
                        Collections.emptyList()
                )
        );

        Set<Action> actions = service.deriveActions(null, state);

        Assert.assertEquals(1, actions.size());
    }

    @Test
    public void prioritizesForcedActionsOverDefaultAndFallbackActions() {
        State state = createStateStub();
        ComposedActionDerivationService service = new ComposedActionDerivationService(
                context(
                        Collections.singletonList(widget -> true),
                        Collections.singletonList(widget -> false),
                        Collections.singletonList(widget -> false),
                        Collections.singletonList(widget -> true),
                        Collections.singletonList(widget -> false),
                        Collections.singletonList(widget -> true)
                ),
                new ActionDerivationPlan(
                        Collections.<ActionDeriver>singletonList(
                                (system, currentState, context) -> Collections.singleton(new TestAction("forced"))
                        ),
                        Collections.<ActionDeriver>singletonList(
                                (system, currentState, context) -> Collections.singleton(new TestAction("default"))
                        ),
                        Collections.<ActionDeriver>singletonList(
                                (system, currentState, context) -> Collections.singleton(new TestAction("fallback"))
                        )
                )
        );

        Set<Action> actions = service.deriveActions(null, state);

        Assert.assertEquals(1, actions.size());
        Assert.assertEquals("forced", actions.iterator().next().toShortString());
    }

    @Test
    public void usesFallbackActionsWhenForcedAndDefaultActionsAreEmpty() {
        State state = createStateStub();
        ComposedActionDerivationService service = new ComposedActionDerivationService(
                context(
                        Collections.singletonList(widget -> true),
                        Collections.singletonList(widget -> false),
                        Collections.singletonList(widget -> false),
                        Collections.singletonList(widget -> true),
                        Collections.singletonList(widget -> false),
                        Collections.singletonList(widget -> true)
                ),
                new ActionDerivationPlan(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.<ActionDeriver>singletonList(
                                (system, currentState, context) -> Collections.singleton(new TestAction("fallback"))
                        )
                )
        );

        Set<Action> actions = service.deriveActions(null, state);

        Assert.assertEquals(1, actions.size());
        Assert.assertEquals("fallback", actions.iterator().next().toShortString());
    }

    @Test
    public void exposesComposedPoliciesThroughContext() {
        ClickablePolicy clickable = widget -> true;
        TypeablePolicy typeable = widget -> false;
        ScrollablePolicy scrollable = widget -> false;
        EnabledPolicy enabled = widget -> true;
        BlockedPolicy blocked = widget -> false;
        WidgetFilterPolicy filter = widget -> true;
        VisiblePolicy visible = widget -> true;
        AtCanvasPolicy atCanvas = widget -> true;
        TopLevelPolicy topLevel = widget -> true;

        ComposedActionDerivationService service = new ComposedActionDerivationService(
                context(
                        Arrays.asList(clickable),
                        Arrays.asList(typeable),
                        Arrays.asList(scrollable),
                        Arrays.asList(enabled),
                        Arrays.asList(blocked),
                        Arrays.asList(filter),
                        Arrays.asList(visible),
                        Arrays.asList(atCanvas),
                        Arrays.asList(topLevel)
                ),
                new ActionDerivationPlan(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                )
        );

        Widget widget = new StateStub();
        Assert.assertTrue(service.context().require(ClickablePolicy.class).isClickable(widget));
        Assert.assertFalse(service.context().require(TypeablePolicy.class).isTypeable(widget));
        Assert.assertFalse(service.context().require(ScrollablePolicy.class).isScrollable(widget));
        Assert.assertTrue(service.context().require(EnabledPolicy.class).isEnabled(widget));
        Assert.assertFalse(service.context().require(BlockedPolicy.class).isBlocked(widget));
        Assert.assertTrue(service.context().require(WidgetFilterPolicy.class).allows(widget));
        Assert.assertTrue(service.context().require(VisiblePolicy.class).isVisible(widget));
        Assert.assertTrue(service.context().require(AtCanvasPolicy.class).isAtCanvas(widget));
        Assert.assertTrue(service.context().require(TopLevelPolicy.class).isTopLevel(widget));
    }

    private State createStateStub() {
        StateStub state = new StateStub();
        state.set(Tags.ConcreteID, "state-concrete-id");
        state.set(Tags.AbstractID, "state-abstract-id");
        return state;
    }

    private SessionPolicyContext context(java.util.List<ClickablePolicy> clickablePolicies,
                                         java.util.List<TypeablePolicy> typeablePolicies,
                                         java.util.List<ScrollablePolicy> scrollablePolicies,
                                         java.util.List<EnabledPolicy> enabledPolicies,
                                         java.util.List<BlockedPolicy> blockedPolicies,
                                         java.util.List<WidgetFilterPolicy> widgetFilterPolicies) {
        return context(
                clickablePolicies,
                typeablePolicies,
                scrollablePolicies,
                enabledPolicies,
                blockedPolicies,
                widgetFilterPolicies,
                Collections.singletonList(widget -> true),
                Collections.singletonList(widget -> true),
                Collections.singletonList(widget -> true)
        );
    }

    private SessionPolicyContext context(java.util.List<ClickablePolicy> clickablePolicies,
                                         java.util.List<TypeablePolicy> typeablePolicies,
                                         java.util.List<ScrollablePolicy> scrollablePolicies,
                                         java.util.List<EnabledPolicy> enabledPolicies,
                                         java.util.List<BlockedPolicy> blockedPolicies,
                                         java.util.List<WidgetFilterPolicy> widgetFilterPolicies,
                                         java.util.List<VisiblePolicy> visiblePolicies,
                                         java.util.List<AtCanvasPolicy> atCanvasPolicies,
                                         java.util.List<TopLevelPolicy> topLevelPolicies) {
        return new SessionPolicyContext(
                new CompositeClickablePolicy(clickablePolicies),
                new CompositeTypeablePolicy(typeablePolicies),
                new CompositeScrollablePolicy(scrollablePolicies),
                new CompositeEnabledPolicy(enabledPolicies),
                new CompositeBlockedPolicy(blockedPolicies),
                new CompositeWidgetFilterPolicy(widgetFilterPolicies),
                new CompositeVisiblePolicy(visiblePolicies),
                new CompositeAtCanvasPolicy(atCanvasPolicies),
                new CompositeTopLevelPolicy(topLevelPolicies)
        );
    }

    private static final class TestAction extends org.testar.core.tag.TaggableBase implements Action {

        private static final long serialVersionUID = 1L;
        private final String label;

        private TestAction() {
            this("test");
        }

        private TestAction(String label) {
            this.label = label;
            set(org.testar.core.tag.Tags.Role, ActionRoles.NOPAction);
        }

        @Override
        public void run(SUT system, State state, double duration) {
        }

        @Override
        public String toShortString() {
            return label;
        }

        @Override
        public String toParametersString() {
            return "";
        }

        @Override
        public String toString(org.testar.core.alayer.Role... discardParameters) {
            return label;
        }
    }
}
