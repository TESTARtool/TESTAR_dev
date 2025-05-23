package org.testar.settings.dialog.tagsvisualization;

import org.junit.Assert;
import org.junit.Test;
import org.testar.monkey.alayer.Tag;

public class TestDefaultTagFilter {

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
