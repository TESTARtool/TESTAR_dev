/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

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
import org.testar.monkey.alayer.TaggableBase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StateModelManagerFactory {

    public static StateModelManager getStateModelManager(String applicationName, String applicationVersion, TaggableBase configTags) {
        return createStateModelManager(applicationName, applicationVersion, configTags, false);
    }

    public static StateModelManager getStateModelManager(String applicationName, String applicationVersion, TaggableBase configTags, boolean listeningMode) {
        return createStateModelManager(applicationName, applicationVersion, configTags, listeningMode);
    }

    public static StateModelManager createStateModelManager(String applicationName, String applicationVersion, TaggableBase configTags, boolean listeningMode) {
        // first check if the state model module is enabled
        if(!configTags.get(StateModelTags.StateModelEnabled)) {
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
        }
        else {
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

        if(listeningMode) {
            System.out.println("TESTAR State Model enabled with Listening mode... AbstractStateModelListener");

            // create the abstract state model and then the state model manager
            AbstractStateModelListener abstractStateModelListener = new AbstractStateModelListener(modelIdentifier,
                    applicationName,
                    applicationVersion,
                    abstractTags,
                    persistenceManager instanceof StateModelEventListener ? (StateModelEventListener) persistenceManager : null);
            ActionSelector actionSelector = CompoundFactory.getCompoundActionSelector(configTags);

            // should we store widgets?
            boolean storeWidgets = configTags.get(StateModelTags.StateModelStoreWidgets);

            return new ModelManagerListeningMode(abstractStateModelListener, actionSelector, persistenceManager, sequenceManager, storeWidgets);

        }

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
