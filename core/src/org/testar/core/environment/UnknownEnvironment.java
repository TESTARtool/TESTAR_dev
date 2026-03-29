/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.environment;

/**
 * A default environment implementation. Can be used as fallback option when a OS specific environment implementation
 * is missing.
 * NOTE: This implementation prevents the application from crashing but doesn't guarantee correct behavior.
 */
public class UnknownEnvironment implements IEnvironment {

    @Override
    public double getDisplayScale(long windowHandle) {
        return 1.0;
    }
}
