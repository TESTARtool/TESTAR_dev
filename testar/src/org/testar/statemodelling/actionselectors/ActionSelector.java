package org.testar.statemodelling.actionselectors;

import org.testar.statemodelling.AbstractAction;
import org.testar.statemodelling.AbstractState;
import org.testar.statemodelling.AbstractStateModel;
import org.testar.statemodelling.exception.ActionNotFoundException;

public interface ActionSelector {

    /**
     * This method returns an action to execute
     * @param currentState
     * @param abstractStateModel
     * @return
     */
    public AbstractAction selectAction(AbstractState currentState, AbstractStateModel abstractStateModel) throws ActionNotFoundException;

}
