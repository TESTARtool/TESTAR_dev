package org.testar.windows.tag;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testar.core.CodingManager;
import org.testar.core.StateManagementTags;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;

import org.testar.windows.state.UIAState;
import org.testar.windows.state.UIAWidget;

public class TestWindowsStateManagementTag {

    private Tag<?>[] previousAbstractTags;

    @Before
    public void preserveConfiguration() {
        previousAbstractTags = CodingManager.getCustomTagsForAbstractId();
    }

    @After
    public void restoreConfiguration() {
        CodingManager.setCustomTagsForAbstractId(previousAbstractTags);
    }

    @Test
    public void testWindowsMapping() {
        UIAState uiaState = new UIAState(null);
        uiaState.set(UIATags.UIAName, "UIAName");
        uiaState.set(UIATags.UIAControlType, 123L);

        Assert.assertEquals(uiaState.get(StateManagementTags.WidgetTitle), "UIAName");
        Assert.assertEquals(uiaState.get(StateManagementTags.WidgetControlType), 123L);
    }

    @Test
    public void testWindowsCodingIDs() {
        Tag<?>[] abstractTags = new Tag<?>[]{StateManagementTags.WidgetTitle, StateManagementTags.WidgetControlType};
        CodingManager.setCustomTagsForAbstractId(abstractTags);

        UIAState uiaState = new UIAState(null);
        UIAWidget uiaWidget = new UIAWidget(uiaState, uiaState, null);
        uiaWidget.set(UIATags.UIAName, "CustomName");
        uiaWidget.set(UIATags.UIAControlType, 123L);

        // Build the first AbstractID and check the StateManagementTags uses the UIA values
        CodingManager.buildIDs(uiaWidget);
        String originalId = uiaWidget.get(Tags.AbstractID);
        Assert.assertTrue(originalId.startsWith("WA"));
        CodingManager.buildIDs(uiaWidget);
        Assert.assertEquals(originalId, uiaWidget.get(Tags.AbstractID));

        // Change UIAName value to verify the AbstractID changes
        uiaWidget.set(UIATags.UIAName, "CustomNameNEW");
        uiaWidget.set(UIATags.UIAControlType, 123L);
        CodingManager.buildIDs(uiaWidget);
        String changedNameId = uiaWidget.get(Tags.AbstractID);
        Assert.assertNotEquals(originalId, changedNameId);

        // Change UIAControlType value to verify the AbstractID changes
        uiaWidget.set(UIATags.UIAName, "CustomNameNEW");
        uiaWidget.set(UIATags.UIAControlType, 122L);
        CodingManager.buildIDs(uiaWidget);
        Assert.assertNotEquals(originalId, uiaWidget.get(Tags.AbstractID));
        Assert.assertNotEquals(changedNameId, uiaWidget.get(Tags.AbstractID));
    }

}
