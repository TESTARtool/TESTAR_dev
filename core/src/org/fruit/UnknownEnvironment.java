package org.fruit;

/**
 * A default environment implementation. Can be used as fallback option when a OS specific environment implementation
 * is missing.
 * NOTE: This implementation prevents the application from crashing but doesn't guarantee correct behavior.
 */
public class UnknownEnvironment implements IEnvironment {
    @Override
    public double getDisplayScale(long windowHandle){
        System.out.println("WARNING getDisplayScale not implemented for current OS, returning default value");
        return 1.0;
    }
}
