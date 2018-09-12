package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

public abstract class HydratorFactory {

    public static final int HYDRATOR_ABSTRACT_STATE = 1;

    public static EntityHydrator getHydrator(int hydratorType) {
        switch (hydratorType) {
            case HYDRATOR_ABSTRACT_STATE:
            default:
                return new AbstractStateHydrator();
        }
    }

}
