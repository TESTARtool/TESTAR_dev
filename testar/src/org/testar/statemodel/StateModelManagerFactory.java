package org.testar.statemodel;

import org.testar.CodingManager;
import org.testar.statemodel.actionselector.ActionSelector;
import org.testar.statemodel.actionselector.CompoundFactory;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.statemodel.persistence.PersistenceManager;
import org.testar.statemodel.persistence.PersistenceManagerFactory;
import org.testar.statemodel.persistence.PersistenceManagerFactoryBuilder;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.monkey.RuntimeControlsProtocol.Modes;

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

        if(settings.get(ConfigTags.Mode) == Modes.Listening) {
        	System.out.println("TESTAR State Model enabled with Listening mode... AbstractStateModelListener");

        	// create the abstract state model and then the state model manager
        	AbstractStateModelListener abstractStateModelListener = new AbstractStateModelListener(modelIdentifier,
        			settings.get(ConfigTags.ApplicationName),
        			settings.get(ConfigTags.ApplicationVersion),
        			abstractTags,
        			persistenceManager instanceof StateModelEventListener ? (StateModelEventListener) persistenceManager : null);
        	ActionSelector actionSelector = CompoundFactory.getCompoundActionSelector(settings);

        	// should we store widgets?
        	boolean storeWidgets = settings.get(ConfigTags.StateModelStoreWidgets);

        	return new ModelManagerListeningMode(abstractStateModelListener, actionSelector, persistenceManager, concreteStateTags, sequenceManager, storeWidgets);

        }

        // create the abstract state model and then the state model manager
        AbstractStateModel abstractStateModel = new AbstractStateModel(modelIdentifier,
                settings.get(ConfigTags.ApplicationName),
                settings.get(ConfigTags.ApplicationVersion),
                abstractTags,
                persistenceManager instanceof StateModelEventListener ? (StateModelEventListener) persistenceManager : null);
        ActionSelector actionSelector = CompoundFactory.getCompoundActionSelector(settings);

        // should we store widgets?
        boolean storeWidgets = settings.get(ConfigTags.StateModelStoreWidgets);

        return new ModelManager(abstractStateModel, actionSelector, persistenceManager, concreteStateTags, sequenceManager, storeWidgets);
    }

}
