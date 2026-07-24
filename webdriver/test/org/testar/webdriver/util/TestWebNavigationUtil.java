package org.testar.webdriver.util;

import org.junit.Assert;
import org.junit.Test;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TestWebNavigationUtil {

    @Test
    public void emptyLinkIsAllowed() {
        Assert.assertFalse(
            "Link is not denied because widget link does not exist",
            WebNavigationUtil.isLinkDenied(
                settingsWithDomains(List.of("para.testar.org")),
                "",
                "https://para.testar.org"
            )
        );
    }

    @Test
    public void fileLinkIsAllowed() {
        Assert.assertFalse(WebNavigationUtil.isLinkDenied(
                settingsWithDomains(List.of("para.testar.org")),
                "file:///C:/Users/table.pdf",
                "https://para.testar.org"
        ));
    }

    @Test
    public void mailLinkIsDenied() {
        Assert.assertTrue(WebNavigationUtil.isLinkDenied(
                settingsWithDomains(List.of("para.testar.org")),
                "mailto:someone@example.com",
                "https://para.testar.org"
        ));
    }

    @Test
    public void deniedExtensionsAreRejected() {
        Settings settings = settingsWithDomains(List.of("para.testar.org"));
        settings.set(ConfigTags.WebDeniedExtensions, new ArrayList<>(List.of("png", "jpg")));

        Assert.assertTrue(WebNavigationUtil.isLinkDenied(
                settings,
                "https://para.testar.org/parabank/image.png",
                "https://para.testar.org"
        ));

        Assert.assertTrue(WebNavigationUtil.isLinkDenied(
                settings,
                "https://para.testar.org/parabank/image.jpg",
                "https://para.testar.org"
        ));

        Assert.assertFalse(WebNavigationUtil.isLinkDenied(
                settings,
                "https://para.testar.org/parabank/index.htm",
                "https://para.testar.org"
        ));
    }

    @Test
    public void nullDomainTokenDisablesDomainRestriction() {
        Settings settings = settingsWithDomains(List.of("null"));

        List<String> allowedDomains = WebNavigationUtil.addInitialAllowedDomains(
                settings,
                "https://para.testar.org/parabank/index.htm"
        );

        Assert.assertEquals(List.of("null"), allowedDomains);
        Assert.assertFalse(WebNavigationUtil.isUrlDenied(settings, "https://para.testar.org/parabank/contact.htm"));
        Assert.assertFalse(WebNavigationUtil.isUrlDenied(settings, "https://other.testar.org/parabank/contact.htm"));
    }

    @Test
    public void emptyDomainsAreInitializedToDetectedSutDomain() {
        Settings settings = settingsWithDomains(List.of());

        List<String> allowedDomains = WebNavigationUtil.addInitialAllowedDomains(
                settings,
                "https://para.testar.org/parabank/index.htm"
        );

        Assert.assertEquals(List.of("para.testar.org"), allowedDomains);
        Assert.assertFalse(WebNavigationUtil.isUrlDenied(settings, "https://para.testar.org/parabank/contact.htm"));
    }

    @Test
    public void configuredDifferentDomainKeepsExistingDomainAndAddsDetectedSutDomain() {
        Settings settings = settingsWithDomains(List.of("www.ou.nl"));

        List<String> allowedDomains = WebNavigationUtil.addInitialAllowedDomains(
                settings,
                "https://para.testar.org/parabank/index.htm"
        );

        Assert.assertEquals(List.of("www.ou.nl", "para.testar.org"), allowedDomains);
        Assert.assertFalse(WebNavigationUtil.isUrlDenied(settings, "https://para.testar.org/parabank/contact.htm"));
    }

    @Test
    public void differentDomainIsDeniedBeforeNormalizationAddsIt() {
        Settings settings = settingsWithDomains(List.of("www.ou.nl"));

        Assert.assertTrue(WebNavigationUtil.isUrlDenied(
                settings,
                "https://para.testar.org/parabank/contact.htm"
        ));
    }

    @Test
    public void relativePathUsesCurrentUrlAsBaseUrl() {
        Settings settings = settingsWithDomains(List.of("para.testar.org"));
        settings.set(ConfigTags.WebPathsAllowed, ".*parabank.*");

        Assert.assertFalse(WebNavigationUtil.isLinkDenied(
                settings,
                "index.htm",
                "https://para.testar.org/parabank/"
        ));
    }

    @Test
    public void absolutePathsAreAllowedWhenRegexMatches() {
        Settings settings = settingsWithDomains(List.of("null"));

        settings.set(ConfigTags.WebPathsAllowed, ".*parabank.*");
        Assert.assertFalse(WebNavigationUtil.isUrlDenied(settings, "https://para.testar.org/parabank/index.htm"));

        settings.set(ConfigTags.WebPathsAllowed, ".*index.htm.*");
        Assert.assertFalse(WebNavigationUtil.isUrlDenied(settings, "https://para.testar.org/parabank/index.htm"));

        settings.set(ConfigTags.WebPathsAllowed, ".*cookies.htm.*|.*index.htm.*|.*overview.*|.*activity.*");
        Assert.assertFalse(WebNavigationUtil.isUrlDenied(settings, "https://para.testar.org/parabank/index.htm"));

        settings.set(ConfigTags.WebPathsAllowed, "parabank/index.htm");
        Assert.assertFalse(WebNavigationUtil.isUrlDenied(settings, "https://para.testar.org/parabank/index.htm"));

        settings.set(ConfigTags.WebPathsAllowed, ".*cookies.htm.*|parabank/index.htm|parabank/activity.htm");
        Assert.assertFalse(WebNavigationUtil.isUrlDenied(settings, "https://para.testar.org/parabank/index.htm"));
    }

    @Test
    public void relativePathsAreAllowedWhenResolvedPathMatchesRegex() {
        Settings settings = settingsWithDomains(List.of("null"));
        String currentUrl = "https://para.testar.org/parabank/";

        settings.set(ConfigTags.WebPathsAllowed, ".*parabank.*");
        Assert.assertFalse(WebNavigationUtil.isLinkDenied(settings, "index.htm", currentUrl));

        settings.set(ConfigTags.WebPathsAllowed, ".*index.*");
        Assert.assertFalse(WebNavigationUtil.isLinkDenied(settings, "index.htm", currentUrl));

        settings.set(ConfigTags.WebPathsAllowed, ".*cookies.htm.*|.*index.htm.*|.*overview.*|.*activity.*");
        Assert.assertFalse(WebNavigationUtil.isLinkDenied(settings, "index.htm", currentUrl));

        settings.set(ConfigTags.WebPathsAllowed, "parabank/index.htm");
        Assert.assertFalse(WebNavigationUtil.isLinkDenied(settings, "index.htm", currentUrl));

        settings.set(ConfigTags.WebPathsAllowed, ".*cookies.htm.*|parabank/index.htm|parabank/activity.htm");
        Assert.assertFalse(WebNavigationUtil.isLinkDenied(settings, "index.htm", currentUrl));
    }

    @Test
    public void absolutePathsAreDeniedWhenRegexDoesNotMatch() {
        Settings settings = settingsWithDomains(List.of("null"));

        settings.set(ConfigTags.WebPathsAllowed, ".*cookies.*");
        Assert.assertTrue(WebNavigationUtil.isUrlDenied(settings, "https://para.testar.org/parabank/index.htm"));

        settings.set(ConfigTags.WebPathsAllowed, ".*cookies.htm.*|.*overview.*|.*activity.*");
        Assert.assertTrue(WebNavigationUtil.isUrlDenied(settings, "https://para.testar.org/parabank/index.htm"));

        settings.set(ConfigTags.WebPathsAllowed, "parabank/activity.htm");
        Assert.assertTrue(WebNavigationUtil.isUrlDenied(settings, "https://para.testar.org/parabank/index.htm"));

        settings.set(ConfigTags.WebPathsAllowed, ".*cookies.htm.*|parabank/overview.htm|parabank/activity.htm");
        Assert.assertTrue(WebNavigationUtil.isUrlDenied(settings, "https://para.testar.org/parabank/index.htm"));
    }

    @Test
    public void urlPathsWithSessionIdAreAllowedWhenRegexMatches() {
        Settings settings = settingsWithDomains(List.of("para.testar.org"));
        String url = "https://para.testar.org/parabank/index.htm;JSESSIONID=ABC";

        settings.set(ConfigTags.WebPathsAllowed, "");
        Assert.assertFalse(WebNavigationUtil.isUrlDenied(settings, url));

        settings.set(ConfigTags.WebPathsAllowed, ".*parabank.*");
        Assert.assertFalse(WebNavigationUtil.isUrlDenied(settings, url));

        settings.set(ConfigTags.WebPathsAllowed, ".*index.htm.*");
        Assert.assertFalse(WebNavigationUtil.isUrlDenied(settings, url));

        settings.set(ConfigTags.WebPathsAllowed, ".*cookies.htm.*|.*index.htm.*|.*overview.*|.*activity.*");
        Assert.assertFalse(WebNavigationUtil.isUrlDenied(settings, url));

        settings.set(ConfigTags.WebPathsAllowed, "parabank/index.htm");
        Assert.assertFalse(WebNavigationUtil.isUrlDenied(settings, url));

        settings.set(ConfigTags.WebPathsAllowed, ".*cookies.htm.*|parabank/index.htm|parabank/activity.htm");
        Assert.assertFalse(WebNavigationUtil.isUrlDenied(settings, url));
    }

    @Test
    public void urlPathsWithSessionIdAreDeniedWhenRegexDoesNotMatch() {
        Settings settings = settingsWithDomains(List.of("para.testar.org"));
        String url = "https://para.testar.org/parabank/index.htm;JSESSIONID=ABC";

        settings.set(ConfigTags.WebPathsAllowed, ".*cookies.*");
        Assert.assertTrue(WebNavigationUtil.isUrlDenied(settings, url));

        settings.set(ConfigTags.WebPathsAllowed, ".*cookies.htm.*|.*overview.*|.*activity.*");
        Assert.assertTrue(WebNavigationUtil.isUrlDenied(settings, url));

        settings.set(ConfigTags.WebPathsAllowed, "parabank/activity.htm");
        Assert.assertTrue(WebNavigationUtil.isUrlDenied(settings, url));

        settings.set(ConfigTags.WebPathsAllowed, ".*cookies.htm.*|parabank/overview.htm|parabank/activity.htm");
        Assert.assertTrue(WebNavigationUtil.isUrlDenied(settings, url));
    }

    @Test
    public void emptyAndNullPathSettingsAllowUrls() {
        Settings emptyPathSettings = settingsWithDomains(List.of("null"));
        emptyPathSettings.set(ConfigTags.WebPathsAllowed, "");

        Assert.assertFalse(WebNavigationUtil.isUrlDenied(
                emptyPathSettings,
                "https://para.testar.org/parabank/contact.htm"
        ));

        Settings nullPathSettings = settingsWithDomains(List.of("null"));
        nullPathSettings.set(ConfigTags.WebPathsAllowed, "null");

        Assert.assertFalse(WebNavigationUtil.isUrlDenied(
                nullPathSettings,
                "https://para.testar.org/parabank/contact.htm"
        ));
    }

    @Test
    public void emptyRootPathIsAllowedIndependentlyOfPathRegex() {
        Settings settings = settingsWithDomains(List.of("para.testar.org"));
        settings.set(ConfigTags.WebPathsAllowed, ".*index.htm.*");

        Assert.assertFalse(WebNavigationUtil.isUrlDenied(
                settings,
                "https://para.testar.org/"
        ));

        Assert.assertFalse(WebNavigationUtil.isUrlDenied(
                settings,
                "https://para.testar.org"
        ));
    }

    @Test
    public void fileUrlIsAllowedIndependentlyOfDomainsAndPaths() {
        Settings settings = settingsWithDomains(List.of("www.ou.nl"));
        settings.set(ConfigTags.WebPathsAllowed, ".*index.*");

        Assert.assertFalse(WebNavigationUtil.isUrlDenied(
                settings,
                "file:///C:/Users/table.pdf"
        ));
    }

    private static Settings settingsWithDomains(List<String> domains) {
        Properties properties = new Properties();
        properties.setProperty(ConfigTags.SUTConnector.name(), Settings.SUT_CONNECTOR_WEBDRIVER);
        properties.setProperty(ConfigTags.SUTConnectorValue.name(), "\"https://para.testar.org\"");
        Settings settings = new Settings(new Properties(properties));
        settings.set(ConfigTags.WebDomainsAllowed, new ArrayList<>(domains));
        settings.set(ConfigTags.WebDeniedExtensions, new ArrayList<>());
        settings.set(ConfigTags.WebPathsAllowed, "");
        return settings;
    }
}
