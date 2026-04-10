package org.testar.engine.action.derivation;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.policy.BlockedPolicy;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.policy.EnabledPolicy;
import org.testar.core.policy.ScrollablePolicy;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.stub.StateStub;
public final class DefaultActionDerivationServiceTest {

    @Test
    public void derivesActionsThroughConfiguredDerivers() {
        State state = createStateStub();
        DefaultActionDerivationService service = DefaultActionDerivationService.withPolicies(
                Collections.singletonList(widget -> true),
                Collections.singletonList(widget -> false),
                Collections.singletonList(widget -> false),
                Collections.singletonList(widget -> true),
                Collections.singletonList(widget -> false),
                Collections.singletonList(widget -> true),
                Collections.singletonList((system, currentState, context, actions) -> actions.add(new TestAction()))
        );

        Set<Action> actions = service.deriveActions(null, state);

        Assert.assertEquals(1, actions.size());
    }

    @Test
    public void prioritizesForcedActionsOverDefaultAndFallbackActions() {
        State state = createStateStub();
        DefaultActionDerivationService service = DefaultActionDerivationService.prioritizedWithPolicies(
                Collections.singletonList(widget -> true),
                Collections.singletonList(widget -> false),
                Collections.singletonList(widget -> false),
                Collections.singletonList(widget -> true),
                Collections.singletonList(widget -> false),
                Collections.singletonList(widget -> true),
                Collections.singletonList((system, currentState, context, actions) -> actions.add(new TestAction("forced"))),
                Collections.singletonList((system, currentState, context, actions) -> actions.add(new TestAction("default"))),
                Collections.singletonList((system, currentState, context, actions) -> actions.add(new TestAction("fallback")))
        );

        Set<Action> actions = service.deriveActions(null, state);

        Assert.assertEquals(1, actions.size());
        Assert.assertEquals("forced", actions.iterator().next().toShortString());
    }

    @Test
    public void usesFallbackActionsWhenForcedAndDefaultActionsAreEmpty() {
        State state = createStateStub();
        DefaultActionDerivationService service = DefaultActionDerivationService.prioritizedWithPolicies(
                Collections.singletonList(widget -> true),
                Collections.singletonList(widget -> false),
                Collections.singletonList(widget -> false),
                Collections.singletonList(widget -> true),
                Collections.singletonList(widget -> false),
                Collections.singletonList(widget -> true),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.singletonList((system, currentState, context, actions) -> actions.add(new TestAction("fallback")))
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

        DefaultActionDerivationService service = DefaultActionDerivationService.withPolicies(
                Arrays.asList(clickable),
                Arrays.asList(typeable),
                Arrays.asList(scrollable),
                Arrays.asList(enabled),
                Arrays.asList(blocked),
                Arrays.asList(filter),
                Collections.emptyList()
        );

        Widget widget = new StateStub();
        Assert.assertTrue(service.context().clickablePolicy().isClickable(widget));
        Assert.assertFalse(service.context().typeablePolicy().isTypeable(widget));
        Assert.assertFalse(service.context().scrollablePolicy().isScrollable(widget));
        Assert.assertTrue(service.context().enabledPolicy().isEnabled(widget));
        Assert.assertFalse(service.context().blockedPolicy().isBlocked(widget));
        Assert.assertTrue(service.context().widgetFilterPolicy().allows(widget));
    }

    private State createStateStub() {
        StateStub state = new StateStub();
        state.set(Tags.ConcreteID, "state-concrete-id");
        state.set(Tags.AbstractID, "state-abstract-id");
        return state;
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
