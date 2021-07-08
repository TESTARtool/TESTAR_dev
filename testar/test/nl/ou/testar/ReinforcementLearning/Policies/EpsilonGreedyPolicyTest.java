package nl.ou.testar.ReinforcementLearning.Policies;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class EpsilonGreedyPolicyTest {

    @Spy private final EpsilonGreedyPolicy policy = new EpsilonGreedyPolicy(new GreedyPolicy(0.0f, RLTags.getTag("sarsaValue")), 0.7f);

    @Before
    public void setup () {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void applyPolicy_returnsAnAction_whenEpsilonIsZero() {
        // given
        when(policy.getRandomValue()).thenReturn(0.0f);

        final Set<AbstractAction> actions = new HashSet<>();

        final AbstractAction abstractAction = new AbstractAction("1");
        actions.add(abstractAction);

        // when
        final AbstractAction selectedAction = policy.applyPolicy(actions);

        // then
        assertEquals(abstractAction, selectedAction);
    }

    @Test
    public void applyPolicy_returnsAnAction_whenEpsilonIsOne() {
        // given
        when(policy.getRandomValue()).thenReturn(1.0f);
        final Set<AbstractAction> actions = new HashSet<>();

        final AbstractAction abstractAction = new AbstractAction("1");
        actions.add(abstractAction);

        // when
        final AbstractAction selectedAction = policy.applyPolicy(actions);

        // then
        assertEquals(abstractAction, selectedAction);
    }
}
