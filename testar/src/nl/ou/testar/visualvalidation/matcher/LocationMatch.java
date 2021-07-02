package nl.ou.testar.visualvalidation.matcher;

import nl.ou.testar.visualvalidation.extractor.ExpectedElement;
import nl.ou.testar.visualvalidation.ocr.RecognizedElement;

import java.util.Set;
import java.util.TreeSet;

public class LocationMatch implements Comparable<LocationMatch> {
    final public MatchLocation location;
    final public ExpectedElement expectedElement;

    public Set<RecognizedElement> recognizedElements = new TreeSet<>();

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

    @Override
    public int compareTo(LocationMatch other) {
        int result = -1;
        if (location.equals(other.location) && expectedElement.equals(other.expectedElement)) {
            result = 0;
        }
        return result;    }
}
