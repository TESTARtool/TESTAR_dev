package nl.ou.testar.StateModel.ActionSelection;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.AbstractStateModel;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;

import java.util.List;

public class CompoundActionSelector implements ActionSelector{

    private List<ActionSelector> selectors;

    public CompoundActionSelector(List<ActionSelector> selectors) {
        this.selectors = selectors;
    }

    @Override
    public AbstractAction selectAction(AbstractState currentState, AbstractStateModel abstractStateModel) throws ActionNotFoundException {
        for(ActionSelector selector:selectors) {
            try {
                return selector.selectAction(currentState, abstractStateModel);
            }
            catch (ActionNotFoundException ex) {
                //@todo maybe some logging here later?
            }
        }
        throw new ActionNotFoundException();
    }
}
