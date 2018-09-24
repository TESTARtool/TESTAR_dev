package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import nl.ou.testar.StateModel.Exception.HydrationException;

public abstract class HydratorFactory {

    public static final int HYDRATOR_ABSTRACT_STATE = 1;

    public static final int HYDRATOR_ABSTRACT_ACTION = 2;

    public static final int HYDRATOR_ABSTRACT_STATE_MODEL = 3;

    public static EntityHydrator getHydrator(int hydratorType) throws HydrationException {
        switch (hydratorType) {
            case HYDRATOR_ABSTRACT_STATE:
                return new AbstractStateHydrator();

            case HYDRATOR_ABSTRACT_ACTION:
                return new AbstractActionHydrator();

            case HYDRATOR_ABSTRACT_STATE_MODEL:
                return new AbstractStateModelHydrator();

            default:
                throw new HydrationException("Invalid hydrator type provided to the hydrator factory");
        }
    }

}
