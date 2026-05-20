/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.action.derivation;

import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.engine.action.derivation.ActionDeriver;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.webdriver.action.WdCloseTabAction;
import org.testar.webdriver.action.WdHistoryBackAction;
import org.testar.webdriver.state.WdDriver;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public final class WdDeniedUrlForcedActionDeriver implements ActionDeriver {

    private final List<String> webDeniedExtensions;
    private final List<String> webDomainsAllowed;
    private final String webPathsAllowed;

    public WdDeniedUrlForcedActionDeriver(Settings settings) {
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
    public Set<Action> derive(SUT system, State state, SessionPolicyContext context) {
        Assert.notNull(state);

        String currentUrl = WdDriver.getCurrentUrl();
        if (!isUrlDenied(currentUrl) && !isExtensionDenied(currentUrl)) {
            return Collections.emptySet();
        }

        Set<Action> actions = new LinkedHashSet<Action>();
        if (WdDriver.getWindowHandles().size() > 1) {
            actions.add(new WdCloseTabAction(state));
        } else {
            actions.add(new WdHistoryBackAction(state));
        }

        return Collections.unmodifiableSet(actions);
    }

    private boolean isExtensionDenied(String currentUrl) {
        if (currentUrl == null || !currentUrl.contains(".")) {
            return false;
        }

        if (webDeniedExtensions == null || webDeniedExtensions.isEmpty()) {
            return false;
        }

        String extension = currentUrl.substring(currentUrl.lastIndexOf(".") + 1);
        extension = extension.replace("/", "").toLowerCase();
        return webDeniedExtensions.contains(extension);
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
        } catch (Exception exception) {
            return "";
        }
    }
}
