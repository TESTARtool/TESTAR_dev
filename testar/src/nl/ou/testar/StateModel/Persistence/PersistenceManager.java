package nl.ou.testar.StateModel.Persistence;

import nl.ou.testar.StateModel.*;
import nl.ou.testar.StateModel.Sequence.Sequence;
import nl.ou.testar.StateModel.Sequence.SequenceManager;
import nl.ou.testar.StateModel.Sequence.SequenceNode;
import nl.ou.testar.StateModel.Sequence.SequenceStep;

public interface PersistenceManager {

    // the data will be stored instantly as requests reach the  persistence manager
    String DATA_STORE_MODE_INSTANT = "instant";

    // the data will not be stored until a test sequence has finished
    String DATA_STORE_MODE_DELAYED = "delayed";

    // some data will be stored instantly and some will be stored after the sequence has finished
    String DATA_STORE_MODE_HYBRID = "hybrid";

    // sometimes we do not want to persist data
    String DATA_STORE_MODE_NONE = "none";

    /**
     * This method persists an entire state model.
     */
    void shutdown();

    /**
     * This method persists an abstract state.
     * @param abstractState
     */
    void persistAbstractState(AbstractState abstractState);

    /**
     * This method persists an abstract action
     * @param abstractAction
     */
    void persistAbstractAction(AbstractAction abstractAction);

    /**
     * This method persists an abstract state transition
     * @param abstractStateTransition
     */
    void persistAbstractStateTransition(AbstractStateTransition abstractStateTransition);

    /**
     * This method persists a concrete state.
     * @param concreteState
     */
    void persistConcreteState(ConcreteState concreteState);

    /**
     * This method persists a concrete action.
     * @param concreteStateTransition
     */
    void persistConcreteStateTransition(ConcreteStateTransition concreteStateTransition);

    /**
     * This method initializes and abstract state model before use in Testar.
     * @param abstractStateModel
     */
    void initAbstractStateModel(AbstractStateModel abstractStateModel);

    /**
     * This method persists a sequence to the orient data store.
     * @param sequence
     */
    void persistSequence(Sequence sequence);

    /**
     * This method initializes a sequence manager implementation with data from the data store.
     * @param sequenceManager
     */
    void initSequenceManager(SequenceManager sequenceManager);

    /**
     * This method persists a sequence node to the data store.
     * @param sequenceNode
     */
    void persistSequenceNode(SequenceNode sequenceNode);

    /**
     * This method persists a sequence step to the data store.
     * @param sequenceStep
     */
    void persistSequenceStep(SequenceStep sequenceStep);

    /**
     * This method returns true if the model is deterministic, meaning no transitions lead to more than one target state.
     * @param abstractStateModel
     * @return
     */
    boolean modelIsDeterministic(AbstractStateModel abstractStateModel);

    /**
     * This method returns the nr of non-deterministic actions in the model, meaning the same actions ends in more
     * than one unique abstract states.
     * @param abstractStateModel
     * @return
     */
    public int getNrOfNondeterministicActions(AbstractStateModel abstractStateModel);

}
