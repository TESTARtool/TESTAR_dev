package nl.ou.testar.visualvalidation.matcher;

import nl.ou.testar.visualvalidation.extractor.ExpectedElement;
import nl.ou.testar.visualvalidation.ocr.RecognizedElement;

import java.util.HashSet;
import java.util.Set;

public class LocationMatch {
    final public MatchLocation location;
    final public ExpectedElement expectedElement;

    public Set<RecognizedElement> recognizedElements = new HashSet<>();

    public LocationMatch(ExpectedElement expectedElement, int margin) {
        this.location = new MatchLocation(margin, expectedElement._location);
        this.expectedElement = expectedElement;
    }

    public void addRecognizedElement(RecognizedElement element) {
        recognizedElements.add(element);
    }

    @Override
    public String toString() {
        return "LocationMatch{" +
                "location=" + location +
                ", expectedElement=" + expectedElement +
                ", recognizedElements=" + recognizedElements +
                '}';
    }
}
