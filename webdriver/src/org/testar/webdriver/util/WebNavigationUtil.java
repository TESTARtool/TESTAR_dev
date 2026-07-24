/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.util;

import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.webdriver.SutConnectorParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class WebNavigationUtil {

    private WebNavigationUtil() {
    }

    public static List<String> addInitialAllowedDomains(Settings settings, String currentUrl) {
        Assert.notNull(settings);

        List<String> initialConfiguredDomains = settings.get(ConfigTags.WebDomainsAllowed);
        if (containsNullToken(initialConfiguredDomains)) {
            System.out.println("WEBDRIVER INFO: WebDomainsAllowed contains 'null' token, all web domains are allowed");
            return initialConfiguredDomains;
        }

        List<String> allowedDomains = configuredDomains(settings);
        addDomainIfPresent(allowedDomains, new SutConnectorParser(settings.get(ConfigTags.SUTConnectorValue, "")).getUrl());
        addDomainIfPresent(allowedDomains, currentUrl);
        settings.set(ConfigTags.WebDomainsAllowed, allowedDomains);

        for (String webDomainAllowed : settings.get(ConfigTags.WebDomainsAllowed)) {
            if (!initialConfiguredDomains.contains(webDomainAllowed)) {
                System.out.println(String.format(
                    "WEBDRIVER INFO: Automatically adding %s Web domain to WebDomainsAllowed List",
                    webDomainAllowed
                ));
            }
        }

        return allowedDomains;
    }

    public static boolean isLinkDenied(Settings settings, String linkUrl, String currentUrl) {
        Assert.notNull(settings);

        if (linkUrl == null || linkUrl.isBlank() || linkUrl.startsWith("file:///")) {
            return false;
        }

        if (linkUrl.startsWith("mailto:")) {
            return true;
        }

        String absoluteUrl = resolveRelativeUrl(linkUrl, currentUrl);
        return isUrlDenied(settings, absoluteUrl);
    }

    public static boolean isUrlDenied(Settings settings, String currentUrl) {
        Assert.notNull(settings);

        if (currentUrl == null || currentUrl.isBlank()) {
            return false;
        }

        if (currentUrl.startsWith("mailto:")) {
            return true;
        }

        if (currentUrl.startsWith("file:///")) {
            return false;
        }

        if (isExtensionDenied(settings, currentUrl)) {
            return true;
        }

        List<String> allowedDomains = configuredList(settings.get(ConfigTags.WebDomainsAllowed));
        if (allowedDomains != null && !allowedDomains.contains(getDomain(currentUrl))) {
            return true;
        }

        String allowedPathsRegex = configuredRegex(settings.get(ConfigTags.WebPathsAllowed));
        String currentPath = getPath(currentUrl);
        if (allowedPathsRegex != null
                && !currentPath.isEmpty()
                && !currentPath.equals("/")) {
            Pattern pattern = Pattern.compile(allowedPathsRegex);
            if (!pattern.matcher(currentPath).find()) {
                return true;
            }
        }

        return false;
    }

    public static boolean isExtensionDenied(Settings settings, String currentUrl) {
        Assert.notNull(settings);

        if (currentUrl == null || currentUrl.isBlank() || !currentUrl.contains(".")) {
            return false;
        }

        List<String> deniedExtensions = configuredList(settings.get(ConfigTags.WebDeniedExtensions));
        if (deniedExtensions == null || deniedExtensions.isEmpty()) {
            return false;
        }

        String path = getPath(currentUrl);
        String pathSegment = path == null || path.isBlank() ? currentUrl : path;
        int extensionIndex = pathSegment.lastIndexOf('.');
        if (extensionIndex < 0 || extensionIndex == pathSegment.length() - 1) {
            return false;
        }

        String extension = pathSegment.substring(extensionIndex + 1).replace("/", "").toLowerCase();
        return deniedExtensions.contains(extension);
    }

    public static String getDomain(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }

        if (url.startsWith("file://")) {
            return "file://";
        }

        String normalizedUrl = url.replace("https://", "").replace("http://", "").replace("file://", "");
        return normalizedUrl.split("/")[0].split("\\?")[0];
    }

    public static String getPath(String url) {
        try {
            return new URL(url).getPath();
        } catch (Exception exception) {
            return "";
        }
    }

    public static String resolveRelativeUrl(String relativeUrl, String baseUrl) {
        try {
            URL base = new URL(baseUrl);
            URL absolute = new URL(base, relativeUrl);
            return absolute.toString();
        } catch (MalformedURLException exception) {
            return relativeUrl;
        }
    }

    private static List<String> configuredList(List<String> configuredValues) {
        if (configuredValues == null || containsNullToken(configuredValues)) {
            return null;
        }

        return configuredValues;
    }

    private static String configuredRegex(String configuredValue) {
        if (configuredValue == null || configuredValue.isBlank() || "null".equalsIgnoreCase(configuredValue.trim())) {
            return null;
        }

        return configuredValue;
    }

    private static List<String> configuredDomains(Settings settings) {
        List<String> configuredDomains = settings.get(ConfigTags.WebDomainsAllowed);
        List<String> normalizedDomains = new ArrayList<>();

        if (configuredDomains == null) {
            return normalizedDomains;
        }

        for (String configuredDomain : configuredDomains) {
            if (configuredDomain != null
                    && !configuredDomain.isBlank()
                    && !"null".equalsIgnoreCase(configuredDomain.trim())) {
                normalizedDomains.add(configuredDomain.trim());
            }
        }

        return normalizedDomains;
    }

    private static boolean containsNullToken(List<String> configuredValues) {
        if (configuredValues == null) {
            return false;
        }

        for (String configuredValue : configuredValues) {
            if (configuredValue != null && "null".equalsIgnoreCase(configuredValue.trim())) {
                return true;
            }
        }

        return false;
    }

    private static void addDomainIfPresent(List<String> allowedDomains, String url) {
        String domain = getDomain(url);
        if (domain != null && !domain.isBlank() && !allowedDomains.contains(domain)) {
            allowedDomains.add(domain);
        }
    }
}
