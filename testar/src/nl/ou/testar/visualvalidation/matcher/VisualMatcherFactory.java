package nl.ou.testar.visualvalidation.matcher;

public class VisualMatcherFactory {
    public static VisualMatcher createDummyMatcher() {
        return new VisualDummyMatcher();
    }

    public static VisualMatcher createLocationMatcher(MatcherConfiguration setting) {
        return new LocationMatcher(setting);
    }
}
