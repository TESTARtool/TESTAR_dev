package org.testar.webdriver.state;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testar.core.alayer.Rect;
import org.testar.webdriver.alayer.WdCanvasDimensions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestWdElementOptionVisibility {

    @Test
    public void collapsedSelectUsesCurrentSelectionAndParentVisibility() {
        try (MockedStatic<WdCanvasDimensions> dimensions = canvasDimensions()) {
            WdRootElement root = new WdRootElement();
            WdElement select = element(root, root, "select", Rect.from(10, 10, 100, 30));
            WdElement option = element(root, select, "option", Rect.from(0, 0, 0, 0));
            option.outerHTML = "<option value='a'>A</option>";

            assertFalse(option.isFullVisibleAtCanvasBrowser());
            option.selected = true;
            assertTrue(option.isFullVisibleAtCanvasBrowser());
            select.rect = Rect.from(1100, 10, 100, 30);
            assertFalse(option.isFullVisibleAtCanvasBrowser());
        }
    }

    @Test
    public void multiSelectAllowsVisibleUnselectedOptions() {
        try (MockedStatic<WdCanvasDimensions> dimensions = canvasDimensions()) {
            WdRootElement root = new WdRootElement();
            WdElement select = element(root, root, "select", Rect.from(10, 10, 100, 80));
            select.multiple = true;
            WdElement option = element(root, select, "option", Rect.from(15, 15, 90, 20));

            assertTrue(option.isFullVisibleAtCanvasBrowser());
            option.rect = Rect.from(1100, 15, 90, 20);
            assertFalse(option.isFullVisibleAtCanvasBrowser());
        }
    }

    @Test
    public void customListboxOptionUsesItsOwnGeometry() {
        try (MockedStatic<WdCanvasDimensions> dimensions = canvasDimensions()) {
            WdRootElement root = new WdRootElement();
            WdElement listbox = element(root, root, "div", Rect.from(10, 10, 100, 80));
            WdElement option = element(root, listbox, "option", Rect.from(15, 15, 90, 20));

            assertTrue(option.isFullVisibleAtCanvasBrowser());
            option.rect = Rect.from(1100, 15, 90, 20);
            assertFalse(option.isFullVisibleAtCanvasBrowser());
        }
    }

    private static WdElement element(WdRootElement root, WdElement parent, String tagName, Rect rect) {
        WdElement element = new WdElement(root, parent);
        element.tagName = tagName;
        element.rect = rect;
        return element;
    }

    private static MockedStatic<WdCanvasDimensions> canvasDimensions() {
        MockedStatic<WdCanvasDimensions> dimensions = Mockito.mockStatic(WdCanvasDimensions.class);
        dimensions.when(WdCanvasDimensions::getCanvasWidth).thenReturn(1000);
        dimensions.when(WdCanvasDimensions::getInnerHeight).thenReturn(800);
        return dimensions;
    }
}
