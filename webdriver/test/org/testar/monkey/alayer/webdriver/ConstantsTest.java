package org.testar.monkey.alayer.webdriver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ConstantsTest {

    // Default constants values
    private static final List<String> BASE_IGNORED_TAGS = Arrays.asList("script", "style", "iframe");
    private static final List<String> BASE_IGNORED_ATTRS = Arrays.asList("xpath", "innerHTML");

    @Before
    public void setUp() {
        Constants.setIgnoredTags(BASE_IGNORED_TAGS);
        Constants.setIgnoredAttributes(BASE_IGNORED_ATTRS);
    }

    @Test
    public void testDefaultHiddenTags() {
        assertNotNull(Constants.getHiddenTags());
        assertEquals(1, Constants.getHiddenTags().size());
        assertTrue(Constants.getHiddenTags().contains("canvas"));
    }

    @Test
    public void testDefaultIgnoredTags() {
        assertNotNull(Constants.getIgnoredTags());
        assertEquals(3, Constants.getIgnoredTags().size());
        assertTrue(Constants.getIgnoredTags().contains("script"));
        assertTrue(Constants.getIgnoredTags().contains("style"));
        assertTrue(Constants.getIgnoredTags().contains("iframe"));
    }

    @Test
    public void testDefaultIgnoredAttributes() {
        assertNotNull(Constants.getIgnoredAttributes());
        assertEquals(2, Constants.getIgnoredAttributes().size());
        assertTrue(Constants.getIgnoredAttributes().contains("xpath"));
        assertTrue(Constants.getIgnoredAttributes().contains("innerHTML"));
    }

    @Test
    public void testUpdateWithValidValuesIgnoredTags() {
        Constants.setIgnoredTags(Arrays.asList("head", "meta", "link"));

        assertEquals(Arrays.asList("head", "meta", "link"), Constants.getIgnoredTags());
    }

    @Test
    public void testUpdateWithValidValuesIgnoredAttributes() {
        Constants.setIgnoredAttributes(Arrays.asList("role", "aria-label"));

        assertEquals(Arrays.asList("role", "aria-label"), Constants.getIgnoredAttributes());
    }

    @Test
    public void testNullReturnsEmptyIgnoredTags() {
        Constants.setIgnoredTags(null);
        assertTrue(Constants.getIgnoredTags().isEmpty());
    }

    @Test
    public void testNullReturnsEmptyIgnoredAttributes() {
        Constants.setIgnoredAttributes(null);
        assertTrue(Constants.getIgnoredAttributes().isEmpty());
    }

    @Test
    public void testEmptyIgnoredTags() {
        Constants.setIgnoredTags(Arrays.asList(""));
        assertTrue(Constants.getIgnoredTags().isEmpty());
    }

    @Test
    public void testEmptyIgnoredAttributes() {
        Constants.setIgnoredAttributes(Arrays.asList(""));
        assertTrue(Constants.getIgnoredAttributes().isEmpty());
    }

    @Test
    public void testCleanListIgnoredTags() {
        Constants.setIgnoredTags(Arrays.asList("  script  ", "", "style", "style", "  ", null, "svg", "script" ));

        List<String> expected = Arrays.asList("script", "style", "svg");
        assertEquals(expected, Constants.getIgnoredTags());
    }

    @Test
    public void testCleanListIgnoredAttributes() {
        Constants.setIgnoredAttributes(Arrays.asList(" xpath ", "", "id", " id", "data-test", "  ", null, "xpath"));

        List<String> expected = Arrays.asList("xpath", "id", "data-test");
        assertEquals(expected, Constants.getIgnoredAttributes());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableHiddenTags() {
        Constants.getHiddenTags().add("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableIgnoredTags() {
        Constants.getIgnoredTags().add("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableIgnoredAttributes() {
        Constants.getIgnoredAttributes().add("foo");
    }
}
