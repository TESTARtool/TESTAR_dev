package org.testar.engine.action.derivation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.alayer.Role;
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
import org.testar.core.tag.TaggableBase;
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
import org.testar.stub.WidgetStub;

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

    @Test
    public void disambiguatesDuplicateDescriptionsBySharedParentOrder() {
        State state = createStateStub();
        WidgetStub container = child(state, "Question container", "0");
        WidgetStub firstField = child(container, "Field one", "0/0");
        WidgetStub secondField = child(container, "Field two", "0/1");

        TestAction firstAction = new TestAction("type");
        firstAction.set(Tags.Desc, "Type into field with hint 'Option'");
        firstAction.mapOriginWidget(firstField);

        TestAction secondAction = new TestAction("type");
        secondAction.set(Tags.Desc, "Type into field with hint 'Option'");
        secondAction.mapOriginWidget(secondField);

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
                                (system, currentState, ctx) -> Set.of(firstAction, secondAction)
                        ),
                        Collections.emptyList()
                )
        );

        List<Action> actions = List.copyOf(service.deriveActions(null, state));

        Assert.assertEquals("Type into field with hint 'Option' [within 'Question container' #1]",
                actions.get(0).get(Tags.Desc));
        Assert.assertEquals("Type into field with hint 'Option' [within 'Question container' #2]",
                actions.get(1).get(Tags.Desc));
    }

    @Test
    public void keepsUniqueDescriptionsUnchanged() {
        State state = createStateStub();
        WidgetStub widget = child(state, "Only field", "0");

        TestAction action = new TestAction("type");
        action.set(Tags.Desc, "Type into field with hint 'Email'");
        action.mapOriginWidget(widget);

        ComposedActionDerivationService service = new ComposedActionDerivationService(
                context(
                        Collections.singletonList(widgetPolicy -> true),
                        Collections.singletonList(widgetPolicy -> false),
                        Collections.singletonList(widgetPolicy -> false),
                        Collections.singletonList(widgetPolicy -> true),
                        Collections.singletonList(widgetPolicy -> false),
                        Collections.singletonList(widgetPolicy -> true)
                ),
                new ActionDerivationPlan(
                        Collections.emptyList(),
                        Collections.<ActionDeriver>singletonList(
                                (system, currentState, ctx) -> Collections.singleton(action)
                        ),
                        Collections.emptyList()
                )
        );

        Action derivedAction = service.deriveActions(null, state).iterator().next();

        Assert.assertEquals("Type into field with hint 'Email'", derivedAction.get(Tags.Desc));
    }

    @Test
    public void disambiguatesTypeActionsWhileKeepingOriginalInputText() {
        State state = createStateStub();
        WidgetStub container = child(state, "Answer group", "0");
        WidgetStub firstField = child(container, "Answer one", "0/0");
        WidgetStub secondField = child(container, "Answer two", "0/1");

        TestAction firstAction = new TestAction("type");
        firstAction.set(Tags.InputText, "2017-22-03");
        firstAction.set(Tags.Desc, "Remote scroll and type 2017-22-03 to widget input_type_your_answer_here..");
        firstAction.mapOriginWidget(firstField);

        TestAction secondAction = new TestAction("type");
        secondAction.set(Tags.InputText, "www.boo.com");
        secondAction.set(Tags.Desc, "Remote scroll and type www.boo.com to widget input_type_your_answer_here..");
        secondAction.mapOriginWidget(secondField);

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
                                (system, currentState, ctx) -> Set.of(firstAction, secondAction)
                        ),
                        Collections.emptyList()
                )
        );

        List<Action> actions = List.copyOf(service.deriveActions(null, state));

        Assert.assertEquals(
                "Remote scroll and type 2017-22-03 to widget input_type_your_answer_here.. [within 'Answer group' #1]",
                actions.get(0).get(Tags.Desc));
        Assert.assertEquals(
                "Remote scroll and type www.boo.com to widget input_type_your_answer_here.. [within 'Answer group' #2]",
                actions.get(1).get(Tags.Desc));
    }

    private State createStateStub() {
        StateStub state = new StateStub();
        state.set(Tags.ConcreteID, "state-concrete-id");
        state.set(Tags.AbstractID, "state-abstract-id");
        return state;
    }

    private WidgetStub child(Widget parent, String description, String path) {
        WidgetStub child = new WidgetStub();
        child.setRoot(parent.root());
        child.setParent(parent);
        child.set(Tags.Desc, description);
        child.set(Tags.Path, path);
        ((WidgetStub) parent).addChild(child);
        return child;
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

    private static final class TestAction extends TaggableBase implements Action {

        private static final long serialVersionUID = 1L;
        private final String label;

        private TestAction() {
            this("test");
        }

        private TestAction(String label) {
            this.label = label;
            set(Tags.Role, ActionRoles.NOPAction);
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
        public String toString(Role... discardParameters) {
            return label;
        }
    }
}
