/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability.webdriver;

import org.testar.config.ConfigTags;
import org.testar.core.Assert;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.capability.TestSessionCapability;
import org.testar.webdriver.state.WdDriver;
import org.testar.webdriver.util.WebNavigationUtil;

import java.util.List;

public class WebdriverTestSessionCapability extends TestSessionCapability {

    private final TestSessionCapability delegate;

    public WebdriverTestSessionCapability(TestSessionCapability delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public void initializeTestSession(RuntimeContext runtimeContext) {
        Assert.notNull(runtimeContext);
        delegate.initializeTestSession(runtimeContext);
        ensureWebDomainsAllowed(runtimeContext);
    }

    @Override
    public void closeTestSession(RuntimeContext runtimeContext) {
        Assert.notNull(runtimeContext);
        delegate.closeTestSession(runtimeContext);
    }

    /*
     * If webDomainsAllowed from SUTConnectorValue is not set, include it in the webDomainsAllowed. 
     * If the default starting domain of the SUT is not set, included it in the webDomainsAllowed. 
     */
    private void ensureWebDomainsAllowed(RuntimeContext runtimeContext) {
        try {
            WebNavigationUtil.addInitialAllowedDomains(runtimeContext.settings(), WdDriver.getCurrentUrl());

            List<String> configuredDomainsAllowed = List.copyOf(runtimeContext.settings().get(ConfigTags.WebDomainsAllowed));
            System.out.println(String.format("WebDomainsAllowed: %s", String.join(",", configuredDomainsAllowed)));
        } catch(Exception e) {
            System.out.println("WEBDRIVER ERROR: Trying to add the startup domain to webDomainsAllowed List");
            System.out.println("Please review webDomainsAllowed List inside Webdriver Java Protocol");
        }
    }
}
