/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.service;

import org.testar.android.AndroidAppiumFramework;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.exceptions.SystemStopException;
import org.testar.core.service.SystemService;
import org.testar.core.state.SUT;

/**
 * Android implementation of {@link SystemService} backed by Appium settings.
 */
public final class AndroidSystemService implements SystemService {

    @FunctionalInterface
    public interface Starter {
        SUT start() throws SystemStartException;
    }

    private final Starter starter;

    public AndroidSystemService(Starter starter) {
        this.starter = Assert.notNull(starter);
    }

    public static AndroidSystemService fromSettings(Settings settings) {
        Assert.notNull(settings);
        return new AndroidSystemService(() -> AndroidAppiumFramework.fromSettings(settings));
    }

    @Override
    public SUT startSystem() throws SystemStartException {
        return starter.start();
    }

    @Override
    public void stopSystem(SUT system) {
        if (system == null) {
            return;
        }
        try {
            system.stop();
        } catch (SystemStopException exception) {
            throw new IllegalStateException("Unable to stop Android system", exception);
        }
    }
}
