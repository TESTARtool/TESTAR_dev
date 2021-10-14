package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.RewardFunctions.Helpers.CompareScreenshotsByPixelsHelper;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;

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
	Function<State, AWTCanvas> getStateshotBinary = es.upv.staq.testar.ProtocolUtil::getStateshotBinary;
	private final CompareScreenshotsByPixelsHelper helper;

	public CompareScreenshotsByPixelsRewardFunction(CompareScreenshotsByPixelsHelper helper) {
		this.helper = helper;
	}

	@Override
    public float getReward(State state, final ConcreteState notUsedCurrentConcreteState, final AbstractState notUsedcurrentAbstractState, final Action notUsedExecutedAction, final AbstractAction notUsedExecutedAbstractAction, final AbstractAction notUsedSelectedAbstractAction, Set<Action> notUsedActions) {
		logger.info(". . . . . CompareScreenshotsByPixelsRewardFunction . . . . .");
		AWTCanvas currentStateCanvas = getStateshotBinary.apply(state);

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
