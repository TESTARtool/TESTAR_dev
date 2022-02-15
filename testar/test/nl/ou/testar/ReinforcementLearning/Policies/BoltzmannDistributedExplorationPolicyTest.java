package nl.ou.testar.ReinforcementLearning.Policies;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

public class BoltzmannDistributedExplorationPolicyTest {

    @Spy
    private final BoltzmannDistributedExplorationPolicy policy = new BoltzmannDistributedExplorationPolicy(0.0001f, 0.7f);

    @Before
    public void setup () {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void applyPolicy_returnsARandomAction_whenASetWithTwoActionsAreProvided () {
        // given
        final Set<AbstractAction> actions = new HashSet<>();

        final AbstractAction abstractActionOne = new AbstractAction("1");
        abstractActionOne.getAttributes().set(RLTags.SarsaValue, 1.0f);
        actions.add(abstractActionOne);

        final AbstractAction abstractActionTwo = new AbstractAction("2");
        actions.add(abstractActionTwo);

        // when
        final AbstractAction selectedAction = policy.applyPolicy(actions);

        // then
        assertNotNull(selectedAction);
    }

    @Test
    public void applyPolicy_returnsARandomAction_whenASetWithTwoActionsWithoutQValuesAreProvided () {
        // given
        final Set<AbstractAction> actions = new HashSet<>();

        final AbstractAction abstractActionOne = new AbstractAction("1");
        actions.add(abstractActionOne);

        final AbstractAction abstractActionTwo = new AbstractAction("2");
        actions.add(abstractActionTwo);

        // when
        final AbstractAction selectedAction = policy.applyPolicy(actions);

        // then
        assertNotNull(selectedAction);
    }

    @Test
    public void applyPolicy_returnsARandomAction_whenTheBoltzmannTemperatureIsZero () {
        // given
        policy.boltzmannTemperature = 0.0f;
        final Set<AbstractAction> actions = new HashSet<>();

        final AbstractAction abstractActionOne = new AbstractAction("1");
        abstractActionOne.getAttributes().set(RLTags.SarsaValue, 1.0f);
        actions.add(abstractActionOne);

        final AbstractAction abstractActionTwo = new AbstractAction("2");
        actions.add(abstractActionTwo);

        // when
        final AbstractAction selectedAction = policy.applyPolicy(actions);

        // then
        assertNotNull(selectedAction);
    }
}
