package org.testar.statemodel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.CodingManager;
import org.testar.reinforcementlearning.actionselectors.ReinforcementLearningActionSelector;
import org.testar.reinforcementlearning.policies.PolicyFactory;
import org.testar.reinforcementlearning.qfunctions.QFunction;
import org.testar.reinforcementlearning.qfunctions.QFunctionFactory;
import org.testar.reinforcementlearning.rewardfunctions.RewardFunction;
import org.testar.reinforcementlearning.rewardfunctions.RewardFunctionFactory;
import org.testar.reinforcementlearning.utils.ReinforcementLearningUtil;
import org.testar.statemodel.actionselector.ActionSelector;
import org.testar.statemodel.actionselector.CompoundFactory;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.statemodel.persistence.PersistenceManager;
import org.testar.statemodel.persistence.PersistenceManagerFactory;
import org.testar.statemodel.persistence.PersistenceManagerFactoryBuilder;
import org.testar.statemodel.reinforcementlearning.QLearningModelManager;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StateModelManagerFactory {

    private static final Logger logger = LogManager.getLogger(StateModelManagerFactory.class);

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

//        // create the abstract state model and then the state model manager
//        AbstractStateModel abstractStateModel = new AbstractStateModel(modelIdentifier,
//                settings.get(ConfigTags.ApplicationName),
//                settings.get(ConfigTags.ApplicationVersion),
//                abstractTags,
//                persistenceManager instanceof StateModelEventListener ? (StateModelEventListener) persistenceManager : null);
//        ActionSelector actionSelector = CompoundFactory.getCompoundActionSelector(settings);

        // should we store widgets?
        boolean storeWidgets = settings.get(ConfigTags.StateModelStoreWidgets);

        // create the abstract state model and then the state model manager
        AbstractStateModelReinforcementLearning abstractStateModel = new AbstractStateModelReinforcementLearning(modelIdentifier,
                settings.get(ConfigTags.ApplicationName),
                settings.get(ConfigTags.ApplicationVersion),
                abstractTags,
                persistenceManager != null ? (StateModelEventListener) persistenceManager : null);
        String stateModelRL = settings.get(ConfigTags.StateModelReinforcementLearningEnabled, "");

        if (!stateModelRL.equals("")) {
            Tag<Float> tag = ReinforcementLearningUtil.getTag(settings);
            final ActionSelector actionSelector = new ReinforcementLearningActionSelector(PolicyFactory.getPolicy(settings)) ;

            final RewardFunction rewardFunction = RewardFunctionFactory.getRewardFunction(settings);
            final QFunction qFunction = QFunctionFactory.getQFunction(settings);

//            logger.info("State model with Reinforcement Learning Model Manager selected");
            logger.info("State model with QLearningModelManager selected");
            return new QLearningModelManager(abstractStateModel,
                    actionSelector,
                    persistenceManager,
                    concreteStateTags,
                    sequenceManager,
                    storeWidgets,
                    rewardFunction,
                    qFunction,
                    tag);
        }
        ActionSelector actionSelector = CompoundFactory.getCompoundActionSelector(settings);

        logger.info("State model with modelManager selected");
        return new ModelManager(abstractStateModel,
                actionSelector,
                persistenceManager,
                concreteStateTags,
                sequenceManager,
                storeWidgets);
    }


}
