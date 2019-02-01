package nl.ou.testar.StateModel.Persistence;

import nl.ou.testar.StateModel.*;

public interface PersistenceManager {

    String DATA_STORE_MODE_INSTANT = "instant";

    String DATA_STORE_MODE_DELAYED = "delayed";

    /**
     * This method persists an entire state model.
     * @param abstractStateModel
     */
    void persistAbstractStateModel(AbstractStateModel abstractStateModel);

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

}
