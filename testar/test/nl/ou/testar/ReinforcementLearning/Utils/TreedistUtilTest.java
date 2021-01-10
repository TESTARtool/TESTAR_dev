package nl.ou.testar.ReinforcementLearning.Utils;

import nl.ou.testar.ReinforcementLearning.RewardFunctions.StateStub;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.WidgetStub;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.Assert.*;

public class TreedistUtilTest {
    // tree 1
    final WidgetStub widgetT1A = new WidgetStub();
    final WidgetStub widgetT1B = new WidgetStub();
    final WidgetStub widgetT1C = new WidgetStub();
    final WidgetStub widgetT1D = new WidgetStub();
    final WidgetStub widgetT1E = new WidgetStub();
    final WidgetStub widgetT1F = new WidgetStub();

    // tree 2
    final WidgetStub widgetT2A = new WidgetStub();
    final WidgetStub widgetT2B = new WidgetStub();
    final WidgetStub widgetT2C = new WidgetStub();
    final WidgetStub widgetT2D = new WidgetStub();
    final WidgetStub widgetT2E = new WidgetStub();
    final WidgetStub widgetT2F = new WidgetStub();

    @Before
    public void setup() {
        // tree 1
        widgetT1A.set(Tags.AbstractIDCustom, "a");
        widgetT1B.set(Tags.AbstractIDCustom, "b");
        widgetT1C.set(Tags.AbstractIDCustom, "c");
        widgetT1D.set(Tags.AbstractIDCustom, "d");
        widgetT1E.set(Tags.AbstractIDCustom, "e");
        widgetT1F.set(Tags.AbstractIDCustom, "f");

        // tree 2
        widgetT2A.set(Tags.AbstractIDCustom, "a");
        widgetT2B.set(Tags.AbstractIDCustom, "b");
        widgetT2C.set(Tags.AbstractIDCustom, "c");
        widgetT2D.set(Tags.AbstractIDCustom, "d");
        widgetT2E.set(Tags.AbstractIDCustom, "e");
        widgetT2F.set(Tags.AbstractIDCustom, "f");

        // tree 1
        widgetT1F.addChild(widgetT1D);
        widgetT1D.setParent(widgetT1F);
        widgetT1F.addChild(widgetT1E);
        widgetT1E.setParent(widgetT1F);
        widgetT1D.addChild(widgetT1A);
        widgetT1A.setParent(widgetT1D);
        widgetT1D.addChild(widgetT1C);
        widgetT1C.setParent(widgetT1D);
        widgetT1C.addChild(widgetT1B);
        widgetT1B.setParent(widgetT1C);

        // tree 2
        widgetT2F.addChild(widgetT2C);
        widgetT2C.setParent(widgetT2F);
        widgetT2F.addChild(widgetT2E);
        widgetT2E.setParent(widgetT2F);
        widgetT2C.addChild(widgetT2D);
        widgetT2D.setParent(widgetT2C);
        widgetT2D.addChild(widgetT2A);
        widgetT2A.setParent(widgetT2D);
        widgetT2D.addChild(widgetT2B);
        widgetT2B.setParent(widgetT2D);
    }

    @Test
    public void abstractRepresentationEmpty() {
        // when
        final String abstractRepresentation = new WidgetStub().getAbstractRepresentation();

        // then
        assertEquals("AbstractIDCustom=null", abstractRepresentation);
    }

    @Test
    public void abstractRepresentation() {
        // when
        final String abstractRepresentation = widgetT1A.getAbstractRepresentation();

        // then
        assertEquals("AbstractIDCustom=a", abstractRepresentation);
    }

    @Test
    public void getPostOrder () {
        // when
        final ArrayDeque<Widget> result = new ArrayDeque<>();
         TreedistUtil.getPostOrder(widgetT2F, result);

        // then
        assertEquals(widgetT2A, result.removeFirst());
        assertEquals(widgetT2B, result.removeFirst());
        assertEquals(widgetT2D, result.removeFirst());
        assertEquals(widgetT2C, result.removeFirst());
        assertEquals(widgetT2E, result.removeFirst());
        assertEquals(widgetT2F, result.removeFirst());
    }

    @Test
    public void getLeftMostArray () {
        // when
        final Deque<Widget> result = TreedistUtil.getLeftMostArray(widgetT2F);

        // then
        assertEquals(widgetT2A, result.removeFirst());
        assertEquals(widgetT2D, result.removeFirst());
        assertEquals(widgetT2C, result.removeFirst());
        assertEquals(widgetT2F, result.removeFirst());
    }

    @Test
    public void getEarlierNode () {
        // given
        final Deque<Widget> deque = TreedistUtil.getLeftMostArray(widgetT2F);

        // when
        assertEquals(widgetT2C, TreedistUtil.getEarlierNode(widgetT2F, deque));

    }

    @Test
    public void testWidgetUtil_widgetsAreEqual(){
        assertTrue(TreedistUtil.equals(widgetT1A, widgetT2A));
        assertTrue(TreedistUtil.equals(widgetT1B, widgetT2B));
        assertTrue(TreedistUtil.equals(widgetT1C, widgetT2C));
    }

    @Test
    public void testWidgetUtil_widgetsAreUnEquals(){
        assertFalse(TreedistUtil.equals(widgetT1B, widgetT2A));
    }
}
