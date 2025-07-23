package org.testar.monkey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

public class DragTest {

    @Test
    public void testDragConstructor() {
        Drag drag = new Drag(0.0, 0.0, 5.0, 5.0);

        assertEquals(0.0, drag.getFromX(), 0.0001);
        assertEquals(0.0, drag.getFromY(), 0.0001);
        assertEquals(5.0, drag.getToX(), 0.0001);
        assertEquals(5.0, drag.getToY(), 0.0001);
    }

    @Test
    public void testEquals() {
        Drag d1 = new Drag(1.0, 1.0, 2.0, 2.0);
        Drag d2 = new Drag(1.0, 1.0, 2.0, 2.0);

        assertEquals(d1, d2);
    }

    @Test
    public void testNotEquals() {
        Drag d1 = new Drag(1, 1, 2, 2);
        Drag d2 = new Drag(1, 1, 3, 3);

        assertNotEquals(d1, d2);
    }

}
