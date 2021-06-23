package nl.ou.testar.visualvalidation.matcher;

import nl.ou.testar.visualvalidation.TextElement;

import java.util.HashSet;
import java.util.Set;

public class MatcherResult {
    private final Set<TextElement> noMatches = new HashSet<>();
    private final Set<LocationMatch> locationMatches = new HashSet<>();
    private final Set<ContentMatchResult> contentMatchResults = new HashSet<>();

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
