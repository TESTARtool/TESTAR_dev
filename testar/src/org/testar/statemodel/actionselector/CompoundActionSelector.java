package org.testar.statemodel.actionselector;

import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.exceptions.ActionNotFoundException;

import java.util.List;

public class CompoundActionSelector implements ActionSelector{

    private List<ActionSelector> selectors;

    public CompoundActionSelector(List<ActionSelector> selectors) {
        this.selectors = selectors;
    }

    @Override
    public void notifyNewSequence() {
    	selectors.forEach(selector -> selector.notifyNewSequence());
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
