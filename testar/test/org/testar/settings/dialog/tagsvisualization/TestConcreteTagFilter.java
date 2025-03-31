package org.testar.settings.dialog.tagsvisualization;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.testar.monkey.alayer.Tag;
import org.testar.settings.dialog.tagsvisualization.ConcreteTagFilter;

import java.util.Set;


public class TestConcreteTagFilter {
    @Test
    public void testVisualizeTagDefault() {
        ConcreteTagFilter filter = new ConcreteTagFilter();
        Tag<?> mockTag = Mockito.mock(Tag.class);

        Assert.assertFalse("Default behavior should not visualize unconfigured tags.", filter.visualizeTag(mockTag));
    }

    @Test
    public void testMultipleTagsFilter() {
        ConcreteTagFilter filter = new ConcreteTagFilter();
        Tag<?> mockTag1 = Mockito.mock(Tag.class);
        Tag<?> mockTag2 = Mockito.mock(Tag.class);
        Tag<?> mockTag3 = Mockito.mock(Tag.class);

        filter.setFilter(Set.of(mockTag1, mockTag2));

        Assert.assertTrue("Tag should be visualized after setting filter.", filter.visualizeTag(mockTag1));
        Assert.assertTrue("Tag should be visualized after setting filter.", filter.visualizeTag(mockTag2));
        Assert.assertFalse("Tag should not be visualized after setting filter.", filter.visualizeTag(mockTag3));
    }
}
