package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.Color;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.testar.OutputStructure;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Set;

public class BorjaReward4 implements RewardFunction {

    /**
     *{@inheritDoc}
     */
    @Override
    public float getReward(State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final AbstractAction executedAction, Set<Action> actions) {
        float totalReward = 0f;
        
        if(executedAction.getAttributes().get(RLTags.QBorja, 0.0) == 0.0) {
			executedAction.addAttribute(RLTags.QBorja, 1.0);
		}
        
        totalReward += executedAction.getAttributes().get(RLTags.QBorja, 0.0);
        return totalReward;
    }
}
