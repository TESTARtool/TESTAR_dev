package org.testar.statemodel.persistence.orientdb.extractor;

import org.testar.statemodel.exceptions.ExtractionException;

import java.util.HashMap;
import java.util.Map;

public abstract class ExtractorFactory {

    public static final int EXTRACTOR_ABSTRACT_STATE = 1;

    public static final int EXTRACTOR_ABSTRACT_ACTION = 2;

    public static final int EXTRACTOR_ABSTRACT_STATE_TRANSITION = 3;

    // a repo for generated classes, so we don't execute the same generation code over and over if not needed
    private static Map<Integer, EntityExtractor> extractors = new HashMap<>();

    /**
     * This method returns an extractor for a given extractor type.
     * @param extractorType
     * @return
     * @throws ExtractionException
     */
    public static EntityExtractor getExtractor(int extractorType) throws ExtractionException {
        if (extractors.containsKey(extractorType)) {
            return extractors.get(extractorType);
        }

        switch (extractorType) {
            case EXTRACTOR_ABSTRACT_STATE:
                return createAbstractStateExtractor();

            case EXTRACTOR_ABSTRACT_ACTION:
                return createAbstractActionExtractor();

            case EXTRACTOR_ABSTRACT_STATE_TRANSITION:
                return createAbstractStateTransitionExtractor();

            default: throw new ExtractionException("Illegal extractor type specified");
        }
    }

    private static AbstractStateExtractor createAbstractStateExtractor() {
        AbstractStateExtractor abstractStateExtractor = null;
        try {
            abstractStateExtractor = new AbstractStateExtractor((AbstractActionExtractor) ExtractorFactory.getExtractor(EXTRACTOR_ABSTRACT_ACTION));
        } catch (ExtractionException e) {
            return null;
        }
        extractors.put(EXTRACTOR_ABSTRACT_STATE, abstractStateExtractor);
        return abstractStateExtractor;
    }

    private static AbstractActionExtractor createAbstractActionExtractor() {
        AbstractActionExtractor abstractActionExtractor = new AbstractActionExtractor();
        extractors.put(EXTRACTOR_ABSTRACT_ACTION, abstractActionExtractor);
        return abstractActionExtractor;
    }

    private static AbstractStateTransitionExtractor createAbstractStateTransitionExtractor() {
        try {
            AbstractStateExtractor abstractStateExtractor = (AbstractStateExtractor) ExtractorFactory.getExtractor(EXTRACTOR_ABSTRACT_STATE);
            AbstractActionExtractor abstractActionExtractor = (AbstractActionExtractor) ExtractorFactory.getExtractor(EXTRACTOR_ABSTRACT_ACTION);
            AbstractStateTransitionExtractor abstractStateTransitionExtractor = new AbstractStateTransitionExtractor(abstractStateExtractor, abstractActionExtractor);
            extractors.put(EXTRACTOR_ABSTRACT_STATE_TRANSITION, abstractStateTransitionExtractor);
            return abstractStateTransitionExtractor;
        }
        catch (ExtractionException ex) {
            return null;
        }
    }

}
