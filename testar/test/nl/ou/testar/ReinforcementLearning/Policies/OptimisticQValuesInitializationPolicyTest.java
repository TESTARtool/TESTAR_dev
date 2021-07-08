package nl.ou.testar.ReinforcementLearning.Policies;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class OptimisticQValuesInitializationPolicyTest {

    private final static Policy policy =  new OptimisticQValuesInitializationPolicy(new GreedyPolicy(0.0f, RLTags.getTag("sarsaValue")), 1.0f);

    @Test
    public void applyPolicy_returnsAnAction_whenASetWithOneActionIsProvided () {
        // given
        final Set<AbstractAction> actions = new HashSet<>();

        final AbstractAction abstractAction = new AbstractAction("1");
        abstractAction.getAttributes().set(RLTags.SarsaValue, 1.0f);
        actions.add(abstractAction);

        // when
        final AbstractAction selectedAction = policy.applyPolicy(actions);

        // then
        assertEquals(abstractAction, selectedAction);
    }

    @Test
    public void applyPolicy_returnsAnAction_whenASetWithOneActionWithNoQValueIsProvided () {
        // given
        final Set<AbstractAction> actions = new HashSet<>();

        final AbstractAction abstractAction = new AbstractAction("1");
        actions.add(abstractAction);

        // when
        final AbstractAction selectedAction = policy.applyPolicy(actions);

        // then
        assertEquals(abstractAction, selectedAction);

    }

    @Test
    public void applyPolicy_returnsARandomAction_whenASetWithTwoActionsWithTheNoQValuesIsProvided () {
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
    public void applyPolicy_returnsTheActionWithNoQValue_whenASetWithTwoActionsOfWhichOneActionHasNoQValueIsProvided () {
        // given
        final Set<AbstractAction> actions = new HashSet<>();

        final AbstractAction abstractActionOne = new AbstractAction("1");
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
