/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability.webdriver;

import org.apache.commons.lang3.ArrayUtils;
import org.testar.config.ConfigTags;
import org.testar.core.Assert;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.capability.TestSessionCapability;
import org.testar.webdriver.state.WdDriver;

import java.util.Arrays;
import java.util.List;

public final class WebdriverTestSessionCapability extends TestSessionCapability {

    private final TestSessionCapability delegate;

    public WebdriverTestSessionCapability(TestSessionCapability delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public void initializeTestSession(RuntimeContext runtimeContext) {
        delegate.initializeTestSession(runtimeContext);
        ensureWebDomainsAllowed(runtimeContext);
    }

    @Override
    public void closeTestSession(RuntimeContext runtimeContext) {
        delegate.closeTestSession(runtimeContext);
    }

    /*
     * If webDomainsAllowed from SUTConnectorValue is not set, include it in the webDomainsAllowed
     */
    private void ensureWebDomainsAllowed(RuntimeContext runtimeContext) {
        List<String> webDomainsAllowed = runtimeContext.settings().get(ConfigTags.WebDomainsAllowed).contains("null") ? 
            null : 
            runtimeContext.settings().get(ConfigTags.WebDomainsAllowed);

        try{
            // Adding default domain from SUTConnectorValue if is not included in the webDomainsAllowed list
            String[] parts = runtimeContext.settings().get(ConfigTags.SUTConnectorValue, "").split(" ");
            String sutConnectorUrl = "";

            for (String raw : parts) {
                String part = raw.replace("\"", "");

                if (part.matches("^(?:[a-zA-Z][a-zA-Z0-9+.-]*):.*")) {
                    sutConnectorUrl = part;
                }
            }

            if(webDomainsAllowed != null && !webDomainsAllowed.contains(getDomain(sutConnectorUrl))) {
                System.out.println(String.format("WEBDRIVER INFO: Automatically adding %s SUT Connector domain to webDomainsAllowed List", getDomain(sutConnectorUrl)));
                String[] newWebDomainsAllowed = webDomainsAllowed.stream().toArray(String[]::new);
                webDomainsAllowed = Arrays.asList(ArrayUtils.insert(newWebDomainsAllowed.length, newWebDomainsAllowed, getDomain(sutConnectorUrl)));
                System.out.println(String.format("webDomainsAllowed: %s", String.join(",", webDomainsAllowed)));
            }

            // Also add the default starting domain of the SUT if is not included in the webDomainsAllowed list
            String initialUrl = WdDriver.getCurrentUrl();

            if(webDomainsAllowed != null && !webDomainsAllowed.contains(getDomain(initialUrl))) {
                System.out.println(String.format("WEBDRIVER INFO: Automatically adding initial %s Web domain to webDomainsAllowed List", getDomain(initialUrl)));
                String[] newWebDomainsAllowed = webDomainsAllowed.stream().toArray(String[]::new);
                webDomainsAllowed = Arrays.asList(ArrayUtils.insert(newWebDomainsAllowed.length, newWebDomainsAllowed, getDomain(initialUrl)));
                System.out.println(String.format("webDomainsAllowed: %s", String.join(",", webDomainsAllowed)));
            }
        } catch(Exception e) {
            System.out.println("WEBDRIVER ERROR: Trying to add the startup domain to webDomainsAllowed List");
            System.out.println("Please review webDomainsAllowed List inside Webdriver Java Protocol");
        }
    }

    /*
     * Get the domain from a full URL
     */
    private String getDomain(String url) {
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
}
