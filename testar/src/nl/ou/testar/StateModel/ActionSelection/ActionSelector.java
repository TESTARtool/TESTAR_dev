package nl.ou.testar.StateModel.ActionSelection;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.AbstractStateModel;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;

public interface ActionSelector {

    /**
     * This method returns an action to execute
     * @param currentState
     * @param abstractStateModel
     * @return
     */
    public AbstractAction selectAction(final AbstractState currentState, final AbstractStateModel abstractStateModel) throws ActionNotFoundException;

}
