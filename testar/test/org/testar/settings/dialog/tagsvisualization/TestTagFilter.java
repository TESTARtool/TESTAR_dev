package org.testar.settings.dialog.tagsvisualization;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;
import org.testar.monkey.alayer.Tag;

public class TestTagFilter {

    @Test
    public void testSingletonBehavior() {
        ITagFilter mockFilter = Mockito.mock(ITagFilter.class);
        Tag<?> mockTag = Mockito.mock(Tag.class);

        TagFilter.setInstance(mockFilter);
        Mockito.when(mockFilter.visualizeTag(mockTag)).thenReturn(true);

        Assert.assertTrue("Should delegate to the current ITagFilter instance",
                TagFilter.getInstance().visualizeTag(mockTag));
    }
}
