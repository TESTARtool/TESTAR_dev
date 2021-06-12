package nl.ou.testar.visualvalidation.matcher;

import nl.ou.testar.visualvalidation.ocr.RecognizedElement;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * The matcher result for the recognized text.
 */
public class RecognizedTextMatchResult {
    List<CharacterMatchEntry> recognized;

    /**
     * Constructor creates a consecutive order of recognized text characters.
     * @param recognizedElements A list with recognized elements that should be treated as one.
     */
    public RecognizedTextMatchResult(List<RecognizedElement> recognizedElements) {
        recognized = recognizedElements.stream().flatMap(
                recognizedElement -> recognizedElement._text.chars().boxed().map(it -> Character.toChars(it)[0]))
                .map(CharacterMatchEntry::new)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "RecognizedTextMatchResult{" +
                recognized.stream().map(e -> e.character).collect(Collector.of(
                        StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append,
                        StringBuilder::toString)) +
                ":" + recognized.size() + "){ " +
                recognized.stream().map(CharacterMatchEntry::toString).collect(Collectors.joining());
    }
}
