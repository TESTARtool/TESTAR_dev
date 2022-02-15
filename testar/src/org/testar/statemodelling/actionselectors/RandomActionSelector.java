package org.testar.statemodelling.actionselectors;

import org.testar.statemodelling.AbstractAction;
import org.testar.statemodelling.AbstractState;
import org.testar.statemodelling.AbstractStateModel;
import org.testar.statemodelling.exception.ActionNotFoundException;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class RandomActionSelector implements ActionSelector{

    @Override
    public AbstractAction selectAction(AbstractState currentState, AbstractStateModel abstractStateModel) throws ActionNotFoundException {
        long graphTime = System.currentTimeMillis();
        Random rnd = new Random(graphTime);
        Set<String> actionIds = currentState.getActionIds();
        String actionId  = (new ArrayList<>(actionIds)).get(rnd.nextInt(actionIds.size()));
        return currentState.getAction(actionId);
    }
}
