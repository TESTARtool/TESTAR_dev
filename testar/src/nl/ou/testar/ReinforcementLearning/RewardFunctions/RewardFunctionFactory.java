package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

public class RewardFunctionFactory {

    public static RewardFunction getRewardFunction (final Settings settings) {
        final String rewardFunction = settings.get(ConfigTags.RewardFunction, "");
        final RewardFunction selectedRewardFunction;

        switch(rewardFunction) {
            case "WidgetTreeBasedRewardFunction":
                selectedRewardFunction = new WidgetTreeZhangShashaBasedRewardFunction(new LRKeyrootsHelper());
                break;
            case "ImageRecognitionBasedRewardFunction":
                final float defaultReward = settings.get(ConfigTags.DefaultReward, 1.0f);
                selectedRewardFunction = new ImageRecognitionBasedRewardFunction(defaultReward);
                break;
            case "ABTBasedRewardFunction":
                selectedRewardFunction = new ABTBasedRewardFunction();
                break;
            case "BorjaReward4":
                selectedRewardFunction = new BorjaReward4();
                break;
            default:
                selectedRewardFunction = new CounterBasedRewardFunction();
        }

        System.out.printf("Using rewardFunction='%S'", selectedRewardFunction.getClass().getName());

        return selectedRewardFunction;
    }
}
