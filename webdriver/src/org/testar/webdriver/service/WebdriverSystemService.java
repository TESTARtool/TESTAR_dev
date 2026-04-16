/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.service;

import org.testar.core.Assert;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.exceptions.SystemStopException;
import org.testar.core.service.SystemService;
import org.testar.core.state.SUT;
import org.testar.webdriver.state.WdDriver;

/**
 * WebDriver implementation of {@link SystemService} backed by a SUT connector
 * string.
 */
public final class WebdriverSystemService implements SystemService {

    @FunctionalInterface
    public interface Starter {
        SUT start() throws SystemStartException;
    }

    private final Starter starter;

    public WebdriverSystemService(Starter starter) {
        this.starter = Assert.notNull(starter);
    }

    public static WebdriverSystemService fromSutConnector(String sutConnector) {
        return new WebdriverSystemService(() -> WdDriver.fromExecutable(sutConnector));
    }

    @Override
    public SUT startSystem() throws SystemStartException {
        SUT system = starter.start();
        WebdriverWindowHandleSupport.bindWindowHandle(system);
        return system;
    }

    @Override
    public void stopSystem(SUT system) {
        if (system == null) {
            return;
        }
        try {
            system.stop();
        } catch (SystemStopException exception) {
            throw new IllegalStateException("Unable to stop WebDriver system", exception);
        }
    }
}
