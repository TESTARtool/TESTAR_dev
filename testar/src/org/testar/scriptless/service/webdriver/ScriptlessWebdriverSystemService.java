/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service.webdriver;

import org.testar.config.ConfigTags;
import org.testar.core.Assert;
import org.testar.core.environment.Environment;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.service.SystemService;
import org.testar.core.state.SUT;
import org.testar.core.tag.Tags;
import org.testar.scriptless.RuntimeContext;

public final class ScriptlessWebdriverSystemService implements SystemService {

    private final SystemService delegate;
    private final RuntimeContext runtimeContext;

    public ScriptlessWebdriverSystemService(SystemService delegate, RuntimeContext runtimeContext) {
        this.delegate = Assert.notNull(delegate);
        this.runtimeContext = Assert.notNull(runtimeContext);
    }

    @Override
    public SUT startSystem() throws SystemStartException {
        SUT system = delegate.startSystem();

        double displayScale = getDisplayScale(system);
        runtimeContext.setMouse(system.get(Tags.StandardMouse));
        runtimeContext.mouse().setCursorDisplayScale(displayScale);

        return system;
    }

    @Override
    public void stopSystem(SUT system) {
        delegate.stopSystem(system);
    }

    private double getDisplayScale(SUT system) {
        double displayScale = Environment.getInstance().getDisplayScale(system.get(Tags.HWND, 0L));
        String overrideDisplayScale = runtimeContext.settings().get(ConfigTags.OverrideWebDriverDisplayScale, "");

        if (!overrideDisplayScale.isEmpty()) {
            try {
                double parsedOverrideDisplayScale = Double.parseDouble(overrideDisplayScale);
                if (parsedOverrideDisplayScale != 0) {
                    displayScale = parsedOverrideDisplayScale;
                }
            } catch (NumberFormatException exception) {
                System.out.printf(
                        "WARNING Unable to convert display scale override to double: %s, will use %f\n",
                        overrideDisplayScale,
                        displayScale
                );
            }
        }

        return displayScale;
    }
}
