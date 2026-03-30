package org.testar.engine.action;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.action.policy.ClickablePolicy;
import org.testar.core.action.policy.ScrollablePolicy;
import org.testar.core.action.policy.TypeablePolicy;
import org.testar.core.action.policy.WidgetFilterPolicy;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.stub.StateStub;
public final class DefaultActionDerivationServiceTest {

    @Test
    public void derivesActionsThroughConfiguredDerivers() {
        State state = new StateStub();
        DefaultActionDerivationService service = DefaultActionDerivationService.withPolicies(
                Collections.singletonList(widget -> true),
                Collections.singletonList(widget -> false),
                Collections.singletonList(widget -> false),
                Collections.singletonList(widget -> true),
                Collections.singletonList((system, currentState, context, actions) -> actions.add(new TestAction()))
        );

        Set<Action> actions = service.deriveActions(null, state);

        Assert.assertEquals(1, actions.size());
    }

    @Test
    public void prioritizesForcedActionsOverDefaultAndFallbackActions() {
        State state = new StateStub();
        DefaultActionDerivationService service = DefaultActionDerivationService.prioritizedWithPolicies(
                Collections.singletonList(widget -> true),
                Collections.singletonList(widget -> false),
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
        State state = new StateStub();
        DefaultActionDerivationService service = DefaultActionDerivationService.prioritizedWithPolicies(
                Collections.singletonList(widget -> true),
                Collections.singletonList(widget -> false),
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
        WidgetFilterPolicy filter = widget -> true;

        DefaultActionDerivationService service = DefaultActionDerivationService.withPolicies(
                Arrays.asList(clickable),
                Arrays.asList(typeable),
                Arrays.asList(scrollable),
                Arrays.asList(filter),
                Collections.emptyList()
        );

        Widget widget = new StateStub();
        Assert.assertTrue(service.context().clickablePolicy().isClickable(widget));
        Assert.assertFalse(service.context().typeablePolicy().isTypeable(widget));
        Assert.assertFalse(service.context().scrollablePolicy().isScrollable(widget));
        Assert.assertTrue(service.context().widgetFilterPolicy().allows(widget));
    }

    private static final class TestAction extends org.testar.core.tag.TaggableBase implements Action {

        private static final long serialVersionUID = 1L;
        private final String label;

        private TestAction() {
            this("test");
        }

        private TestAction(String label) {
            this.label = label;
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
