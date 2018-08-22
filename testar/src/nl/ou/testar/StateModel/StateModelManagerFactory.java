package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.ActionSelection.CompoundFactory;

public class StateModelManagerFactory {

    public static StateModelManager getStateModelManager() {
        // simple creation logic for now
        //@todo replace this hash with a real one later
        String abstractionHash = "12345679";
        AbstractStateModel abstractStateModel = new AbstractStateModel(abstractionHash);
        ActionSelector actionSelector = CompoundFactory.getCompoundActionSelector();
        return new StateModelManager(abstractStateModel, actionSelector);
    }

}
