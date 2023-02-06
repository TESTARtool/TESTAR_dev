package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.RewardFunctions.Helpers.CompareScreenshotsByPixelsHelper;
import org.testar.ProtocolUtil;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.ConcreteState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.alayer.AWTCanvas;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Set;
import java.util.function.Function;

/**
 * This reward function compares for two states the binary representations of their screenshots.

 * @author Borja
 */
public class CompareScreenshotsByPixelsRewardFunction implements RewardFunction {
    private static final Logger logger = LogManager.getLogger(CompareScreenshotsByPixelsRewardFunction.class);
    private final static float defaultReward = 0f;
    AWTCanvas previousStateCanvas = null;
    Function<State, AWTCanvas> getStateshotBinary = ProtocolUtil::getStateshotBinary;
    private final CompareScreenshotsByPixelsHelper helper;

    public CompareScreenshotsByPixelsRewardFunction(CompareScreenshotsByPixelsHelper helper) {
        this.helper = helper;
    }

    @Override
    public float getReward(State state, final ConcreteState notUsedCurrentConcreteState, final AbstractState notUsedcurrentAbstractState, final Action notUsedExecutedAction, final AbstractAction notUsedExecutedAbstractAction, final AbstractAction notUsedSelectedAbstractAction, Set<Action> notUsedActions) {
        logger.info(". . . . . CompareScreenshotsByPixelsRewardFunction . . . . .");
        AWTCanvas currentStateCanvas;
        try{
            currentStateCanvas = getStateshotBinary.apply(state);
        }
        catch (RuntimeException awte) {
            System.out.println("Error at currentStateCanvas");
            System.out.println(awte.getMessage());
            return defaultReward;
        }
        catch (OutOfMemoryError oome) {
            //Log the info
            System.err.println("Array size too large");
            System.err.println("Max JVM memory: " + Runtime.getRuntime().maxMemory());
            currentStateCanvas = null;
        }

        if (currentStateCanvas == null){
            return defaultReward;
        }

        if (previousStateCanvas == null) {
            previousStateCanvas = currentStateCanvas;
            return defaultReward;
        }

        float reward = helper.getDiffPxRatio(currentStateCanvas, previousStateCanvas, defaultReward);

        previousStateCanvas = currentStateCanvas;

        return reward;
    }

    @Override
    public void reset() {
        previousStateCanvas = null;
    }
}
