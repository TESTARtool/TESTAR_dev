/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.webdriver.state.WdDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

public final class WebdriverLinkDeniedFilterPolicy implements WidgetFilterPolicy {

    private final List<String> webDeniedExtensions;
    private final List<String> webDomainsAllowed;
    private final String webPathsAllowed;

    public WebdriverLinkDeniedFilterPolicy(Settings settings) {
        Assert.notNull(settings);

        this.webDeniedExtensions = settings.get(ConfigTags.WebDeniedExtensions).contains("null")
                ? null
                : settings.get(ConfigTags.WebDeniedExtensions);
        this.webDomainsAllowed = settings.get(ConfigTags.WebDomainsAllowed).contains("null")
                ? null
                : settings.get(ConfigTags.WebDomainsAllowed);
        this.webPathsAllowed = settings.get(ConfigTags.WebPathsAllowed).contains("null")
                ? null
                : settings.get(ConfigTags.WebPathsAllowed);
    }

    @Override
    public boolean allows(Widget widget) {
        Assert.notNull(widget);
        return !isLinkDenied(widget, WdDriver.getCurrentUrl());
    }

    private boolean isLinkDenied(Widget widget, String currentUrl) {
        String linkUrl = widget.get(Tags.ValuePattern, "");
        if (linkUrl == null || linkUrl.isBlank() || linkUrl.startsWith("file:///")) {
            return false;
        }

        if (linkUrl.startsWith("mailto:")) {
            return true;
        }

        String absoluteUrl = resolveRelativeUrl(linkUrl, currentUrl);
        if (isUrlDenied(absoluteUrl)) {
            return true;
        }

        if (webPathsAllowed != null
                && !webPathsAllowed.isEmpty()
                && !absoluteUrl.isEmpty()) {
            Pattern pattern = Pattern.compile(webPathsAllowed);
            if (!pattern.matcher(absoluteUrl).find()) {
                return true;
            }
        }

        return false;
    }

    private boolean isUrlDenied(String currentUrl) {
        if (currentUrl == null || currentUrl.isBlank()) {
            return false;
        }

        if (currentUrl.startsWith("mailto:")) {
            return true;
        }

        if (currentUrl.startsWith("file:///")) {
            return false;
        }

        if (isExtensionDenied(currentUrl)) {
            return true;
        }

        if (webDomainsAllowed != null && !webDomainsAllowed.contains(getDomain(currentUrl))) {
            return true;
        }

        String currentPath = getPath(currentUrl);
        if (webPathsAllowed != null
                && !webPathsAllowed.isEmpty()
                && !currentPath.isEmpty()
                && !currentPath.equals("/")) {
            Pattern pattern = Pattern.compile(webPathsAllowed);
            if (!pattern.matcher(currentPath).find()) {
                return true;
            }
        }

        return false;
    }

    private boolean isExtensionDenied(String currentUrl) {
        if (currentUrl == null || currentUrl.isBlank() || !currentUrl.contains(".")) {
            return false;
        }

        if (webDeniedExtensions == null || webDeniedExtensions.isEmpty()) {
            return false;
        }

        String path = getPath(currentUrl);
        String pathSegment = path == null || path.isBlank() ? currentUrl : path;
        int extensionIndex = pathSegment.lastIndexOf('.');
        if (extensionIndex < 0 || extensionIndex == pathSegment.length() - 1) {
            return false;
        }

        String extension = pathSegment.substring(extensionIndex + 1).replace("/", "").toLowerCase();
        return webDeniedExtensions.contains(extension);
    }

    private String getDomain(String url) {
        if (url == null) {
            return null;
        }

        if (url.startsWith("file://")) {
            return "file://";
        }

        url = url.replace("https://", "").replace("http://", "").replace("file://", "");
        return (url.split("/")[0]).split("\\?")[0];
    }

    private String getPath(String url) {
        try {
            return new URL(url).getPath();
        }
        catch (Exception exception) {
            return "";
        }
    }

    private String resolveRelativeUrl(String relativeUrl, String baseUrl) {
        try {
            URL base = new URL(baseUrl);
            URL absolute = new URL(base, relativeUrl);
            return absolute.toString();
        }
        catch (MalformedURLException exception) {
            return relativeUrl;
        }
    }
}
