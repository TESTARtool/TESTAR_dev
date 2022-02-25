package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import nl.ou.testar.StateModel.Exception.HydrationException;

import java.util.HashMap;
import java.util.Map;

public abstract class HydratorFactory {

    public static final int HYDRATOR_ABSTRACT_STATE = 1;

    public static final int HYDRATOR_ABSTRACT_ACTION = 2;

    public static final int HYDRATOR_ABSTRACT_STATE_MODEL = 3;

    public static final int HYDRATOR_CONCRETE_STATE = 4;

    public static final int HYDRATOR_WIDGET = 5;

    public static final int HYDRATOR_WIDGET_RELATION = 6;

    public static final int HYDRATOR_ABSTRACTED_BY = 7;

    public static final int HYDRATOR_BLACKHOLE = 8;

    public static final int HYDRATOR_CONCRETE_ACTION = 9;

    public static final int HYDRATOR_SEQUENCE = 10;

    public static final int HYDRATOR_SEQUENCE_NODE = 11;

    public static final int HYDRATOR_ACCESSED = 12;

    public static final int HYDRATOR_SEQUENCE_STEP = 13;

    public static final int HYDRATOR_FIRST_NODE = 14;
    
    public static final int HYDRATOR_NON_DETERMINISTIC_HOLE = 15;

    // a repo for generated classes, so we don't execute the same generation code over and over if not needed
    private static Map<Integer, EntityHydrator> hydrators = new HashMap<>();

    public static EntityHydrator getHydrator(int hydratorType) throws HydrationException {
        if (hydrators.containsKey(hydratorType)) {
            return hydrators.get(hydratorType);
        }

        switch (hydratorType) {
            case HYDRATOR_ABSTRACT_STATE:
                return createAbstractStateHydrator();

            case HYDRATOR_ABSTRACT_ACTION:
                return createAbstractActionHydrator();

            case HYDRATOR_ABSTRACT_STATE_MODEL:
                return createAbstractStateModelHydrator();

            case HYDRATOR_CONCRETE_STATE:
                return createConcreteStateHydrator();

            case HYDRATOR_CONCRETE_ACTION:
                return createConcreteActionHydrator();

            case HYDRATOR_WIDGET:
                return createWidgetHydrator();

            case HYDRATOR_WIDGET_RELATION:
                return createWidgetRelationHydrator();

            case HYDRATOR_ABSTRACTED_BY:
                return createIsAbstractedByHydrator();

            case HYDRATOR_BLACKHOLE:
                return createBlackHoleHydrator();

            case HYDRATOR_SEQUENCE:
                return createSequenceHydrator();

            case HYDRATOR_SEQUENCE_NODE:
                return createSequenceNodeHydrator();

            case HYDRATOR_ACCESSED:
                return createAccessedHydrator();

            case HYDRATOR_SEQUENCE_STEP:
                return createSequenceStepHydrator();

            case HYDRATOR_FIRST_NODE:
                return createFirstNodeHydrator();

            case HYDRATOR_NON_DETERMINISTIC_HOLE:
                return createNonDeterministicHoleHydrator();


            default:
                throw new HydrationException("Invalid hydrator type provided to the hydrator factory");
        }
    }

    private static AbstractStateHydrator createAbstractStateHydrator() {
        AbstractStateHydrator abstractStateHydrator = new AbstractStateHydrator();
        hydrators.put(HYDRATOR_ABSTRACT_STATE, abstractStateHydrator);
        return abstractStateHydrator;
    }

    private static AbstractActionHydrator createAbstractActionHydrator() {
        AbstractActionHydrator abstractActionHydrator = new AbstractActionHydrator();
        hydrators.put(HYDRATOR_ABSTRACT_ACTION, abstractActionHydrator);
        return abstractActionHydrator;
    }

    private static AbstractStateModelHydrator createAbstractStateModelHydrator() {
        AbstractStateModelHydrator abstractStateModelHydrator  = new AbstractStateModelHydrator();
        hydrators.put(HYDRATOR_ABSTRACT_STATE_MODEL, abstractStateModelHydrator);
        return abstractStateModelHydrator;
    }

    private static ConcreteStateHydrator createConcreteStateHydrator() {
        ConcreteStateHydrator concreteStateHydrator = new ConcreteStateHydrator();
        hydrators.put(HYDRATOR_CONCRETE_STATE, concreteStateHydrator);
        return concreteStateHydrator;
    }

    private static WidgetHydrator createWidgetHydrator() {
        WidgetHydrator widgetHydrator = new WidgetHydrator();
        hydrators.put(HYDRATOR_WIDGET, widgetHydrator);
        return widgetHydrator;
    }

    private static WidgetRelationHydrator createWidgetRelationHydrator() {
        WidgetRelationHydrator widgetRelationHydrator = new WidgetRelationHydrator();
        hydrators.put(HYDRATOR_WIDGET_RELATION, widgetRelationHydrator);
        return widgetRelationHydrator;
    }

    private static IsAbstractedByHydrator createIsAbstractedByHydrator() {
        IsAbstractedByHydrator isAbstractedByHydrator = new IsAbstractedByHydrator();
        hydrators.put(HYDRATOR_ABSTRACTED_BY, isAbstractedByHydrator);
        return isAbstractedByHydrator;
    }

    private static BlackHoleHydrator createBlackHoleHydrator() {
        BlackHoleHydrator blackHoleHydrator = new BlackHoleHydrator();
        hydrators.put(HYDRATOR_BLACKHOLE, blackHoleHydrator);
        return blackHoleHydrator;
    }

    private static ConcreteActionHydrator createConcreteActionHydrator() {
        ConcreteActionHydrator concreteActionHydrator = new ConcreteActionHydrator();
        hydrators.put(HYDRATOR_CONCRETE_ACTION, concreteActionHydrator);
        return concreteActionHydrator;
    }

    private static SequenceHydrator createSequenceHydrator() {
        SequenceHydrator sequenceHydrator = new SequenceHydrator();
        hydrators.put(HYDRATOR_SEQUENCE, sequenceHydrator);
        return sequenceHydrator;
    }

    private static SequenceNodeHydrator createSequenceNodeHydrator() {
        SequenceNodeHydrator sequenceNodeHydrator = new SequenceNodeHydrator();
        hydrators.put(HYDRATOR_SEQUENCE_NODE, sequenceNodeHydrator);
        return sequenceNodeHydrator;
    }

    private static AccessedHydrator createAccessedHydrator() {
        AccessedHydrator accessedHydrator = new AccessedHydrator();
        hydrators.put(HYDRATOR_ACCESSED, accessedHydrator);
        return accessedHydrator;
    }

    private static SequenceStepHydrator createSequenceStepHydrator() {
        SequenceStepHydrator sequenceStepHydrator = new SequenceStepHydrator();
        hydrators.put(HYDRATOR_SEQUENCE_STEP, sequenceStepHydrator);
        return sequenceStepHydrator;
    }

    private static FirstNodeHydrator createFirstNodeHydrator() {
        FirstNodeHydrator firstNodeHydrator = new FirstNodeHydrator();
        hydrators.put(HYDRATOR_FIRST_NODE, firstNodeHydrator);
        return firstNodeHydrator;
    }

    private static NonDeterministicHoleHydrator createNonDeterministicHoleHydrator() {
    	NonDeterministicHoleHydrator nonDeterministicHoleHydrator = new NonDeterministicHoleHydrator();
    	hydrators.put(HYDRATOR_NON_DETERMINISTIC_HOLE, nonDeterministicHoleHydrator);
    	return nonDeterministicHoleHydrator;
    }

}

