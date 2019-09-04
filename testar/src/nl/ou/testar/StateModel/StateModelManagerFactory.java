package nl.ou.testar.StateModel;

import es.upv.staq.testar.CodingManager;
import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.ActionSelection.CompoundFactory;
import nl.ou.testar.StateModel.Event.StateModelEventListener;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Persistence.PersistenceManagerFactory;
import nl.ou.testar.StateModel.Persistence.PersistenceManagerFactoryBuilder;
import nl.ou.testar.StateModel.Sequence.SequenceManager;
import org.fruit.alayer.Tag;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StateModelManagerFactory {

    public static StateModelManager getStateModelManager(Settings settings) {
        // first check if the state model module is enabled
        if(!settings.get(ConfigTags.StateModelEnabled)) {
            return new DummyModelManager();
        }

        Set<Tag<?>> abstractTags = Arrays.stream(CodingManager.getCustomTagsForAbstractId()).collect(Collectors.toSet());
        if (abstractTags.isEmpty()) {
            throw new RuntimeException("No Abstract State Attributes were provided in the settings file");
        }

        Set<Tag<?>> concreteStateTags = Arrays.stream(CodingManager.getCustomTagsForConcreteId()).collect(Collectors.toSet());
        if (concreteStateTags.isEmpty()) {
            throw new RuntimeException("No concrete State Attributes were provided in the settings file");
        }

        // get a persistence manager
        PersistenceManagerFactoryBuilder.ManagerType managerType;
        if (settings.get(ConfigTags.DataStoreMode).equals(PersistenceManager.DATA_STORE_MODE_NONE)) {
            managerType = PersistenceManagerFactoryBuilder.ManagerType.DUMMY;
        }
        else {
            managerType = PersistenceManagerFactoryBuilder.ManagerType.valueOf(settings.get(ConfigTags.DataStore).toUpperCase());
        }
        PersistenceManagerFactory persistenceManagerFactory = PersistenceManagerFactoryBuilder.createPersistenceManagerFactory(managerType);
        PersistenceManager persistenceManager = persistenceManagerFactory.getPersistenceManager(settings);

        // get the abstraction level identifier that uniquely identifies the state model we are testing against.
        String modelIdentifier = CodingManager.getAbstractStateModelHash(settings.get(ConfigTags.ApplicationName),
                settings.get(ConfigTags.ApplicationVersion));

        // we need a sequence manager to record the sequences
        Set<StateModelEventListener> eventListeners = new HashSet<>();
        eventListeners.add((StateModelEventListener) persistenceManager);
        SequenceManager sequenceManager = new SequenceManager(eventListeners, modelIdentifier);

        // create the abstract state model and then the state model manager
        AbstractStateModel abstractStateModel = new AbstractStateModel(modelIdentifier,
                settings.get(ConfigTags.ApplicationName),
                settings.get(ConfigTags.ApplicationVersion),
                abstractTags,
                persistenceManager instanceof StateModelEventListener ? (StateModelEventListener) persistenceManager : null);
        ActionSelector actionSelector = CompoundFactory.getCompoundActionSelector(settings);

        return new ModelManager(abstractStateModel, actionSelector, persistenceManager, concreteStateTags, sequenceManager);
    }

}
