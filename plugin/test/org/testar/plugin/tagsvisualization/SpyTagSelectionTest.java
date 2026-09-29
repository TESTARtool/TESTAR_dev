package org.testar.plugin.tagsvisualization;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.webdriver.tag.WdTags;

public class SpyTagSelectionTest {

    @Test
    public void includesOnlyConfiguredWidgetTags() {
        SpyTagSelection selection = new SpyTagSelection(List.of("Title", "Role"));

        Assert.assertTrue(selection.includes(Tags.Title));
        Assert.assertTrue(selection.includes(Tags.Role));
        Assert.assertFalse(selection.includes(Tags.Path));
    }

    @Test
    public void readsSelectedCoreAndWebDriverPropertiesFromWidget() {
        SpyTagSelection selection = new SpyTagSelection(List.of("Title", "WebTagName"));
        Widget widget = Mockito.mock(Widget.class);
        Mockito.when(widget.get(Tags.Title, null)).thenReturn("Submit");
        Mockito.when(widget.get(WdTags.WebTagName, null)).thenReturn("button");

        Map<String, String> properties = selection.selectWidgetProperties(widget);

        Assert.assertEquals(Map.of("Title", "Submit", "WebTagName", "button"), properties);
        Assert.assertEquals(List.of("Title", "WebTagName"), List.copyOf(properties.keySet()));
        Assert.assertFalse(properties.containsKey("Path"));
    }

    @Test
    public void emptySelectionHidesAllConfigurableAttributes() {
        SpyTagSelection selection = new SpyTagSelection(List.of());
        Widget widget = Mockito.mock(Widget.class);

        Assert.assertFalse(selection.includes(Tags.Title));
        Assert.assertTrue(selection.selectWidgetProperties(widget).isEmpty());
        Mockito.verifyNoInteractions(widget);
    }

    @Test
    public void readsConfiguredCustomTagWhenPresentOnWidget() {
        Tag<String> customTag = Tag.from("WorkspaceSpyDetail", String.class);
        SpyTagSelection selection = new SpyTagSelection(List.of("WorkspaceSpyDetail"));
        Widget widget = Mockito.mock(Widget.class);
        Mockito.when(widget.tags()).thenReturn(List.of(customTag));
        Mockito.when(widget.get(customTag, null)).thenReturn("custom value");

        Assert.assertEquals(
                Map.of("WorkspaceSpyDetail", "custom value"),
                selection.selectWidgetProperties(widget)
        );
    }
}
