/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.environment;

/**
 * Provides access to the environment in which the application is running.
 * This interface should be realized by an operating specific implementation. Since the core module is standalone module
 * without dependencies towards other modules, we specific the interface in the core module so that we can
 * use the IEnvironment within the core module. During the initialization phase of the application the realization of
 * IEnvironment needs to be set. This construction creates an abstraction layer between the logic and the operating
 * system on which the application is running.
 */
public class Environment {

    private static IEnvironment instance;

    /**
     * Get the environment interface.
     * @return The environment interface or UnknownEnvironment.
     */
    public static IEnvironment getInstance() {
        return (instance != null) ? instance : new UnknownEnvironment();
    }

    /**
     * Sets the actual implementation of the interface.
     * @param implementation The concrete implementation of the interface.
     */
    public static void setInstance(IEnvironment implementation) {
        if (implementation == null) {
            throw new IllegalArgumentException("Environment implementation cannot be set to null");
        }
        instance = implementation;
    }
}
