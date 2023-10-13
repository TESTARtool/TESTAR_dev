package org.testar.monkey;

import static org.testar.monkey.ConfigTags.ApplicationName;
import static org.testar.monkey.ConfigTags.ApplicationVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.testar.settings.Settings;

public class TestApplicationNameSettings {

	@Test
	public void validApplicationNameValues() {
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ApplicationName, "desktop_app"));
		tags.add(Pair.from(ApplicationVersion, "v1.0.0"));

		Settings settings = new Settings(tags, new Properties());
		Assert.isTrue(settings.get(ConfigTags.ApplicationName, "").equals("desktop_app"));
		Assert.isTrue(settings.get(ConfigTags.ApplicationVersion, "").equals("v1.0.0"));
	}

	@Test
	public void escapedApplicationNameValues() {
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ApplicationName, "desktop_app_.\\./.?.:.*.\".|.>.<."));
		tags.add(Pair.from(ApplicationVersion, "v1.0.0_.\\./.?.:.*.\".|.>.<."));

		Settings settings = new Settings(tags, new Properties());
		Assert.isTrue(settings.get(ConfigTags.ApplicationName, "").equals("desktop_app_._._._._._._._._._._"));
		Assert.isTrue(settings.get(ConfigTags.ApplicationVersion, "").equals("v1.0.0_._._._._._._._._._._"));
	}

}
