package org.testar.engine.service;

import static org.junit.Assert.assertSame;

import java.util.Collections;

import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.action.resolver.ResolvedAction;
import org.testar.core.tag.TaggableBase;
import org.testar.engine.action.resolver.ActionResolverPlan;

public final class ComposedActionResolverTest {

    @Test
    public void resolveDelegatesToPlanResolver() {
        TestAction expectedAction = new TestAction();
        ResolvedAction expectedResolvedAction = new ResolvedAction(expectedAction);
        ComposedActionResolver resolver = ComposedActionResolver.compose(
                ActionResolverPlan.basic((actions, arguments) -> expectedResolvedAction)
        );

        ResolvedAction resolvedAction = resolver.resolve(Collections.emptySet(), Collections.emptyList());

        assertSame(expectedResolvedAction, resolvedAction);
        assertSame(expectedAction, resolvedAction.action());
    }

    private static final class TestAction extends TaggableBase implements Action {

        private static final long serialVersionUID = 1L;

        private TestAction() {
            set(org.testar.core.tag.Tags.Role, ActionRoles.NOPAction);
        }

        @Override
        public void run(org.testar.core.state.SUT system,
                        org.testar.core.state.State state,
                        double duration) {
        }

        @Override
        public String toShortString() {
            return "test";
        }

        @Override
        public String toParametersString() {
            return "";
        }

        @Override
        public String toString(org.testar.core.alayer.Role... discardParameters) {
            return "test";
        }
    }
}
