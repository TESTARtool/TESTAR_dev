package nl.ou.testar.ReinforcementLearning.Policies;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

public class PolicyFactory {

    public static Policy getPolicy(final Settings settings){
        final String policy = settings.get(ConfigTags.Policy, "");
        final Policy selectedPolicy;

        switch (policy) {
            case "EpsilonGreedyPolicy":
                selectedPolicy = getEpsilonGreedyPolicy(settings);
                break;
            case "OptimisticQValuesInitializationPolicy":
                selectedPolicy = getOptimisticQValuesInitializationPolicy(settings);
                break;
            case "EpsilonGreedyAndBoltzmannDistributedExplorationPolicy":
                selectedPolicy = getEpsilonGreedyAndBoltzmannDistributedExplorationPolicy(settings);
                break;
            case "BoltzmannDistributedExplorationPolicy":
                selectedPolicy = getBoltzmannDistributedExplorationPolicy(settings);
                break;
            default:
                selectedPolicy = getGreedyPolicy(settings);
        }

        System.out.println(String.format("Using policy=%s", selectedPolicy.getClass().getName()));

        return selectedPolicy;
    }

    private static Policy getEpsilonGreedyPolicy(final Settings settings) {
        final float epsilon = settings.get(ConfigTags.Epsilon, 0.7f);
        final float defaultQValue = settings.get(ConfigTags.DefaultValue, 0f);
        final String tag = settings.get(ConfigTags.TagName, "qvalue");
        return new EpsilonGreedyPolicy(new GreedyPolicy(defaultQValue), epsilon);
    }

    private static Policy getGreedyPolicy(final Settings settings) {
        final float defaultQValue = settings.get(ConfigTags.DefaultValue, 0f);
        final String tag = settings.get(ConfigTags.TagName, "qvalue");
        return new GreedyPolicy(defaultQValue);
    }

    private static Policy getOptimisticQValuesInitializationPolicy(final Settings settings) {
        final float defaultQValue = settings.get(ConfigTags.DefaultValue, 0f);
        final float maxQValue = settings.get(ConfigTags.MaxQValue, 0f);
        final String tag = settings.get(ConfigTags.TagName, "qvalue");
        return new OptimisticQValuesInitializationPolicy(new GreedyPolicy(defaultQValue), maxQValue);
    }

    private static Policy getEpsilonGreedyAndBoltzmannDistributedExplorationPolicy(final Settings settings) {
        final float defaultQValue = settings.get(ConfigTags.DefaultValue, 0f);
        final float decayRate = settings.get(ConfigTags.DecayRate, 0.0001f);
        final float temperature = settings.get(ConfigTags.Temperature, 1.0f);
        final float epsilon = settings.get(ConfigTags.Epsilon, 0.7f);
        final String tag = settings.get(ConfigTags.TagName, "qvalue");
        return new EpsilonGreedyAndBoltzmannDistributedExplorationPolicy(new GreedyPolicy(defaultQValue), new BoltzmannDistributedExplorationPolicy(decayRate, temperature), epsilon);
    }

    private static BoltzmannDistributedExplorationPolicy getBoltzmannDistributedExplorationPolicy(final Settings settings) {
        final float decayRate = settings.get(ConfigTags.DecayRate, 0.0001f);
        final float temperature = settings.get(ConfigTags.Temperature, 1.0f);
        return new BoltzmannDistributedExplorationPolicy(decayRate, temperature);
    }
}
