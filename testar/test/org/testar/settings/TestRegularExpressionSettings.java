package org.testar.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.testar.monkey.*;
import org.testar.monkey.alayer.Tag;

public class TestRegularExpressionSettings {

	private List<Tag<String>> regularExpressionTags = Arrays.asList(
			ConfigTags.ProcessesToKillDuringTest,
			ConfigTags.ClickFilter,
			ConfigTags.SuspiciousTags,
			ConfigTags.SuspiciousProcessOutput,
			ConfigTags.ProcessLogs,
			ConfigTags.LogOracleRegex,
			ConfigTags.WebConsoleErrorPattern,
			ConfigTags.WebConsoleWarningPattern
			);

	@Test
	public void validRegularExpressionSetting() {
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		for(Tag<String> tag : regularExpressionTags) {
			tags.add(Pair.from(tag, ".*[aA].*|.*bc.*"));
		}

		// When initializing the regex settings with correct values
		Settings settings = new Settings(tags, new Properties());

		// All ConfigTags settings were set with the desired correct value
		for(Tag<String> tag : regularExpressionTags) {
			System.out.println(tag + " valid regex: " + settings.get(tag, ""));
			Assert.isTrue(settings.get(tag, "").equals(".*[aA].*|.*bc.*"));
		}
	}

	@Test
	public void invalidRegularExpressionSetting() {
		for(Tag<String> tag : regularExpressionTags) {
			List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
			tags.add(Pair.from(tag, ".**[aA].*|.*bc.*"));

			String exceptionMessage = ""; 
			try {
				// When the settings regular expression is not valid, 
				// TESTAR throws an IllegalStateException
				new Settings(tags, new Properties());
			} catch (IllegalStateException e) {
				exceptionMessage = e.getMessage();
			}

			// Because the regex is NOT correct, we should get an exception message
			System.out.println(exceptionMessage);
			Assert.hasText(exceptionMessage);
			Assert.isTrue(exceptionMessage.contains(tag + " setting is not a valid regular expression"));
		}
	}

}
