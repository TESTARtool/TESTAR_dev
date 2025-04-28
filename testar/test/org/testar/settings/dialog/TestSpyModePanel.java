package org.testar.settings.dialog;


import org.junit.Test;
import org.mockito.MockedStatic;
import org.testar.monkey.*;
import org.testar.monkey.alayer.Tag;
import org.testar.settings.Settings;

import org.mockito.Mockito;
import org.testar.settings.dialog.tagsvisualization.ConcreteTagFilter;
import org.testar.settings.dialog.tagsvisualization.DefaultTagFilter;
import org.testar.settings.dialog.tagsvisualization.TagFilter;

import java.util.List;

public class TestSpyModePanel {

    @Test
    public void testExtractInformation() {
        TagFilter.setInstance(new ConcreteTagFilter());
        Settings settingsMock = Mockito.mock(Settings.class);

        SpyModePanel spyModePanel = new SpyModePanel();
        Tag<?> sampleTag = DefaultTagFilter.getSet().iterator().next();
        spyModePanel.includeTags.add(sampleTag);

        spyModePanel.extractInformation(settingsMock);

        Mockito.verify(settingsMock, Mockito.times(1)).set(Mockito.eq(ConfigTags.SpyTagAttributes), Mockito.anyList());
        Assert.isTrue(TagFilter.getInstance().visualizeTag(sampleTag), "TagFilter should visualize the included tag.");
    }

    @Test
    public void testPopulateFrom() throws Exception {
        TagFilter.setInstance(new ConcreteTagFilter());
        Settings settingsMock = Mockito.mock(Settings.class);
        Mockito.when(settingsMock.get(ConfigTags.SpyTagAttributes)).thenReturn(List.of("Title"));

        Tag<?> mockTag = Mockito.mock(Tag.class);
        Mockito.when(mockTag.name()).thenReturn("Title");

        try (MockedStatic<DefaultTagFilter> mockedStatic = Mockito.mockStatic(DefaultTagFilter.class)) {
            mockedStatic.when(() -> DefaultTagFilter.findTagByName("Title")).thenReturn(mockTag);

            SpyModePanel spyModePanel = new SpyModePanel();
            spyModePanel.populateFrom(settingsMock);

            Assert.isTrue(!spyModePanel.includeTags.asSet().isEmpty(),
                    "Included tags should not be empty after populate.");
            Assert.isTrue(spyModePanel.includeTags.asSet().stream().anyMatch(tag -> tag.name().equals("Title")),
                    "Tag 'Title' should be included after populate.");

            Assert.isTrue(TagFilter.getInstance().visualizeTag(mockTag),
                    "TagFilter should visualize 'Title' after populate.");
        }
    }

}
