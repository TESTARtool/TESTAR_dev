package org.testar.statemodel.actionselector;

import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.exceptions.ActionNotFoundException;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class RandomActionSelector implements ActionSelector{

	@Override
	public void notifyNewSequence() {
		//
	}

    @Override
    public AbstractAction selectAction(AbstractState currentState, AbstractStateModel abstractStateModel) throws ActionNotFoundException {
        long graphTime = System.currentTimeMillis();
        Random rnd = new Random(graphTime);
        Set<String> actionIds = currentState.getActionIds();
        String actionId  = (new ArrayList<>(actionIds)).get(rnd.nextInt(actionIds.size()));
        return currentState.getAction(actionId);
    }
}
