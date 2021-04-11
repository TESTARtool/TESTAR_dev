package nl.ou.testar.visualvalidation.matcher;

import nl.ou.testar.visualvalidation.TextElement;

import java.util.HashSet;
import java.util.Set;

public class MatcherResult {
    private final Set<TextElement> noMatches = new HashSet<>();
    private final Set<Match> matches = new HashSet<>();

    public void addMatch(Match match) {
        matches.add(match);
    }

    public void addNoMatch(TextElement element) {
        noMatches.add(element);
    }

    public Set<TextElement> getNoMatches() {
        return noMatches;
    }

    public Set<Match> getMatches() {
        return matches;
    }

    @Override
    public String toString() {
        return "MatcherResult{" +
                "noMatches(" + noMatches.size() + ")=" + noMatches +
                ", matches(" + matches.size() + ")=" + matches +
                '}';
    }
}
