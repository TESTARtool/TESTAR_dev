package org.testar.webdriver.state;

import org.junit.Test;
import org.testar.core.alayer.Rect;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestWdElementVisibleAt {

    @Test
    public void testVisibleAtUsesCurrentScrollPosition() {
        WdRootElement root = new WdRootElement();
        root.scrollLeft = 20;
        root.scrollTop = 100;
        root.scrollHeight = 1000;

        WdElement element = new WdElement(root, root);
        element.rect = Rect.from(10, 15, 30, 20);

        assertTrue(element.visibleAt(40, 125));
        assertFalse(element.visibleAt(40, 225));
    }
}
