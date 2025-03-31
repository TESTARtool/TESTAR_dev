package org.testar;


import org.junit.Test;
import org.junit.Assert;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Widget;
import org.testar.settings.dialog.tagsvisualization.ITagFilter;
import org.testar.settings.dialog.tagsvisualization.TagFilter;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestSUTVisualization {

    @Test
    public void testCalculateNumberOfTagsToShow() throws Exception {
        Widget widgetMock = mock(Widget.class);
        Tag<?> tag1 = mock(Tag.class);
        Tag<?> tag2 = mock(Tag.class);

        Set<Tag<?>> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2);

        when(widgetMock.tags()).thenReturn(tags);

        ITagFilter filterMock = mock(ITagFilter.class);
        TagFilter.setInstance(filterMock);

        when(filterMock.visualizeTag(tag1)).thenReturn(true);
        when(filterMock.visualizeTag(tag2)).thenReturn(false);

        int result = SutVisualization.calculateNumberOfTagsToShow(widgetMock);

        Assert.assertEquals("Only one tag should be visualized based on the mock setup.", 1, result);
    }

}
