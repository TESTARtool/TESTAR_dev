package org.testar.statemodel.changedetection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.testar.monkey.alayer.Tags;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.exceptions.StateModelException;

public class StateSnapshotFactoryTest {

    @Test
    public void testStateSnapshotsFactory() throws StateModelException {
        AbstractAction aa1 = new AbstractAction("AA1");
        AbstractAction aa2 = new AbstractAction("AA2");

        AbstractState as1 = new AbstractState("AS1", new HashSet<>(Arrays.asList(aa1)));
        AbstractState as2 = new AbstractState("AS2", new HashSet<>(Arrays.asList(aa2)));

        as1.addConcreteStateId("CS1");
        as2.addConcreteStateId("CS2");

        AbstractStateModel model = new AbstractStateModel("model-1", "app", "1.0",
                Collections.singleton(Tags.Title));

        // create circular transitions: AS1 -> AA1 -> AS2 -> AA2 -> AS1
        model.addTransition(as1, as2, aa1);
        model.addTransition(as2, as1, aa2);

        List<StateSnapshot> stateSnapshots = StateSnapshotFactory.from(model);
        assertEquals(2, stateSnapshots.size());

        StateSnapshot stateOne = stateSnapshots.stream().filter(s -> s.getStateId().equals("AS1")).findFirst().orElseThrow();
        StateSnapshot stateTwo = stateSnapshots.stream().filter(s -> s.getStateId().equals("AS2")).findFirst().orElseThrow();

        assertEquals(Collections.singletonList("CS1"), stateOne.getConcreteStateIds());
        assertEquals(Collections.singletonList("CS2"), stateTwo.getConcreteStateIds());

        assertTrue(stateOne.getOutgoingActionIds().contains("AA1"));
        assertTrue(stateOne.getIncomingActionIds().contains("AA2"));

        assertTrue(stateTwo.getOutgoingActionIds().contains("AA2"));
        assertTrue(stateTwo.getIncomingActionIds().contains("AA1"));
    }

}
