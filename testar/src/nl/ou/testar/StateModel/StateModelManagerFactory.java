package nl.ou.testar.StateModel;

import es.upv.staq.testar.CodingManager;
import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.ActionSelection.CompoundFactory;
import nl.ou.testar.StateModel.Event.StateModelEventListener;
import nl.ou.testar.StateModel.Persistence.OrientDB.OrientDBManager;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Persistence.PersistenceManagerFactory;
import nl.ou.testar.StateModel.Persistence.PersistenceManagerFactoryBuilder;
import nl.ou.testar.StateModel.Sequence.SequenceManager;
import org.fruit.alayer.Tag;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StateModelManagerFactory {

    public static StateModelManager getStateModelManager(Settings settings) {
        // check the attributes for the abstract state id
        if (settings.get(ConfigTags.AbstractStateAttributes).isEmpty()) {
            throw new RuntimeException("No Abstract State Attributes were provided in the settings file");
        }

        Set<Tag<?>> abstractTags = new HashSet<>();
        for (String abstractStateAttribute : settings.get(ConfigTags.AbstractStateAttributes)) {
            abstractTags.add(CodingManager.allowedStateTags.get(abstractStateAttribute));
        }

        // and then check the attributes for the concrete state id
        if (settings.get(ConfigTags.ConcreteStateAttributes).isEmpty()) {
            throw new RuntimeException("No concrete State Attributes were provided in the settings file");
        }

        Set<Tag<?>> concreteStateTags = new HashSet<>();
        for (String concreteStateAttribute : settings.get(ConfigTags.ConcreteStateAttributes)) {
            concreteStateTags.add(CodingManager.allowedStateTags.get(concreteStateAttribute));
        }

        // get a persistence manager
        PersistenceManagerFactoryBuilder.ManagerType managerType;
        if (!settings.get(ConfigTags.StateModelEnabled)) {
            managerType = PersistenceManagerFactoryBuilder.ManagerType.DUMMY;
        }
        else {
            managerType = PersistenceManagerFactoryBuilder.ManagerType.valueOf(settings.get(ConfigTags.DataStore).toUpperCase());
        }
        PersistenceManagerFactory persistenceManagerFactory = PersistenceManagerFactoryBuilder.createPersistenceManagerFactory(managerType);
        PersistenceManager persistenceManager = persistenceManagerFactory.getPersistenceManager(settings);

        // get the abstraction level identifier that uniquely identifies the state model we are testing against.
        String abstractionLevelIdentifier = CodingManager.getAbstractStateModelHash();

        // we need a sequence manager to record the sequences
        Set<StateModelEventListener> eventListeners = new HashSet<>();
        eventListeners.add((StateModelEventListener) persistenceManager);
        SequenceManager sequenceManager = new SequenceManager(eventListeners, abstractionLevelIdentifier);

        // create the abstract state model and then the state model manager
        AbstractStateModel abstractStateModel = new AbstractStateModel(abstractionLevelIdentifier, concreteStateTags, persistenceManager instanceof StateModelEventListener ? (StateModelEventListener) persistenceManager : null);
        ActionSelector actionSelector = CompoundFactory.getCompoundActionSelector();

        return new StateModelManager(abstractStateModel, actionSelector, persistenceManager, concreteStateTags, sequenceManager);
    }

}
