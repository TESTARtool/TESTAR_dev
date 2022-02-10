package org.testar.StateModel.ActionSelection;

import org.testar.StateModel.AbstractAction;
import org.testar.StateModel.AbstractState;
import org.testar.StateModel.AbstractStateModel;
import org.testar.StateModel.Exception.ActionNotFoundException;

public interface ActionSelector {

    /**
     * This method returns an action to execute
     * @param currentState
     * @param abstractStateModel
     * @return
     */
    public AbstractAction selectAction(AbstractState currentState, AbstractStateModel abstractStateModel) throws ActionNotFoundException;

}
