package org.testar.webdriver.state;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.alayer.Roles;
import org.testar.core.tag.Tags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.tag.WdTags;

public final class WebdriverSemanticWidgetDescriptorTest {

    private final WebdriverSemanticWidgetDescriptor descriptor = new WebdriverSemanticWidgetDescriptor();

    @Test
    public void shouldIncludeVisibleTextualWidget() {
        StateStub state = new StateStub();
        WidgetStub widget = child(state);
        widget.set(WdTags.WebInnerText, "Submit");

        Assert.assertTrue(descriptor.shouldInclude(widget));
    }

    @Test
    public void shouldNotIncludeNonTextualWidget() {
        StateStub state = new StateStub();
        WidgetStub container = child(state);

        WidgetStub widget = child(container, state);
        widget.set(WdTags.WebInnerText, "Child");

        Assert.assertFalse(descriptor.shouldInclude(container));
    }

    @Test
    public void shouldNotIncludeHiddenWidget() {
        StateStub state = new StateStub();
        WidgetStub widget = child(state);
        widget.set(WdTags.WebInnerText, "Hidden");
        widget.set(WdTags.WebIsDisplayed, false);

        Assert.assertFalse(descriptor.shouldInclude(widget));
    }

    @Test
    public void roleOfPrefersAriaRole() {
        StateStub state = new StateStub();
        WidgetStub widget = child(state);
        widget.set(WdTags.WebAriaRole, "button");
        widget.set(WdTags.WebTagName, "div");
        widget.set(Tags.Role, Roles.Widget);

        Assert.assertEquals("button", descriptor.roleOf(widget));
    }

    @Test
    public void labelOfUsesFirstMeaningfulCandidate() {
        StateStub state = new StateStub();
        WidgetStub widget = child(state);
        widget.set(WdTags.WebAriaLabel, " ");
        widget.set(WdTags.WebPlaceholder, "Search");
        widget.set(WdTags.WebInnerText, "Ignored");

        Assert.assertEquals("Search", descriptor.labelOf(widget));
    }

    @Test
    public void testWidgetTreeCompound() {
        StateStub state = new StateStub();

        WidgetStub grid = child(state);
        WidgetStub gridItem = child(grid, state);
        WidgetStub gridItemButton = child(gridItem, state);
        gridItemButton.set(WdTags.WebTagName, "button");
        gridItemButton.set(WdTags.WebInnerText, "alarm_outlined Time");

        WidgetStub gridItemButtonSpan = child(gridItemButton, state);
        gridItemButtonSpan.set(WdTags.WebTagName, "span");
        gridItemButtonSpan.set(WdTags.WebInnerText, "alarm_outlined Time");

        WidgetStub gridItemButtonSpanDiv = child(gridItemButtonSpan, state);
        gridItemButtonSpanDiv.set(WdTags.WebTagName, "div");
        gridItemButtonSpanDiv.set(WdTags.WebInnerText, "div of alarm_icon");

        WidgetStub gridItemButtonSpanDivIcon = child(gridItemButtonSpanDiv, state);
        gridItemButtonSpanDivIcon.set(WdTags.WebTagName, "span");
        gridItemButtonSpanDivIcon.set(WdTags.WebInnerText, "alarm_icon");

        Assert.assertTrue(descriptor.shouldInclude(gridItemButton));
        Assert.assertFalse(descriptor.shouldInclude(gridItemButtonSpan));
        Assert.assertFalse(descriptor.shouldInclude(gridItemButtonSpanDiv));
    }

    private static WidgetStub child(StateStub state) {
        WidgetStub child = new WidgetStub();
        child.setRoot(state);
        child.setParent(state);
        state.addChild(child);
        return child;
    }

    private static WidgetStub child(WidgetStub parent, StateStub state) {
        WidgetStub child = new WidgetStub();
        child.setRoot(state);
        child.setParent(parent);
        parent.addChild(child);
        return child;
    }
}
