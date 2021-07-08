package nl.ou.testar.ReinforcementLearning.Policies;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class GreedyPolicyTest {

    final private Policy policy = new GreedyPolicy(0f, RLTags.getTag("sarsaValue"));

    @Test
    public void applyPolicy_returnsAnAction_whenASetWithOneActionIsProvided () {
        // given
        final Set<AbstractAction> actions = new HashSet<>();

        final AbstractAction abstractAction = new AbstractAction("1");
        abstractAction.getAttributes().set(RLTags.SarsaValue, 1f);
        actions.add(abstractAction);

        // when
        final AbstractAction selectedAction = policy.applyPolicy(actions);

        // then
        assertEquals(abstractAction, selectedAction);
    }

    @Test
    public void applyPolicy_returnsAnAction_whenASetWithTwoActionsWithTheSameQValuesIsProvided () {
        // given
        final Set<AbstractAction> actions = new HashSet<>();

        final AbstractAction abstractActionOne = new AbstractAction("1");
        abstractActionOne.getAttributes().set(RLTags.SarsaValue, 1f);
        actions.add(abstractActionOne);

        final AbstractAction abstractActionTwo = new AbstractAction("2");
        abstractActionTwo.getAttributes().set(RLTags.SarsaValue, 1f);
        actions.add(abstractActionTwo);

        // when
        final AbstractAction selectedAction = policy.applyPolicy(actions);

        // then
        assertNotNull(selectedAction);
    }

    @Test
    public void applyPolicy_returnsActionWithMaxQValue_whenASetWithTwoActionsWithDifferentQValuesIsProvided () {
        // given
        final Set<AbstractAction> actions = new HashSet<>();

        final AbstractAction abstractActionOne = new AbstractAction("1");
        abstractActionOne.getAttributes().set(RLTags.SarsaValue, 1f);
        actions.add(abstractActionOne);

        final AbstractAction abstractActionTwo = new AbstractAction("2");
        abstractActionTwo.getAttributes().set(RLTags.SarsaValue, 0.8f);
        actions.add(abstractActionTwo);

        // when
        final AbstractAction selectedAction = policy.applyPolicy(actions);

        // then
        assertEquals(abstractActionOne, selectedAction);
    }

    @Test
    public void applyPolicy_returnsNull_whenAnEmptySetIsProvided () {
        // given
        final Set<AbstractAction> actions = new HashSet<>();

        // when
        final AbstractAction selectedAction = policy.applyPolicy(actions);

        // then
        assertNull(selectedAction);
    }
}
