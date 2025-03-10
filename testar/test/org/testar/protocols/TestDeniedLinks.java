package org.testar.protocols;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.settings.Settings;
import org.testar.stub.WidgetStub;

public class TestDeniedLinks extends WebdriverProtocol {

	private WidgetStub widget;

	@Before
	public void settings_setup() {
		settings = new Settings();
		widget = new WidgetStub();
	}

	@Test
	public void test_allowed_empty_link() {
		Assert.assertFalse("Link is not denied because widget link does not exist", isLinkDenied(widget));
	}

	@Test
	public void test_allowed_file_link() {
		widget.set(Tags.ValuePattern, "file:///C:/Users/table.pdf");
		Assert.assertFalse("Link is not denied because is a file link", isLinkDenied(widget));
	}

	@Test
	public void test_denied_mail_link() {
		widget.set(Tags.ValuePattern, "mailto:someone@example.com");
		Assert.assertTrue("Link is denied because is a mail link", isLinkDenied(widget));
	}

	@Test
	public void test_allowed_extensions() {
		deniedExtensions = Arrays.asList(".png", ".jpg");

		widget.set(Tags.ValuePattern, "https://para.testar.org/parabank");
		Assert.assertFalse("Link is allowed because extension is not denied", isLinkDenied(widget));

		widget.set(Tags.ValuePattern, "https://para.testar.org/parabank/index.pdf");
		Assert.assertFalse("Link is allowed because extension is not denied", isLinkDenied(widget));

		widget.set(Tags.ValuePattern, "https://para.testar.org/parabank/index.htm");
		Assert.assertFalse("Link is allowed because extension is not denied", isLinkDenied(widget));
	}

	@Test
	public void test_denied_extensions() {
		deniedExtensions = Arrays.asList("png", "jpg");

		widget.set(Tags.ValuePattern, "https://para.testar.org/parabank/image.png");
		Assert.assertTrue("Link is denied because png extension is denied", isLinkDenied(widget));

		widget.set(Tags.ValuePattern, "https://para.testar.org/parabank/image.jpg");
		Assert.assertTrue("Link is denied because jpg extension is denied", isLinkDenied(widget));
	}

	@Test
	public void test_allowed_null_domain() {
		webDomainsAllowed = null;

		widget.set(Tags.ValuePattern, "https://para.testar.org/parabank/contact.htm");
		Assert.assertFalse("Link is allowed because allowed domain is null", isLinkDenied(widget));
	}

	@Test
	public void test_denied_empty_domain_without_ensure() {
		webDomainsAllowed = Collections.emptyList();

		widget.set(Tags.ValuePattern, "https://para.testar.org/parabank/contact.htm");
		Assert.assertTrue("Link is denied due to empty domain list", isLinkDenied(widget));
	}

	@Test
	public void test_denied_different_domain_without_ensure() {
		webDomainsAllowed = Arrays.asList("www.ou.nl");

		widget.set(Tags.ValuePattern, "https://para.testar.org/parabank/contact.htm");
		Assert.assertTrue("Link is denied due to different allowed domain list", isLinkDenied(widget));
	}

	@Test
	public void test_allowed_empty_ensure_domain() {
		webDomainsAllowed = Collections.emptyList();

		// The ensureWebDomainsAllowed feature adds the default url as allowed domain
		String SUTConnectorValue = " \"C:\\Windows\\chromedriver.exe\" \"https://para.testar.org\" ";
		settings.set(ConfigTags.SUTConnectorValue, SUTConnectorValue);

		ensureWebDomainsAllowed();

		widget.set(Tags.ValuePattern, "https://para.testar.org/parabank/contact.htm");
		Assert.assertFalse("Link is allowed due to default ensureWebDomainsAllowed", isLinkDenied(widget));
	}

	@Test
	public void test_allowed_different_ensure_domain() {
		webDomainsAllowed = Arrays.asList("www.ou.nl");

		// The ensureWebDomainsAllowed feature adds the default url as allowed domain
		String SUTConnectorValue = " \"C:\\Windows\\chromedriver.exe\" \"https://para.testar.org\" ";
		settings.set(ConfigTags.SUTConnectorValue, SUTConnectorValue);

		ensureWebDomainsAllowed();

		widget.set(Tags.ValuePattern, "https://para.testar.org/parabank/contact.htm");
		Assert.assertFalse("Link is allowed due to default ensureWebDomainsAllowed", isLinkDenied(widget));
	}

	@Test
	public void test_allowed_empty_path() {
		webDomainsAllowed = null;
		webPathsAllowed = "";

		widget.set(Tags.ValuePattern, "https://para.testar.org/parabank/contact.htm");
		Assert.assertFalse("Link is allowed because paths is empty", isLinkDenied(widget));
	}

	@Test
	public void test_allowed_null_values() {
		webDomainsAllowed = null;
		webPathsAllowed = null;

		widget.set(Tags.ValuePattern, "https://para.testar.org/parabank/contact.htm");
		Assert.assertFalse("Link is allowed because paths is null", isLinkDenied(widget));
	}

	@Test
	public void test_allowed_absolute_paths() {
		webDomainsAllowed = null;

		widget.set(Tags.ValuePattern, "https://para.testar.org/parabank/index.htm");

		webPathsAllowed = ".*parabank.*";
		Assert.assertFalse("Link is allowed because path regex matches", isLinkDenied(widget));

		webPathsAllowed = ".*index.htm.*";
		Assert.assertFalse("Link is allowed because path regex matches", isLinkDenied(widget));

		webPathsAllowed = ".*cookies.htm.*|.*index.htm.*|.*overview.*|.*activity.*";
		Assert.assertFalse("Link is allowed because path regex matches", isLinkDenied(widget));

		webPathsAllowed = "parabank/index.htm";
		Assert.assertFalse("Link is allowed because path regex matches", isLinkDenied(widget));

		webPathsAllowed = ".*cookies.htm.*|parabank/index.htm|parabank/activity.htm";
		Assert.assertFalse("Link is allowed because path regex matches", isLinkDenied(widget));
	}

	@Test
	public void test_allowed_relative_paths() {
		webDomainsAllowed = null;

		// Create a mocked static version of WdDriver
		try (MockedStatic<WdDriver> mockedStatic = Mockito.mockStatic(WdDriver.class)) {
			// Mock the static method getCurrentUrl
			mockedStatic.when(WdDriver::getCurrentUrl).thenReturn("https://para.testar.org/parabank/");

			widget.set(Tags.ValuePattern, "index.htm");

			webPathsAllowed = ".*parabank.*";
			Assert.assertFalse("Link is allowed because path regex matches", isLinkDenied(widget));

			webPathsAllowed = ".*index.*";
			Assert.assertFalse("Link is allowed because path regex matches", isLinkDenied(widget));

			webPathsAllowed = ".*cookies.htm.*|.*index.htm.*|.*overview.*|.*activity.*";
			Assert.assertFalse("Link is allowed because path regex matches", isLinkDenied(widget));

			webPathsAllowed = "parabank/index.htm";
			Assert.assertFalse("Link is allowed because path regex matches", isLinkDenied(widget));

			webPathsAllowed = ".*cookies.htm.*|parabank/index.htm|parabank/activity.htm";
			Assert.assertFalse("Link is allowed because path regex matches", isLinkDenied(widget));
		}

	}

	@Test
	public void test_denied_paths() {
		webDomainsAllowed = null;

		widget.set(Tags.ValuePattern, "https://para.testar.org/parabank/index.htm");

		webPathsAllowed = ".*cookies.*";
		Assert.assertTrue("Link is allowed because path regex does not matches", isLinkDenied(widget));

		webPathsAllowed = ".*cookies.htm.*|.*overview.*|.*activity.*";
		Assert.assertTrue("Link is allowed because path regex does not matches", isLinkDenied(widget));

		webPathsAllowed = "parabank/activity.htm";
		Assert.assertTrue("Link is allowed because path regex does not matches", isLinkDenied(widget));

		webPathsAllowed = ".*cookies.htm.*|parabank/overview.htm|parabank/activity.htm";
		Assert.assertTrue("Link is allowed because path regex does not matches", isLinkDenied(widget));
	}
}
