/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service.webdriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.testar.config.ConfigTags;
import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.action.ActivateSystem;
import org.testar.core.action.AnnotatingActionCompiler;
import org.testar.core.action.StdActionCompiler;
import org.testar.core.alayer.Shape;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.scriptless.RuntimeContext;
import org.testar.webdriver.action.WdCloseTabAction;
import org.testar.webdriver.action.WdHistoryBackAction;
import org.testar.webdriver.action.WdRemoteClickAction;
import org.testar.webdriver.state.WdDriver;
import org.testar.webdriver.state.WdWidget;
import org.testar.webdriver.tag.WdTags;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public final class ScriptlessWebdriverActionDerivationService implements ActionDerivationService {

    private final ActionDerivationService delegate;
    private final RuntimeContext runtimeContext;

    private final List<String> webDeniedExtensions;
    private final List<String> webDomainsAllowed;
    private final String webPathsAllowed;

    // List of attributes to identify and close policy popups
    protected Multimap<String, String> policyAttributes = ArrayListMultimap.create();

    public ScriptlessWebdriverActionDerivationService(ActionDerivationService delegate, RuntimeContext runtimeContext) {
        this.delegate = Assert.notNull(delegate);
        this.runtimeContext = Assert.notNull(runtimeContext);

        // Disallow links and pages with these extensions
        // Set to null to ignore this feature
        webDeniedExtensions = runtimeContext.settings().get(ConfigTags.WebDeniedExtensions).contains("null") ? null : runtimeContext.settings().get(ConfigTags.WebDeniedExtensions);

        // Define a whitelist of allowed domains for links and pages
        // An empty list will be filled with the domain from the sut connector
        // Set to null to ignore this feature
        webDomainsAllowed = runtimeContext.settings().get(ConfigTags.WebDomainsAllowed).contains("null") ? null : runtimeContext.settings().get(ConfigTags.WebDomainsAllowed);

        // Regular expression string that indicates a whitelist of allowed web paths
        webPathsAllowed = runtimeContext.settings().get(ConfigTags.WebPathsAllowed).contains("null") ? null : runtimeContext.settings().get(ConfigTags.WebPathsAllowed);
    }

    @Override
    public Set<Action> deriveActions(SUT system, State state) {
        Assert.notNull(system, state);

        Set<Action> actions = delegate.deriveActions(system, state);

        // If we have forced actions, prioritize and filter the other ones
        Set<Action> forcedActions = detectForcedActions(state);
        if (forcedActions != null && forcedActions.size() > 0) {
            actions = forcedActions;
        }

        // If there is an ActivateSystem action
        // We need to force its selection to move the system to the foreground
        Action forceForegroungAction = actions.stream().filter(a -> a instanceof ActivateSystem).findAny().orElse(null);
        if (forceForegroungAction != null) {
            System.out.println("DEBUG: Forcing the System to be in the foreground !");
            actions = new HashSet<>(Collections.singletonList(forceForegroungAction));
        }

        // If derive actions didn't find any action, inform the user and force WdHistoryBackAction
        if(actions == null || actions.isEmpty()) {
            System.out.println(String.format("** WEBDRIVER WARNING: In Action number %s the State seems to have no interactive widgets", runtimeContext.actionCount()));
            System.out.println(String.format("** URL: %s", WdDriver.getCurrentUrl()));
            System.out.println("** Please try to navigate with SPY mode and configure clickableClasses inside Java protocol");
            actions = new HashSet<>(Collections.singletonList(new WdHistoryBackAction(state)));
        }

        return actions;
    }

    /*
     * Check the state if we need to force an action
     */
    protected Set<Action> detectForcedActions(State state) {
        StdActionCompiler ac = new AnnotatingActionCompiler();

        Set<Action> actions = detectForcedDeniedUrl(state);
        if (actions != null && actions.size() > 0) {
            return actions;
        }

        actions = detectForcedPopupClick(state, ac);
        if (actions != null && actions.size() > 0) {
            return actions;
        }

        return new HashSet<Action>();
    }

    /*
     * Force closing of Policies Popup
     */
    protected Set<Action> detectForcedPopupClick(State state, StdActionCompiler ac) {
        Set<Action> popupClickActions = new HashSet<Action>();

        if (policyAttributes == null || policyAttributes.size() == 0) {
            return popupClickActions;
        }

        for (Widget widget : state) {
            // If not visible widget, ignore
            if (!isAtBrowserCanvas(widget) || widget.get(WdTags.WebAttributeMap, null) == null) {
                continue;
            }

            // If some of the attributed matches, add the possible click action
            boolean popupMatches = false;
            for (String key : policyAttributes.keySet()) {
                String attribute = widget.get(WdTags.WebAttributeMap).get(key);
                for (String entryValue: policyAttributes.get(key)) {
                    popupMatches |= entryValue.equals(attribute);
                }
            }
            if (popupMatches) {
                popupClickActions.add(new WdRemoteClickAction((WdWidget) widget));
            }
        }

        return popupClickActions;
    }

    /*
     * Force back action due to disallowed domain or extension
     */
    protected Set<Action> detectForcedDeniedUrl(State state) {
        String currentUrl = WdDriver.getCurrentUrl();

        // Don't get caught in PDFs etc. and non-whitelisted domains
        if (isUrlDenied(currentUrl) || isExtensionDenied(currentUrl)) {
            // If opened in new tab, close it
            if (WdDriver.getWindowHandles().size() > 1) {
                return new HashSet<>(Collections.singletonList(new WdCloseTabAction(state)));
            }
            // Single tab, go back to previous page
            else {
                return new HashSet<>(Collections.singletonList(new WdHistoryBackAction(state)));
            }
        }

        return new HashSet<Action>();
    }

    /*
     * We need to check if click position is within the canvas
     */
    protected boolean isAtBrowserCanvas(Widget widget) {
        Shape shape = widget.get(Tags.Shape, null);
        if (shape == null) {
            return false;
        }

        // Widget must be completely visible on viewport for screenshots
        return widget.get(WdTags.WebIsFullOnScreen, false);
    }

    /*
     * Check if the current address has a denied extension (PDF etc.)
     */
    protected boolean isExtensionDenied(String currentUrl) {
        // If the current page doesn't have an extension, always allow
        if (!currentUrl.contains(".")) {
            return false;
        }

        if (webDeniedExtensions == null || webDeniedExtensions.size() == 0) {
            return false;
        }

        // Deny if the extension is in the list
        String ext = currentUrl.substring(currentUrl.lastIndexOf(".") + 1);
        ext = ext.replace("/", "").toLowerCase();
        return webDeniedExtensions.contains(ext);
    }

    /*
     * Check if the URL is denied
     */
    protected boolean isUrlDenied(String currentUrl) {
        if (currentUrl.startsWith("mailto:")) {
            return true;
        }

        // Always allow local file
        if (currentUrl.startsWith("file:///")) {
            return false;
        }

        // Only allow pre-approved web domains
        if(webDomainsAllowed != null && !webDomainsAllowed.contains(getDomain(currentUrl))) {
            return true;
        }

        // Only allow pre-approved web paths
        if (webPathsAllowed != null 
                && !webPathsAllowed.isEmpty() 
                // Empty web paths or generic / paths are allowed
                && !getPath(currentUrl).isEmpty()
                && !getPath(currentUrl).equals("/")) {

            // Create a regex pattern from the allowed paths
            Pattern pattern = Pattern.compile(webPathsAllowed);

            // If the path does not match the allowed regex pattern, web url is denied
            if (!pattern.matcher(getPath(currentUrl)).find()) {
                return true;
            }
        }

        // If no condition is meet, do not deny the url
        return false;
    }

    /*
     * Check if the widget has a denied URL as hyperlink
     */
    protected boolean isLinkDenied(Widget widget) {
        String linkUrl = widget.get(Tags.ValuePattern, "");

        // Not a link or local file, allow
        if (linkUrl == null || linkUrl.startsWith("file:///")) {
            return false;
        }

        // Deny the link based on extension
        if (isExtensionDenied(linkUrl)) {
            return true;
        }

        // Mail link, deny
        if (linkUrl.startsWith("mailto:")) {
            return true;
        }

        // For web links (e.g., https://para.testar.org/)
        if ((linkUrl.startsWith("https://") || linkUrl.startsWith("http://"))) {
            // Only allow pre-approved web domains (e.g., para.testar.org)
            if(webDomainsAllowed != null 
                    && !webDomainsAllowed.contains(getDomain(linkUrl))) {
                return true;
            }
        }

        // Check if webPathsAllowed is not empty and valid
        if (webPathsAllowed != null 
                && !webPathsAllowed.isEmpty()
                && !linkUrl.isEmpty()) {
            // Create a regex pattern from the allowed paths
            Pattern pattern = Pattern.compile(webPathsAllowed);

            // When checking the allowed paths, 
            // we need to transform possible relative urls to absolute urls
            String absoluteUrl = resolveRelativeUrl(linkUrl, WdDriver.getCurrentUrl());

            // If the path does not match the allowed regex pattern, web link is denied
            if (!pattern.matcher(absoluteUrl).find()) {
                return true;
            }
        }

        // If no condition is meet, do not deny the link
        return false;
    }

    /*
     * Get the domain from a full URL
     */
    protected String getDomain(String url) {
        if (url == null) {
            return null;
        }

        // When serving from file, 'domain' is filesystem
        if (url.startsWith("file://")) {
            return "file://";
        }

        url = url.replace("https://", "").replace("http://", "").replace("file://", "");
        return (url.split("/")[0]).split("\\?")[0];
    }

    /*
     * Extracts the path from a URL.
     */
    protected String getPath(String url) {
        try {
            return new URL(url).getPath();
        } catch (Exception e) {
            return "";
        }
    }

    // Helper method to resolve relative URLs
    private String resolveRelativeUrl(String relativeUrl, String baseUrl) {
        try {
            URL base = new URL(baseUrl);
            URL absolute = new URL(base, relativeUrl);
            return absolute.toString();
        } catch (MalformedURLException e) {
            // If resolving fails, return the original link
            return relativeUrl;
        }
    }
}
