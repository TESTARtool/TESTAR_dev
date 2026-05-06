package org.testar.android.action.derivation;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.testar.android.action.AndroidBackAction;
import org.testar.android.action.AndroidSystemActionCall;
import org.testar.android.action.AndroidSystemActionOrientation;
import org.testar.android.action.AndroidSystemActionText;
import org.testar.core.action.Action;
import org.testar.engine.action.derivation.ActionDerivationPlan;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.stub.StateStub;

public final class AndroidActionDeriversTest {

    @Test
    public void systemActionDeriverHonorsFlag() {
        StateStub state = new StateStub();
        AndroidSystemActionDeriver enabledDeriver = new AndroidSystemActionDeriver(true);
        AndroidSystemActionDeriver disabledDeriver = new AndroidSystemActionDeriver(false);

        Set<Action> enabledActions = enabledDeriver.derive(null, state, new SessionPolicyContext());
        Set<Action> disabledActions = disabledDeriver.derive(null, state, new SessionPolicyContext());

        Assert.assertEquals(3, enabledActions.size());
        Assert.assertTrue(enabledActions.stream().anyMatch(AndroidSystemActionOrientation.class::isInstance));
        Assert.assertTrue(enabledActions.stream().anyMatch(AndroidSystemActionCall.class::isInstance));
        Assert.assertTrue(enabledActions.stream().anyMatch(AndroidSystemActionText.class::isInstance));
        Assert.assertTrue(disabledActions.isEmpty());
    }

    @Test
    public void backFallbackDeriverReturnsBackAction() {
        StateStub state = new StateStub();
        AndroidBackFallbackActionDeriver deriver = new AndroidBackFallbackActionDeriver();

        Set<Action> actions = deriver.derive(null, state, new SessionPolicyContext());

        Assert.assertEquals(1, actions.size());
        Assert.assertTrue(actions.iterator().next() instanceof AndroidBackAction);
    }

    @Test
    public void planUsesExpectedDerivationPhases() {
        ActionDerivationPlan plan = AndroidActionDerivationPlan.create(widget -> "hello", true);

        Assert.assertEquals(0, plan.forcedDerivers().size());
        Assert.assertEquals(2, plan.defaultDerivers().size());
        Assert.assertEquals(1, plan.fallbackDerivers().size());
        Assert.assertTrue(plan.fallbackDerivers().get(0) instanceof AndroidBackFallbackActionDeriver);
    }
}
