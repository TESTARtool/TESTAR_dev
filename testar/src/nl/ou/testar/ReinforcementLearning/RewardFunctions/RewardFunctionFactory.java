package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

public class RewardFunctionFactory {

    public static RewardFunction getRewardFunction (final Settings settings) {
        final String rewardFunction = settings.get(ConfigTags.RewardFunction, "");
        final RewardFunction selectedRewardFunction;

        switch(rewardFunction) {
            case "WidgetTreeBasedRewardFunction":
                selectedRewardFunction = new WidgetTreeBasedRewardFunction();
                break;
            case "ImageRecognitionBasedRewardFunction":
                final double defaultReward = settings.get(ConfigTags.DefaultReward, 1.0d);
                selectedRewardFunction = new ImageRecognitionBasedRewardFunction(defaultReward);
                break;
            default:
                selectedRewardFunction = new CounterBasedRewardFunction();
        }

        System.out.printf("Using rewardFunction='%S'", selectedRewardFunction.getClass().getName());

        return selectedRewardFunction;
    }
}
