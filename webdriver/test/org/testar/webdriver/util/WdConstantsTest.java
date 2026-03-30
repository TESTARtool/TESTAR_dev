package org.testar.webdriver.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class WdConstantsTest {

    // Default constants values
    private static final List<String> BASE_IGNORED_TAGS = Arrays.asList("script", "style", "iframe");
    private static final List<String> BASE_IGNORED_ATTRS = Arrays.asList("xpath", "innerHTML");

    @Before
    public void setUp() {
        WdConstants.setIgnoredTags(BASE_IGNORED_TAGS);
        WdConstants.setIgnoredAttributes(BASE_IGNORED_ATTRS);
    }

    @Test
    public void testDefaultHiddenTags() {
        assertNotNull(WdConstants.getHiddenTags());
        assertEquals(1, WdConstants.getHiddenTags().size());
        assertTrue(WdConstants.getHiddenTags().contains("canvas"));
    }

    @Test
    public void testDefaultIgnoredTags() {
        assertNotNull(WdConstants.getIgnoredTags());
        assertEquals(3, WdConstants.getIgnoredTags().size());
        assertTrue(WdConstants.getIgnoredTags().contains("script"));
        assertTrue(WdConstants.getIgnoredTags().contains("style"));
        assertTrue(WdConstants.getIgnoredTags().contains("iframe"));
    }

    @Test
    public void testDefaultIgnoredAttributes() {
        assertNotNull(WdConstants.getIgnoredAttributes());
        assertEquals(2, WdConstants.getIgnoredAttributes().size());
        assertTrue(WdConstants.getIgnoredAttributes().contains("xpath"));
        assertTrue(WdConstants.getIgnoredAttributes().contains("innerHTML"));
    }

    @Test
    public void testUpdateWithValidValuesIgnoredTags() {
        WdConstants.setIgnoredTags(Arrays.asList("head", "meta", "link"));

        assertEquals(Arrays.asList("head", "meta", "link"), WdConstants.getIgnoredTags());
    }

    @Test
    public void testUpdateWithValidValuesIgnoredAttributes() {
        WdConstants.setIgnoredAttributes(Arrays.asList("role", "aria-label"));

        assertEquals(Arrays.asList("role", "aria-label"), WdConstants.getIgnoredAttributes());
    }

    @Test
    public void testNullReturnsEmptyIgnoredTags() {
        WdConstants.setIgnoredTags(null);
        assertTrue(WdConstants.getIgnoredTags().isEmpty());
    }

    @Test
    public void testNullReturnsEmptyIgnoredAttributes() {
        WdConstants.setIgnoredAttributes(null);
        assertTrue(WdConstants.getIgnoredAttributes().isEmpty());
    }

    @Test
    public void testEmptyIgnoredTags() {
        WdConstants.setIgnoredTags(Arrays.asList(""));
        assertTrue(WdConstants.getIgnoredTags().isEmpty());
    }

    @Test
    public void testEmptyIgnoredAttributes() {
        WdConstants.setIgnoredAttributes(Arrays.asList(""));
        assertTrue(WdConstants.getIgnoredAttributes().isEmpty());
    }

    @Test
    public void testCleanListIgnoredTags() {
        WdConstants.setIgnoredTags(Arrays.asList("  script  ", "", "style", "style", "  ", null, "svg", "script" ));

        List<String> expected = Arrays.asList("script", "style", "svg");
        assertEquals(expected, WdConstants.getIgnoredTags());
    }

    @Test
    public void testCleanListIgnoredAttributes() {
        WdConstants.setIgnoredAttributes(Arrays.asList(" xpath ", "", "id", " id", "data-test", "  ", null, "xpath"));

        List<String> expected = Arrays.asList("xpath", "id", "data-test");
        assertEquals(expected, WdConstants.getIgnoredAttributes());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableHiddenTags() {
        WdConstants.getHiddenTags().add("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableIgnoredTags() {
        WdConstants.getIgnoredTags().add("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableIgnoredAttributes() {
        WdConstants.getIgnoredAttributes().add("foo");
    }
}
