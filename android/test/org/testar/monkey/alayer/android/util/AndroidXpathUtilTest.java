package org.testar.monkey.alayer.android.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class AndroidXpathUtilTest {

    @Test
    public void constructAndroidXpath_IndexForDuplicates() {
        StateStub root = new StateStub();
        WidgetStub container = new WidgetStub();
        WidgetStub target = new WidgetStub();
        WidgetStub sibling = new WidgetStub();

        root.addChild(container);
        container.setParent(root);
        root.set(Tags.Desc, "Root");
        root.set(AndroidTags.AndroidNodeIndex, 0);

        container.addChild(sibling);
        container.addChild(target);
        sibling.setParent(container);
        target.setParent(container);
        container.set(Tags.Desc, "LinearLayout");
        container.set(AndroidTags.AndroidNodeIndex, 1);

        sibling.set(Tags.Desc, "Button");
        sibling.set(AndroidTags.AndroidNodeIndex, 1);

        target.set(Tags.Desc, "Button");
        target.set(AndroidTags.AndroidNodeIndex, 2);

        String xpath = AndroidXpathUtil.constructXpath(target);

        assertEquals("/hierarchy/LinearLayout/Button[2]", xpath);
    }

    @Test
    public void constructAndroidXpath_EmptyDesc() {
        StateStub root = new StateStub();
        WidgetStub target = new WidgetStub();
        root.addChild(target);
        target.setParent(root);
        // Keep Desc unset on purpose

        String xpath = AndroidXpathUtil.constructXpath(target);

        assertEquals("/hierarchy/*", xpath);
    }

    @Test
    public void constructAndroidXpath_EmptyNodeIndex() {
        StateStub root = new StateStub();
        WidgetStub target = new WidgetStub();
        root.addChild(target);
        target.setParent(root);
        target.set(Tags.Desc, "Button");
        // Keep AndroidNodeIndex unset on purpose

        String xpath = AndroidXpathUtil.constructXpath(target);

        assertEquals("/hierarchy/Button", xpath);
    }

}
