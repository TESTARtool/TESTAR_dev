package org.testar.config.settings;

import org.junit.Assert;
import org.junit.Test;
import org.testar.config.ConfigTags;
import org.testar.config.SettingsTestSupport;
import org.testar.core.Pair;

import java.util.List;
import java.util.Properties;

public class AndroidAbstractStateAttributesTest {

    @Test
    public void acceptsAndroidIdentificationAttributes() {
        List<String> attributes = List.of("WidgetControlType", "AndroidWidgetResourceId", "AndroidWidgetClickable");
        Settings settings = new Settings(SettingsTestSupport.withValidSutConnector(List.of(
                Pair.from(ConfigTags.AbstractStateAttributes, attributes)
        )), new Properties());

        Assert.assertEquals(attributes.size(), settings.get(ConfigTags.AbstractStateAttributes).size());
        Assert.assertTrue(settings.get(ConfigTags.AbstractStateAttributes).containsAll(attributes));
    }
}
