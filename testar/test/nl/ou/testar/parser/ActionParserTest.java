package nl.ou.testar.parser;

import static org.junit.Assert.*;

import org.junit.Test;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.KeyDown;
import org.testar.monkey.alayer.actions.KeyUp;
import org.testar.monkey.alayer.actions.MouseDown;
import org.testar.monkey.alayer.actions.MouseMove;
import org.testar.monkey.alayer.actions.MouseUp;
import org.testar.monkey.alayer.actions.Type;

import java.util.List;

public class ActionParserTest {

    final static String TEST_ACTION = "Compound Action = Move mouse to (89.5, 55.5). Press Mouse Button BUTTON1 Release Mouse Button BUTTON1 Press Key VK_CONTROL Press Key VK_A Release Key VK_A Release Key VK_CONTROL Type text '-1153660869'";

    @Test
    public void testActionParser() {
        final ActionParser parser = new ActionParser();
        try {
            final Pair<Action, String> result = parser.parse(TEST_ACTION);

            assertTrue(result.left().getClass().isAssignableFrom(CompoundAction.class));
            assertEquals(result.right().trim(), "");

            final CompoundAction compoundAction = (CompoundAction) result.left();
            final List<Action> actions = compoundAction.getActions();
            assertNotNull(actions);
            assertEquals(actions.size(), 8);
            assertTrue(actions.get(0).getClass().isAssignableFrom(MouseMove.class));
            assertTrue(actions.get(1).getClass().isAssignableFrom(MouseDown.class));
            assertTrue(actions.get(2).getClass().isAssignableFrom(MouseUp.class));
            assertTrue(actions.get(3).getClass().isAssignableFrom(KeyDown.class));
            assertTrue(actions.get(4).getClass().isAssignableFrom(KeyDown.class));
            assertTrue(actions.get(5).getClass().isAssignableFrom(KeyUp.class));
            assertTrue(actions.get(6).getClass().isAssignableFrom(KeyUp.class));
            assertTrue(actions.get(7).getClass().isAssignableFrom(Type.class));
        }
        catch(ActionParseException e) {
            fail(e.getMessage());
        }
    }
}
