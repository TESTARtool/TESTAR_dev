package org.testar.android.policy;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.testar.android.tag.AndroidTags;
import org.testar.stub.WidgetStub;

public class TestConfigurableAndroidTypeableClassPolicy {

    @Test
    public void testNullWidgetIsNotTypeable() {
        ConfigurableAndroidTypeableClassPolicy policy =
                new ConfigurableAndroidTypeableClassPolicy(Collections.singletonList("android.widget.TextView"));

        Assert.assertFalse(policy.isTypeable(null));
    }

    @Test
    public void testWidgetWithConfiguredTypeableClassIsTypeable() {
        ConfigurableAndroidTypeableClassPolicy policy =
                new ConfigurableAndroidTypeableClassPolicy(Collections.singletonList("android.widget.TextView"));
        WidgetStub widget = new WidgetStub();
        widget.set(AndroidTags.AndroidClassName, "android.widget.TextView");

        Assert.assertTrue(policy.isTypeable(widget));
    }

    @Test
    public void testWidgetWithoutConfiguredTypeableClassIsNotTypeable() {
        ConfigurableAndroidTypeableClassPolicy policy =
                new ConfigurableAndroidTypeableClassPolicy(Collections.singletonList("android.widget.TextView"));
        WidgetStub widget = new WidgetStub();
        widget.set(AndroidTags.AndroidClassName, "android.widget.EditText");

        Assert.assertFalse(policy.isTypeable(widget));
    }

    @Test
    public void testConfiguredTypeableClassIsTrimmed() {
        ConfigurableAndroidTypeableClassPolicy policy =
                new ConfigurableAndroidTypeableClassPolicy(Collections.singletonList("  android.widget.TextView  "));
        WidgetStub widget = new WidgetStub();
        widget.set(AndroidTags.AndroidClassName, "android.widget.TextView");

        Assert.assertTrue(policy.isTypeable(widget));
    }

    @Test
    public void testEmptyConfigurationDoesNotMakeWidgetTypeable() {
        ConfigurableAndroidTypeableClassPolicy policy =
                new ConfigurableAndroidTypeableClassPolicy(Collections.emptyList());
        WidgetStub widget = new WidgetStub();
        widget.set(AndroidTags.AndroidClassName, "android.widget.TextView");

        Assert.assertFalse(policy.isTypeable(widget));
    }
}
