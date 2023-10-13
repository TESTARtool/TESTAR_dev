package org.testar.monkey;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.testar.settings.Settings;

public class TestAbstractStateAttributes {

	@Test
	public void validAbstractStateAttributes() {
		// Both AbstractStateAttributes are valid
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		List<String> configAbstractStateAttributesList = Arrays.asList("WidgetTitle", "WebWidgetId");
		tags.add(Pair.from(ConfigTags.AbstractStateAttributes, configAbstractStateAttributesList));
		System.out.println("INITIAL: validAbstractStateAttributes(): " + configAbstractStateAttributesList);

		// Redirect the console System-output to a variable
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(outContent));	

		Settings settings = new Settings(tags, new Properties());

		// Verify system output is empty
		Assert.isTrue(outContent.toString().isEmpty());

		// Verify the valid AbstractStateAttributes value
		System.out.println("RESULT: Two valid AbstractStateAttributes: " + settings.get(ConfigTags.AbstractStateAttributes));
		Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).size() == 2);
		Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).equals(configAbstractStateAttributesList));
	}

	@Test
	public void invalidAbstractStateAttributes() {
		// One AbstractStateAttributes is invalid and other valid
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		List<String> configAbstractStateAttributesList = Arrays.asList("InvalidWidgetTitlee", "WebWidgetId");
		tags.add(Pair.from(ConfigTags.AbstractStateAttributes, configAbstractStateAttributesList));
		System.out.println("INITIAL: invalidAbstractStateAttributes(): " + configAbstractStateAttributesList);

		// Redirect the console System-output to a variable
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(outContent));	

		Settings settings = new Settings(tags, new Properties());

		// Verify system output prints invalid warning message
		String warningInvalidMsg = "WARNING: Ignoring invalid AbstractStateAttributes configured! [InvalidWidgetTitlee]";
		Assert.isTrue(outContent.toString().contains(warningInvalidMsg));

		// Verify the valid AbstractStateAttributes value
		System.out.println("RESULT: Only one valid AbstractStateAttributes: " + settings.get(ConfigTags.AbstractStateAttributes));
		Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).size() == 1);
		Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).equals(Collections.singletonList("WebWidgetId")));
	}

	@Test
	public void emptyAbstractStateAttributes() {
		// Empty AbstractStateAttributes value
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		List<String> configAbstractStateAttributesList = Arrays.asList("");
		tags.add(Pair.from(ConfigTags.AbstractStateAttributes, configAbstractStateAttributesList));
		System.out.println("INITIAL: emptyAbstractStateAttributes(): " + configAbstractStateAttributesList);

		// Redirect the console System-output to a variable
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(outContent));	

		Settings settings = new Settings(tags, new Properties());

		// Verify system output prints reset message
		String warningResetMsg = "Reseting AbstractStateAttributes test.settings to: [WidgetControlType]";
		Assert.isTrue(outContent.toString().contains(warningResetMsg));

		// Verify the reset AbstractStateAttributes value
		System.out.println("RESULT: Empty then reset AbstractStateAttributes: " + settings.get(ConfigTags.AbstractStateAttributes));
		Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).size() == 1);
		Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).equals(Collections.singletonList("WidgetControlType")));
	}

	@Test
	public void invalidThenEmptyAbstractStateAttributes() {
		// One invalid AbstractStateAttributes value
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		List<String> configAbstractStateAttributesList = Arrays.asList("InvalidWidgetTitlee");
		tags.add(Pair.from(ConfigTags.AbstractStateAttributes, configAbstractStateAttributesList));
		System.out.println("INITIAL: invalidThenEmptyAbstractStateAttributes(): " + configAbstractStateAttributesList);

		// Redirect the console System-output to a variable
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(outContent));	

		Settings settings = new Settings(tags, new Properties());

		// Verify system output prints invalid and reset messages
		String warningInvalidMsg = "WARNING: Ignoring invalid AbstractStateAttributes configured! [InvalidWidgetTitlee]";
		Assert.isTrue(outContent.toString().contains(warningInvalidMsg));
		String warningResetMsg = "Reseting AbstractStateAttributes test.settings to: [WidgetControlType]";
		Assert.isTrue(outContent.toString().contains(warningResetMsg));

		// Verify the reset AbstractStateAttributes value
		System.out.println("RESULT: Invalid then empty reset AbstractStateAttributes: " + settings.get(ConfigTags.AbstractStateAttributes));
		Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).size() == 1);
		Assert.isTrue(settings.get(ConfigTags.AbstractStateAttributes).equals(Collections.singletonList("WidgetControlType")));
	}

}
