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

    // a repo for generated classes, so we don't execute the same generation code over and over if not needed
    private static Map<Integer, EntityHydrator> hydrators = new HashMap<>();

    public static EntityHydrator getHydrator(int hydratorType) throws HydrationException {
        switch (hydratorType) {
            case HYDRATOR_ABSTRACT_STATE:
                return hydrators.containsKey(HYDRATOR_ABSTRACT_STATE) ? hydrators.get(HYDRATOR_ABSTRACT_STATE) : createAbstractStateHydrator();

            case HYDRATOR_ABSTRACT_ACTION:
                return hydrators.containsKey(HYDRATOR_ABSTRACT_ACTION) ? hydrators.get(HYDRATOR_ABSTRACT_ACTION) : createAbstractActionHydrator();

            case HYDRATOR_ABSTRACT_STATE_MODEL:
                return hydrators.containsKey(HYDRATOR_ABSTRACT_STATE_MODEL) ? hydrators.get(HYDRATOR_ABSTRACT_STATE_MODEL) : createAbstractStateModelHydrator();

            case HYDRATOR_CONCRETE_STATE:
                return hydrators.containsKey(HYDRATOR_CONCRETE_STATE) ? hydrators.get(HYDRATOR_CONCRETE_STATE) : createConcreteStateHydrator();

            case HYDRATOR_WIDGET:
                return hydrators.containsKey(HYDRATOR_WIDGET) ? hydrators.get(HYDRATOR_WIDGET) : createWidgetHydrator();

            case HYDRATOR_WIDGET_RELATION:
                return hydrators.containsKey(HYDRATOR_WIDGET_RELATION) ? hydrators.get(HYDRATOR_WIDGET_RELATION) : createWidgetRelationHydrator();

            case HYDRATOR_ABSTRACTED_BY:
                return hydrators.containsKey(HYDRATOR_ABSTRACTED_BY) ? hydrators.get(HYDRATOR_ABSTRACTED_BY) : createIsAbstractedByHydrator();

            case HYDRATOR_BLACKHOLE:
                return hydrators.containsKey(HYDRATOR_BLACKHOLE) ? hydrators.get(HYDRATOR_BLACKHOLE) : createBlackHoleHydrator();

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

}
