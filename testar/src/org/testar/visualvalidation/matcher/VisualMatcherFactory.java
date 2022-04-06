package org.testar.visualvalidation.matcher;

public class VisualMatcherFactory {
    public static VisualMatcherInterface createDummyMatcher() {
        return new VisualDummyMatcher();
    }

    public static VisualMatcherInterface createLocationMatcher(MatcherConfiguration setting) {
        return new LocationMatcher(setting);
    }
}
