package nl.ou.testar.StateModel.ActionSelection;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractStateModel;

public interface ActionSelector {

    /**
     * This method returns an action to execute
     * @param currentAction
     * @param abstractStateModel
     * @return
     */
    public AbstractAction selectAction(AbstractAction currentAction, AbstractStateModel abstractStateModel);

}
