package org.testar.config.settings;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.testar.config.ConfigTags;
import org.testar.core.Assert;
import org.testar.core.Pair;

public class TestAbstractStateAttributes {

    @Test
    public void validAbstractStateAttributes() {
        List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
        List<String> configAbstractStateAttributesList = Arrays.asList("WidgetTitle", "WebWidgetId");
        tags.add(Pair.from(ConfigTags.AbstractStateAttributes, configAbstractStateAttributesList));
        System.out.println("INITIAL: validAbstractStateAttributes(): " + configAbstractStateAttributesList);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));

        Settings settings = new Settings(tags, new Properties());

        Assert.isTrue(outContent.toString().isEmpty());

        System.out.println("RESULT: Two valid AbstractStateAttributes: " + settings.get(ConfigTags.AbstractStateAttributes));
        Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).size() == 2);
        Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).equals(configAbstractStateAttributesList));
    }

    @Test
    public void invalidAbstractStateAttributes() {
        List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
        List<String> configAbstractStateAttributesList = Arrays.asList("InvalidWidgetTitlee", "WebWidgetId");
        tags.add(Pair.from(ConfigTags.AbstractStateAttributes, configAbstractStateAttributesList));
        System.out.println("INITIAL: invalidAbstractStateAttributes(): " + configAbstractStateAttributesList);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));

        Settings settings = new Settings(tags, new Properties());

        String warningInvalidMsg = "WARNING: Ignoring invalid AbstractStateAttributes configured! [InvalidWidgetTitlee]";
        Assert.isTrue(outContent.toString().contains(warningInvalidMsg));

        System.out.println("RESULT: Only one valid AbstractStateAttributes: " + settings.get(ConfigTags.AbstractStateAttributes));
        Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).size() == 1);
        Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).equals(Collections.singletonList("WebWidgetId")));
    }

    @Test
    public void emptyAbstractStateAttributes() {
        List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
        List<String> configAbstractStateAttributesList = Arrays.asList("");
        tags.add(Pair.from(ConfigTags.AbstractStateAttributes, configAbstractStateAttributesList));
        System.out.println("INITIAL: emptyAbstractStateAttributes(): " + configAbstractStateAttributesList);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));

        Settings settings = new Settings(tags, new Properties());

        String warningResetMsg = "Reseting AbstractStateAttributes test.settings to: [WidgetControlType]";
        Assert.isTrue(outContent.toString().contains(warningResetMsg));

        System.out.println("RESULT: Empty then reset AbstractStateAttributes: " + settings.get(ConfigTags.AbstractStateAttributes));
        Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).size() == 1);
        Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).equals(Collections.singletonList("WidgetControlType")));
    }

    @Test
    public void invalidThenEmptyAbstractStateAttributes() {
        List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
        List<String> configAbstractStateAttributesList = Arrays.asList("InvalidWidgetTitlee");
        tags.add(Pair.from(ConfigTags.AbstractStateAttributes, configAbstractStateAttributesList));
        System.out.println("INITIAL: invalidThenEmptyAbstractStateAttributes(): " + configAbstractStateAttributesList);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));

        Settings settings = new Settings(tags, new Properties());

        String warningInvalidMsg = "WARNING: Ignoring invalid AbstractStateAttributes configured! [InvalidWidgetTitlee]";
        Assert.isTrue(outContent.toString().contains(warningInvalidMsg));
        String warningResetMsg = "Reseting AbstractStateAttributes test.settings to: [WidgetControlType]";
        Assert.isTrue(outContent.toString().contains(warningResetMsg));

        System.out.println("RESULT: Invalid then empty reset AbstractStateAttributes: " + settings.get(ConfigTags.AbstractStateAttributes));
        Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).size() == 1);
        Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).equals(Collections.singletonList("WidgetControlType")));
    }
}
