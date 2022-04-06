package org.testar.visualvalidation.matcher;

import org.testar.visualvalidation.TextElement;

import java.util.Set;
import java.util.TreeSet;

public class MatcherResult {
    /**
     * Postfix for the screenshots created by the visual validation module.
     * Should be added to the end of normal action and state screenshots. E.g. "filename+postfix.extension"
     */
    public static final String ScreenshotPostFix = "_vv";
    private final Set<TextElement> noMatches = new TreeSet<>();
    private final Set<LocationMatch> locationMatches = new TreeSet<>();
    private final Set<ContentMatchResult> contentMatchResults = new TreeSet<>();

    public void addContentMatchResult(ContentMatchResult result) {
        contentMatchResults.add(result);
    }

    public void addLocationMatch(LocationMatch locationMatch) {
        locationMatches.add(locationMatch);
    }

    public void addNoLocationMatch(TextElement element) {
        noMatches.add(element);
    }

    public Set<TextElement> getNoLocationMatches() {
        return noMatches;
    }

    public Set<LocationMatch> getLocationMatches() {
        return locationMatches;
    }

    public Set<ContentMatchResult> getResult() {
        return contentMatchResults;
    }

    @Override
    public String toString() {
        return "MatcherResult{" +
                "noLocationMatches(" + noMatches.size() + ")=" + noMatches +
                ", locationMatches(" + locationMatches.size() + ")=" + locationMatches +
                ", contentMatches(" + contentMatchResults.size() + ")=" + contentMatchResults +
                '}';
    }
}
