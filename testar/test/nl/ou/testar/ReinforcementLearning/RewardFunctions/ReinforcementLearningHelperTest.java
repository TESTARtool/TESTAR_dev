package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.Utils.ReinforcementLearningUtil;
import nl.ou.testar.StateModel.AbstractAction;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.*;

public class ReinforcementLearningHelperTest {

    @Test
    public void selectAction_returnsARandomAction_whenMoreThanOneActionIsProvided () {
        // given
        final Collection<AbstractAction> actionsSelected = new HashSet<>();
        final AbstractAction abstractActionOne = new AbstractAction("1");
        actionsSelected.add(abstractActionOne);

        final AbstractAction abstractActionTwo = new AbstractAction("2");
        actionsSelected.add(abstractActionTwo);

        // when
        final AbstractAction actionSelected = ReinforcementLearningUtil.selectAction(actionsSelected);

        // then
        assertNotNull(actionSelected);
    }

    @Test
    public void selectAction_returnsTheProvidedAction_whenOneActionIsProvided () {
        // given
        final Collection<AbstractAction> actionsSelected = new HashSet<>();
        final AbstractAction abstractAction = new AbstractAction("1");
        actionsSelected.add(abstractAction);

        // when
        final AbstractAction actionSelected = ReinforcementLearningUtil.selectAction(actionsSelected);

        // then
        assertEquals(abstractAction, actionSelected);
    }

    @Test
    public void selectAction_returnsNull_whenAnEmptyCollectionIsProvided () {
        final Collection<AbstractAction> actionsSelected = new HashSet<>();

        // when
        final AbstractAction actionSelected = ReinforcementLearningUtil.selectAction(actionsSelected);

        // then
        assertNull(actionSelected);
    }
    
}
