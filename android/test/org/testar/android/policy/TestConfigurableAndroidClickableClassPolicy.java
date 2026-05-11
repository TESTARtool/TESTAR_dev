package org.testar.android.policy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.testar.android.tag.AndroidTags;
import org.testar.stub.WidgetStub;

public class TestConfigurableAndroidClickableClassPolicy {

    @Test
    public void testNullWidgetIsNotClickable() {
        ConfigurableAndroidClickableClassPolicy policy =
                new ConfigurableAndroidClickableClassPolicy(Collections.singletonList("android.widget.TextView"));

        Assert.assertFalse(policy.isClickable(null));
    }

    @Test
    public void testWidgetWithConfiguredClickableClassIsClickable() {
        ConfigurableAndroidClickableClassPolicy policy =
                new ConfigurableAndroidClickableClassPolicy(Collections.singletonList("android.widget.TextView"));
        WidgetStub widget = new WidgetStub();
        widget.set(AndroidTags.AndroidClassName, "android.widget.TextView");

        Assert.assertTrue(policy.isClickable(widget));
    }

    @Test
    public void testWidgetWithoutConfiguredClickableClassIsNotClickable() {
        ConfigurableAndroidClickableClassPolicy policy =
                new ConfigurableAndroidClickableClassPolicy(Collections.singletonList("android.widget.TextView"));
        WidgetStub widget = new WidgetStub();
        widget.set(AndroidTags.AndroidClassName, "android.widget.Button");

        Assert.assertFalse(policy.isClickable(widget));
    }

    @Test
    public void testConfiguredClickableClassIsTrimmed() {
        ConfigurableAndroidClickableClassPolicy policy =
                new ConfigurableAndroidClickableClassPolicy(Collections.singletonList("  android.widget.TextView  "));
        WidgetStub widget = new WidgetStub();
        widget.set(AndroidTags.AndroidClassName, "android.widget.TextView");

        Assert.assertTrue(policy.isClickable(widget));
    }

    @Test
    public void testConfiguredClickableClassWhenMultiple() {
        List<String> multipleClickable = Arrays.asList(
            "android.widget.Image",
            "android.widget.TextView",
            "android.widget.View");
        ConfigurableAndroidClickableClassPolicy policy = new ConfigurableAndroidClickableClassPolicy(multipleClickable);
        WidgetStub widget = new WidgetStub();
        widget.set(AndroidTags.AndroidClassName, "android.widget.TextView");

        Assert.assertTrue(policy.isClickable(widget));
    }

    @Test
    public void testEmptyConfigurationDoesNotMakeWidgetClickable() {
        ConfigurableAndroidClickableClassPolicy policy =
                new ConfigurableAndroidClickableClassPolicy(Collections.emptyList());
        WidgetStub widget = new WidgetStub();
        widget.set(AndroidTags.AndroidClassName, "android.widget.TextView");

        Assert.assertFalse(policy.isClickable(widget));
    }
}
