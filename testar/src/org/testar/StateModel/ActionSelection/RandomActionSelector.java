package org.testar.StateModel.ActionSelection;

import org.testar.StateModel.AbstractAction;
import org.testar.StateModel.AbstractState;
import org.testar.StateModel.AbstractStateModel;
import org.testar.StateModel.Exception.ActionNotFoundException;

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
