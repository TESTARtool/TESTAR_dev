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

    // a repo for generated classes, so we don't execute the same generation code over and over if not needed
    private static Map<Integer, EntityHydrator> hydrators = new HashMap<>();

    public static EntityHydrator getHydrator(int hydratorType) throws HydrationException {
        switch (hydratorType) {
            case HYDRATOR_ABSTRACT_STATE:
                return hydrators.containsKey(HYDRATOR_ABSTRACT_STATE) ? hydrators.get(HYDRATOR_ABSTRACT_STATE) : new AbstractStateHydrator();

            case HYDRATOR_ABSTRACT_ACTION:
                return hydrators.containsKey(HYDRATOR_ABSTRACT_ACTION) ? hydrators.get(HYDRATOR_ABSTRACT_ACTION) : new AbstractActionHydrator();

            case HYDRATOR_ABSTRACT_STATE_MODEL:
                return hydrators.containsKey(HYDRATOR_ABSTRACT_STATE_MODEL) ? hydrators.get(HYDRATOR_ABSTRACT_STATE_MODEL) : new AbstractStateModelHydrator();

            case HYDRATOR_CONCRETE_STATE:
                return hydrators.containsKey(HYDRATOR_CONCRETE_STATE) ? hydrators.get(HYDRATOR_CONCRETE_STATE) : new ConcreteStateHydrator();

            case HYDRATOR_WIDGET:
                return hydrators.containsKey(HYDRATOR_WIDGET) ? hydrators.get(HYDRATOR_WIDGET) : new WidgetHydrator();

            case HYDRATOR_WIDGET_RELATION:
                return hydrators.containsKey(HYDRATOR_WIDGET_RELATION) ? hydrators.get(HYDRATOR_WIDGET_RELATION) : new WidgetRelationHydrator();

            case HYDRATOR_ABSTRACTED_BY:
                return hydrators.containsKey(HYDRATOR_ABSTRACTED_BY) ? hydrators.get(HYDRATOR_ABSTRACTED_BY) : new isAbstractedByHydrator();

            default:
                throw new HydrationException("Invalid hydrator type provided to the hydrator factory");
        }
    }

}
