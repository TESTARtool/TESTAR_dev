package org.testar.statemodel.persistence.orientdb.extractor;

import org.testar.statemodel.ConcreteState;
import org.testar.statemodel.ConcreteStateProxy;
import org.testar.statemodel.exceptions.ExtractionException;

import java.util.HashMap;
import java.util.Map;

public abstract class ExtractorFactory {

    public static final int EXTRACTOR_ABSTRACT_STATE = 1;

    public static final int EXTRACTOR_ABSTRACT_ACTION = 2;

    public static final int EXTRACTOR_ABSTRACT_STATE_TRANSITION = 3;

  public static final int EXTRACTOR_CONCRETE_STATE = 4;

  public static final int EXTRACTOR_CONCRETE_ACTION = 5;

  public static final int EXTRACTOR_CONCRETE_STATE_TRANSITION = 6;

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

            case EXTRACTOR_CONCRETE_STATE:
              return createConcreteStateExtractor();

            case EXTRACTOR_CONCRETE_ACTION:
              return createConcreteActionExtractor();

            case EXTRACTOR_CONCRETE_STATE_TRANSITION:
              return createConcreteStateTransitionExtractor();

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

  private static ConcreteStateProxyExtractor createConcreteStateExtractor() {
    ConcreteStateProxyExtractor concreteStateExtractor = null;
    concreteStateExtractor = new ConcreteStateProxyExtractor();
    extractors.put(EXTRACTOR_CONCRETE_STATE, concreteStateExtractor);
    return concreteStateExtractor;
  }

  private static ConcreteActionProxyExtractor createConcreteActionExtractor() {
    ConcreteActionProxyExtractor concreteActionExtractor = new ConcreteActionProxyExtractor();
    extractors.put(EXTRACTOR_CONCRETE_ACTION, concreteActionExtractor);
    return concreteActionExtractor;
  }

  private static ConcreteStateTransitionExtractor createConcreteStateTransitionExtractor() {
    try {
      ConcreteStateProxyExtractor concreteStateExtractor = (ConcreteStateProxyExtractor) ExtractorFactory.getExtractor(EXTRACTOR_CONCRETE_STATE);
      ConcreteActionProxyExtractor concreteActionExtractor = (ConcreteActionProxyExtractor) ExtractorFactory.getExtractor(EXTRACTOR_CONCRETE_ACTION);
      ConcreteStateTransitionExtractor concreteStateTransitionExtractor = new ConcreteStateTransitionExtractor(concreteStateExtractor, concreteActionExtractor);
      extractors.put(EXTRACTOR_CONCRETE_STATE_TRANSITION, concreteStateTransitionExtractor);
      return concreteStateTransitionExtractor;
    }
    catch (ExtractionException ex) {
      return null;
    }
  }

}
