package org.testar.dialog;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.testar.dialog.tagsvisualization.ConcreteTagFilter;
import org.testar.dialog.tagsvisualization.DefaultTagFilter;
import org.testar.dialog.tagsvisualization.TagFilter;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.core.tag.Tag;
import org.mockito.Mockito;

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
