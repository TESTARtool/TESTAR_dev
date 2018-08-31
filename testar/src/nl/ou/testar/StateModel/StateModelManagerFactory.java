package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.ActionSelection.CompoundFactory;
import nl.ou.testar.StateModel.Persistence.OrientDB.OrientDBManager;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Persistence.PersistenceManagerFactory;
import nl.ou.testar.StateModel.Persistence.PersistenceManagerFactoryBuilder;
import org.fruit.monkey.Settings;

public class StateModelManagerFactory {

    public static StateModelManager getStateModelManager(Settings settings) {
        // simple creation logic for now
        //@todo replace this hash with a real one later
        String abstractionHash = "12345679";
        AbstractStateModel abstractStateModel = new AbstractStateModel(abstractionHash);
        ActionSelector actionSelector = CompoundFactory.getCompoundActionSelector();
        // add an orientdb persistence manager
        PersistenceManagerFactory persistenceManagerFactory = PersistenceManagerFactoryBuilder.createPersistenceManagerFactory(PersistenceManagerFactoryBuilder.ManagerType.ORIENTDB);
        PersistenceManager persistenceManager = persistenceManagerFactory.getPersistenceManager(settings);
        // we provide it to our statemodel as a listener
        abstractStateModel.addEventListener((OrientDBManager)persistenceManager);
        return new StateModelManager(abstractStateModel, actionSelector, persistenceManager);
    }

}
