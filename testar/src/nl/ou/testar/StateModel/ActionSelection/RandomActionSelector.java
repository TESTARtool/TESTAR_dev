package nl.ou.testar.StateModel.ActionSelection;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.AbstractStateModel;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import org.fruit.alayer.Action;

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
