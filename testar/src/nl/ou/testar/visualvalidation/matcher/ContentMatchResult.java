package nl.ou.testar.visualvalidation.matcher;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Holds the matching result for the content of the expected text.
 */
public class ContentMatchResult {
    public final ExpectedTextMatchResult expectedResult;
    public final RecognizedTextMatchResult recognizedResult;
    public final long totalMatched;
    public final long totalExpected;

    public ContentMatchResult(ExpectedTextMatchResult expectedResult, RecognizedTextMatchResult recognizedResult) {
        this.expectedResult = expectedResult;
        this.recognizedResult = recognizedResult;
        totalMatched = expectedResult.expectedText.stream()
                .filter(e -> e.result != CharacterMatchResult.NO_MATCH)
                .count();
        totalExpected = expectedResult.expectedText.size();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder()
                .append("\n")

                .append("Matched \"").append(expectedResult.expectedText.stream().
                        map(e -> e.character.character).collect(Collector.of(
                        StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append,
                        StringBuilder::toString)))
                .append("\"\n")

                .append("Result: ")
                .append(expectedResult.expectedText.stream().map(it -> {
                    switch (it.result) {
                        case WHITESPACE_CORRECTED:
                            return "W";
                        case CASE_MISMATCH:
                            return "C";
                        case MATCHED:
                            return "V";
                        default:
                            return "X";
                    }
                }).collect(Collectors.toList()))
                .append(" [").append(totalMatched).append("/").append(totalExpected).append("]\n")

                .append("Expect: ").append(expectedResult.expectedText.stream()
                        .map(it -> it.character.character)
                        .collect(Collectors.toList()))
                .append("\n")

                .append("Found:  ")
                .append(expectedResult.expectedText.stream()
                        .map(it -> {
                            if (it.character.isMatched()) {
                                return it.character.match.character;
                            } else {
                                return " ";
                            }
                        })
                        .collect(Collectors.toList()))
                .append("\n");

        List<CharacterMatchEntry> garbage = recognizedResult.recognized.stream()
                .filter(CharacterMatchEntry::isNotMatched)
                .collect(Collectors.toList());

        if (!garbage.isEmpty()) {
            str.append("Garbage:");
            str.append(garbage.stream()
                    .map(it -> it.character)
                    .collect(Collectors.toList()));
        }

        return str.toString();
    }
}
