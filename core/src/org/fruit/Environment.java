package org.fruit;

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
     * @return The environment interface.
     */
    public static IEnvironment getInstance() {
        return instance;
    }

    /**
     * Sets the actual implementation of the interface.
     * @param implementation The concrete implementation of the interface.
     */
    public static void setInstance(IEnvironment implementation) {
        instance = implementation;
    }

}
