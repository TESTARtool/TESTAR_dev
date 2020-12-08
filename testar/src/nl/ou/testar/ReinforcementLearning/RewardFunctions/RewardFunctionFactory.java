package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

public class RewardFunctionFactory {

    private static final Logger logger = LogManager.getLogger(RewardFunctionFactory.class);

    public static RewardFunction getRewardFunction (final Settings settings) {
        final String rewardFunction = settings.get(ConfigTags.RewardFunction, "");
        final RewardFunction selectedRewardFunction;

        switch(rewardFunction) {
            case "WidgetTreeBasedRewardFunction":
                selectedRewardFunction = new WidgetTreeZhangShashaBasedRewardFunction(new LRKeyrootsHelper(), new TreeDistHelper());
                break;
            case "ImageRecognitionBasedRewardFunction":
                final float defaultReward = settings.get(ConfigTags.DefaultReward, 1.0f);
                selectedRewardFunction = new ImageRecognitionBasedRewardFunction(defaultReward);
                break;
            default:
                selectedRewardFunction = new CounterBasedRewardFunction();
        }

        logger.info("Using rewardFunction='{}'", selectedRewardFunction.getClass().getName());

        return selectedRewardFunction;
    }
}
