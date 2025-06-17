package org.testar.protocols;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testar.monkey.ConfigTags;
import org.testar.settings.Settings;

public class TestDeniedURLs extends WebdriverProtocol {

	@Before
	public void settings_setup() {
		settings = new Settings();
	}

	@Test
	public void test_allowed_url_null_values() {
		// When both values are null, all domains and paths must be allowed
		webDomainsAllowed = null;
		webPathsAllowed = null;

		String url = "https://para.testar.org/parabank/index.htm;JSESSIONID=ABC";

		Assert.assertFalse("URL is not denied because both values are null", isUrlDenied(url));
	}

	@Test
	public void test_allowed_url_empty_values() {
		// With empty domains and paths
		webDomainsAllowed = Collections.emptyList();
		webPathsAllowed = "";

		// The ensureWebDomainsAllowed feature adds the default url as allowed domain
		String SUTConnectorValue = " \"C:\\Windows\\chrome\\chrome.exe\" \"https://para.testar.org\" \"1920x900+0+0\" ";
		settings.set(ConfigTags.SUTConnectorValue, SUTConnectorValue);

		ensureWebDomainsAllowed();

		String url = "https://para.testar.org/parabank/index.htm;JSESSIONID=ABC";

		Assert.assertFalse("URL is not denied because both values are empty", isUrlDenied(url));
	}

	@Test
	public void test_allowed_url_ensure_default_domain() {
		// Even if the initial domain is not the default url sut domain
		webDomainsAllowed = Arrays.asList("www.ou.nl");
		webPathsAllowed = "";

		// The ensureWebDomainsAllowed feature adds the default url as allowed domain
		String SUTConnectorValue = " \"C:\\Windows\\chrome\\chrome.exe\" \"https://para.testar.org\" \"1920x900+0+0\" ";
		settings.set(ConfigTags.SUTConnectorValue, SUTConnectorValue);

		ensureWebDomainsAllowed();

		String url = "https://para.testar.org/parabank/index.htm;JSESSIONID=ABC";

		Assert.assertFalse("URL is not denied due to default ensureWebDomainsAllowed", isUrlDenied(url));
	}

	@Test
	public void test_denied_domain_without_ensure() {
		webDomainsAllowed = Arrays.asList("www.ou.nl");
		webPathsAllowed = "";

		String url = "https://para.testar.org/";
		Assert.assertTrue("URL is denied because domain is not valid", isUrlDenied(url));
	}

	@Test
	public void test_allowed_empty_path() {
		webDomainsAllowed = Arrays.asList("para.testar.org");

		String url = "https://para.testar.org/";
		webPathsAllowed = ".*index.htm.*";
		Assert.assertFalse("URL is not denied because path is empty", isUrlDenied(url));

		url = "https://para.testar.org";
		webPathsAllowed = ".*index.htm.*";
		Assert.assertFalse("URL is not denied because path is empty", isUrlDenied(url));
	}

	@Test
	public void test_allowed_paths() {
		webDomainsAllowed = Arrays.asList("para.testar.org");
		String url = "https://para.testar.org/parabank/index.htm;JSESSIONID=ABC";

		webPathsAllowed = "";
		Assert.assertFalse("URL is not denied because path is empty", isUrlDenied(url));

		webPathsAllowed = ".*parabank.*";
		Assert.assertFalse("URL is not denied because path regex matches", isUrlDenied(url));

		webPathsAllowed = ".*index.htm.*";
		Assert.assertFalse("URL is not denied because path regex matches", isUrlDenied(url));

		webPathsAllowed = ".*cookies.htm.*|.*index.htm.*|.*overview.*|.*activity.*";
		Assert.assertFalse("URL is not denied because path regex matches", isUrlDenied(url));

		webPathsAllowed = "parabank/index.htm";
		Assert.assertFalse("URL is not denied because path regex matches", isUrlDenied(url));

		webPathsAllowed = ".*cookies.htm.*|parabank/index.htm|parabank/activity.htm";
		Assert.assertFalse("URL is not denied because path regex matches", isUrlDenied(url));
	}

	@Test
	public void test_denied_path() {
		String url = "https://para.testar.org/parabank/index.htm;JSESSIONID=ABC";
		webDomainsAllowed = Arrays.asList("para.testar.org");

		webPathsAllowed = ".*cookies.*";
		Assert.assertTrue("URL is denied because path regex does not matches", isUrlDenied(url));

		webPathsAllowed = ".*cookies.htm.*|.*overview.*|.*activity.*";
		Assert.assertTrue("URL is denied because path regex does not matches", isUrlDenied(url));

		webPathsAllowed = "parabank/activity.htm";
		Assert.assertTrue("URL is denied because path regex does not matches", isUrlDenied(url));

		webPathsAllowed = ".*cookies.htm.*|parabank/overview.htm|parabank/activity.htm";
		Assert.assertTrue("URL is denied because path regex does not matches", isUrlDenied(url));
	}

	@Test
	public void test_denied_mail() {
		// Even if both values are null, on which all domains and paths are allowed
		webDomainsAllowed = null;
		webPathsAllowed = null;

		// mail url are denied
		String url = "mailto:someone@example.com";

		Assert.assertTrue("URL is denied because url is a mail", isUrlDenied(url));
	}

	@Test
	public void test_allowed_file() {
		// Independently of the domains and paths
		webDomainsAllowed = Arrays.asList("www.ou.nl");
		webPathsAllowed = ".*index.*";

		// file url are always allowed
		String url = "file:///C:/Users/table.pdf";

		Assert.assertFalse("URL is allowed because url is a file", isUrlDenied(url));
	}
}
