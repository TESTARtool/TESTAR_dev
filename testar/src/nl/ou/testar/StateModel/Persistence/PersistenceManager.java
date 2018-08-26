package nl.ou.testar.StateModel.Persistence;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.AbstractStateModel;
import nl.ou.testar.StateModel.AbstractStateTransition;

public interface PersistenceManager {

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

}
