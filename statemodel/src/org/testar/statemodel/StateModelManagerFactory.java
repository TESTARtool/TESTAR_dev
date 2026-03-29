/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel;

import org.testar.config.StateModelTags;
import org.testar.core.CodingManager;
import org.testar.statemodel.actionselector.ActionSelector;
import org.testar.statemodel.actionselector.CompoundFactory;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.statemodel.persistence.PersistenceManager;
import org.testar.statemodel.persistence.PersistenceManagerFactory;
import org.testar.statemodel.persistence.PersistenceManagerFactoryBuilder;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.core.tag.Tag;
import org.testar.core.tag.TaggableBase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StateModelManagerFactory {

    public static StateModelManager getStateModelManager(String applicationName, String applicationVersion, TaggableBase configTags) {
        // first check if the state model module is enabled
        if (!configTags.get(StateModelTags.StateModelEnabled)) {
            return new DummyModelManager();
        }

        Set<Tag<?>> abstractTags = Arrays.stream(CodingManager.getCustomTagsForAbstractId()).collect(Collectors.toSet());
        if (abstractTags.isEmpty()) {
            throw new IllegalArgumentException("No Abstract State Attributes were provided in the settings file");
        }

        // get a persistence manager
        PersistenceManagerFactoryBuilder.ManagerType managerType;
        if (configTags.get(StateModelTags.DataStoreMode).equals(PersistenceManager.DATA_STORE_MODE_NONE)) {
            managerType = PersistenceManagerFactoryBuilder.ManagerType.DUMMY;
        } else {
            managerType = PersistenceManagerFactoryBuilder.ManagerType.valueOf(configTags.get(StateModelTags.DataStore).toUpperCase());
        }
        PersistenceManagerFactory persistenceManagerFactory = PersistenceManagerFactoryBuilder.createPersistenceManagerFactory(managerType);
        PersistenceManager persistenceManager = persistenceManagerFactory.getPersistenceManager(configTags);

        // get the abstraction level identifier that uniquely identifies the state model we are testing against.
        String modelIdentifier = CodingManager.getAbstractStateModelHash(applicationName, applicationVersion);

        // we need a sequence manager to record the sequences
        Set<StateModelEventListener> eventListeners = new HashSet<>();
        eventListeners.add((StateModelEventListener) persistenceManager);
        SequenceManager sequenceManager = new SequenceManager(eventListeners, modelIdentifier);

        // create the abstract state model and then the state model manager
        AbstractStateModel abstractStateModel = new AbstractStateModel(modelIdentifier,
                applicationName,
                applicationVersion,
                abstractTags,
                persistenceManager instanceof StateModelEventListener ? (StateModelEventListener) persistenceManager : null);
        ActionSelector actionSelector = CompoundFactory.getCompoundActionSelector(configTags);

        // should we store widgets?
        boolean storeWidgets = configTags.get(StateModelTags.StateModelStoreWidgets);

        return new ModelManager(abstractStateModel, actionSelector, persistenceManager, sequenceManager, storeWidgets);
    }

}
