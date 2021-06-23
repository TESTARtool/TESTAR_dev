package nl.ou.testar.visualvalidation.matcher;

import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * The matcher result for the expected text.
 */
public class ExpectedTextMatchResult {
    ArrayList<CharacterMatch> expectedText;

    /**
     * Constructor.
     * @param expectedText The expected text that we are matching.
     */
    public ExpectedTextMatchResult(String expectedText) {
        this.expectedText = new ArrayList<>(expectedText.length());
        for (char ch: expectedText.toCharArray()) {
            this.expectedText.add(new CharacterMatch(ch));
        }
    }

    @Override
    public String toString() {
        return "ExpectedTextMatchResult (" +
                expectedText.stream().map(e -> e.character.character).collect(Collector.of(
                        StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append,
                        StringBuilder::toString)) +
                ":" + expectedText.size() + "){ " +
                expectedText.stream().map(CharacterMatch::toString).collect(Collectors.joining());
    }
}
