package org.testar.android.state;

import org.junit.Assert;
import org.junit.Test;
import org.testar.android.tag.AndroidTags;
import org.testar.core.StateManagementTags;

public class AndroidPasswordStateTagTest {

    @Test
    public void readsPasswordAttributeIndependentlyOfClickability() {
        AndroidElement element = new AndroidElement();
        element.password = true;
        element.clickable = false;
        AndroidState state = new AndroidState(null);
        AndroidWidget widget = new AndroidWidget(state, state, element);

        Assert.assertEquals(Boolean.TRUE, widget.get(AndroidTags.AndroidPassword));
        Assert.assertEquals(Boolean.TRUE, widget.get(StateManagementTags.AndroidWidgetPassword));
        Assert.assertEquals(Boolean.FALSE, widget.get(StateManagementTags.AndroidWidgetClickable));
    }
}
