package nl.ou.testar.StateModel.Persistence.OrientDB.Extractor;

import nl.ou.testar.StateModel.Exception.ExtractionException;

import java.util.HashMap;
import java.util.Map;

public abstract class ExtractorFactory {

    public static final int EXTRACTOR_ABSTRACT_STATE = 1;

    // a repo for generated classes, so we don't execute the same generation code over and over if not needed
    private static Map<Integer, EntityExtractor> extractors = new HashMap<>();

    public static EntityExtractor getExtractor(int extractorType) throws ExtractionException {
        switch (extractorType) {
            case EXTRACTOR_ABSTRACT_STATE:
                return extractors.containsKey(extractorType) ? extractors.get(extractorType) : new AbstractStateExtractor();

            default: throw new ExtractionException("Illegal extractor type specified");
        }
    }

}
