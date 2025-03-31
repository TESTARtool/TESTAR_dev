package org.testar.settings.dialog.tagsvisualization;

import org.junit.Assert;
import org.junit.Test;
import org.testar.monkey.alayer.Tag;
import org.testar.settings.dialog.tagsvisualization.DefaultTagFilter;

import java.util.List;

public class TestDefaultTagFilter {

    @Test
    public void testGetSettingsStringFromTag() {
        Tag<?> tag = DefaultTagFilter.getSet().iterator().next();
        String settingsString = DefaultTagFilter.getSettingsStringFromTag(tag);

        Assert.assertNotNull("Settings string should not be null.", settingsString);
        Assert.assertEquals("Settings string should match tag name.", tag.name(), settingsString);
    }

    @Test
    public void testFindTagByName() {
        Tag<?> originalTag = DefaultTagFilter.getSet().iterator().next();
        String tagName = originalTag.name();
        Tag<?> foundTag = DefaultTagFilter.findTagByName(tagName);

        Assert.assertNotNull("Found tag should not be null.", foundTag);
        Assert.assertEquals("Found tag should match original tag name.", originalTag.name(), foundTag.name());
    }

    @Test
    public void testFindTagByInvalidNameReturnsNull() {
        Tag<?> foundTag = DefaultTagFilter.findTagByName("__non_existent_tag__");
        Assert.assertNull("Non-existent tag name should return null.", foundTag);
    }

}
