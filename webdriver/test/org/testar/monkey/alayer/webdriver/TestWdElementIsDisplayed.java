package org.testar.monkey.alayer.webdriver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

public class TestWdElementIsDisplayed {

    private WdRootElement rootWdElement;
    private WdState rootWdState;

    private WdElement childWdElement;
    private WdWidget childWdWidget;

    // Before each JUnit test prepare the new objects
    @Before
    public void prepareWebdriverElements() {
        rootWdElement = new WdRootElement();
        rootWdState = new WdState(rootWdElement);

        childWdElement = new WdElement(rootWdElement, rootWdElement);
        childWdWidget = new WdWidget(rootWdState, rootWdState, childWdElement);
    }

    @Test
    public void test_element_is_displayed() {
        childWdElement.display = "block";
        assertTrue(childWdElement.isDisplayed());
        assertTrue(childWdWidget.get(WdTags.WebIsDisplayed, false));
    }

    @Test
    public void test_element_is_not_displayed() {
        childWdElement.display = "none";
        assertFalse(childWdElement.isDisplayed());
        assertFalse(childWdWidget.get(WdTags.WebIsDisplayed, true));
    }

    @Test
    public void test_element_is_not_displayed_due_none_parent() {
        rootWdElement.display = "none";
        childWdElement.display = "block";
        assertFalse(childWdElement.isDisplayed());
        assertFalse(childWdWidget.get(WdTags.WebIsDisplayed, true));
    }

    @Test
    public void test_element_is_not_displayed_due_hidden_parent() {
        rootWdElement.visibility = "hidden";
        childWdElement.display = "block";
        assertFalse(childWdElement.isDisplayed());
        assertFalse(childWdWidget.get(WdTags.WebIsDisplayed, true));
    }
}
