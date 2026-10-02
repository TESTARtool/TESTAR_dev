package org.testar.android.tag;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.CodingManager;
import org.testar.core.StateManagementTags;
import org.testar.android.state.AndroidState;
import org.testar.android.state.AndroidWidget;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.state.StateIdentity;

public class TestAndroidStateManagementTag {

    @Test
    public void testAndroidMapping() {
        AndroidState androidState = new AndroidState(null);
        androidState.set(AndroidTags.AndroidText, "mobileText");
        androidState.set(AndroidTags.AndroidAccessibilityId, "mobileAccessibilityId");

        Assert.assertEquals(androidState.get(StateManagementTags.WidgetTitle), "mobileText");
        Assert.assertEquals(androidState.get(StateManagementTags.WidgetAutomationId), "mobileAccessibilityId");
    }

    @Test
    public void testAndroidSpecificAbstractStateAttributes() {
        Assert.assertEquals(StateManagementTags.AndroidWidgetResourceId,
                StateManagementTags.getTagFromSettingsString("AndroidWidgetResourceId"));
        Assert.assertEquals(StateManagementTags.AndroidWidgetClickable,
                StateManagementTags.getTagFromSettingsString("AndroidWidgetClickable"));
        Assert.assertEquals(StateManagementTags.Group.Android,
                StateManagementTags.getTagGroup(StateManagementTags.AndroidWidgetResourceId));

        AndroidState androidState = new AndroidState(null);
        AndroidWidget androidWidget = new AndroidWidget(androidState, androidState, null);
        androidWidget.set(AndroidTags.AndroidResourceId, "login-button");
        androidWidget.set(AndroidTags.AndroidClickable, true);

        Assert.assertEquals("login-button", androidWidget.get(StateManagementTags.AndroidWidgetResourceId));
        Assert.assertEquals(Boolean.TRUE, androidWidget.get(StateManagementTags.AndroidWidgetClickable));

        Tag<?>[] previousTags = CodingManager.getCustomTagsForAbstractId();
        try {
            CodingManager.setCustomTagsForAbstractId(new Tag<?>[] { StateManagementTags.AndroidWidgetResourceId });
            CodingManager.buildIDs(androidWidget);
            String firstId = androidWidget.get(Tags.AbstractID);

            androidWidget.set(AndroidTags.AndroidResourceId, "logout-button");
            CodingManager.buildIDs(androidWidget);
            Assert.assertNotEquals(firstId, androidWidget.get(Tags.AbstractID));
        } finally {
            CodingManager.setCustomTagsForAbstractId(previousTags);
        }
    }

    @Test
    public void testAndroidCodingIDs() {
        Tag<?>[] previousTags = CodingManager.getCustomTagsForAbstractId();
        try {
            Tag<?>[] abstractTags = new Tag<?>[]{StateManagementTags.WidgetTitle, StateManagementTags.WidgetAutomationId};
            CodingManager.setCustomTagsForAbstractId(abstractTags);

            AndroidState androidState = new AndroidState(null);
            AndroidWidget androidWidget = new AndroidWidget(androidState, androidState, null);
            androidWidget.set(AndroidTags.AndroidText, "mobileText");
            androidWidget.set(AndroidTags.AndroidAccessibilityId, "mobileAccessibilityId");

            // Build the first AbstractID and check the StateManagementTags uses the Android values
            CodingManager.buildIDs(androidWidget);
            String originalId = androidWidget.get(Tags.AbstractID);
            Assert.assertTrue(originalId.startsWith("WA"));
            CodingManager.buildIDs(androidWidget);
            Assert.assertEquals(originalId, androidWidget.get(Tags.AbstractID));

            // Change AndroidText value to verify the AbstractID changes
            androidWidget.set(AndroidTags.AndroidText, "mobileTextNEW");
            androidWidget.set(AndroidTags.AndroidAccessibilityId, "mobileAccessibilityId");
            CodingManager.buildIDs(androidWidget);
            String changedTextId = androidWidget.get(Tags.AbstractID);
            Assert.assertNotEquals(originalId, changedTextId);

            // Change AndroidAccessibilityId value to verify the AbstractID changes
            androidWidget.set(AndroidTags.AndroidText, "mobileTextNEW");
            androidWidget.set(AndroidTags.AndroidAccessibilityId, "mobileAccessibility");
            CodingManager.buildIDs(androidWidget);
            Assert.assertNotEquals(originalId, androidWidget.get(Tags.AbstractID));
            Assert.assertNotEquals(changedTextId, androidWidget.get(Tags.AbstractID));
        } finally {
            CodingManager.setCustomTagsForAbstractId(previousTags);
        }
    }

    @Test
    public void rootActivityParticipatesWhenSelectedForAbstraction() {
        StateIdentity identity = StateIdentity.fromAttributes(List.of("WidgetControlType", "AndroidWidgetActivity"));
        AndroidState state = new AndroidState(null);
        AndroidWidget widget = new AndroidWidget(state, state, null);
        widget.set(AndroidTags.AndroidClassName, "android.widget.Button");
        state.set(AndroidTags.AndroidActivity, "LoginActivity");
        identity.buildIDs(state);
        String abstractId = state.get(Tags.AbstractID);
        String concreteId = state.get(Tags.ConcreteID);
        String widgetId = widget.get(Tags.AbstractID);

        state.set(AndroidTags.AndroidActivity, "AccountActivity");
        identity.buildIDs(state);

        Assert.assertNotEquals(abstractId, state.get(Tags.AbstractID));
        Assert.assertNotEquals(concreteId, state.get(Tags.ConcreteID));
        Assert.assertEquals(widgetId, widget.get(Tags.AbstractID));
    }

}
