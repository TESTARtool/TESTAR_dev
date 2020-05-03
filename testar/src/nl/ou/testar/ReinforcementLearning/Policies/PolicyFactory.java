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

        return selectedPolicy;
    }

    private static Policy getEpsilonGreedyPolicy(final Settings settings) {
        final float epsilon = settings.get(ConfigTags.Epsilon, 0.7f);
        final double defaultQValue = settings.get(ConfigTags.DefaultValue, 0.0d);
        return new EpsilonGreedyPolicy(new GreedyPolicy(defaultQValue), epsilon);
    }

    private static Policy getGreedyPolicy(final Settings settings) {
        final double defaultQValue = settings.get(ConfigTags.DefaultValue, 0.0d);
        return new GreedyPolicy(defaultQValue);
    }

    private static Policy getOptimisticQValuesInitializationPolicy(final Settings settings) {
        final double defaultQValue = settings.get(ConfigTags.DefaultValue, 0.0d);
        final double maxQValue = settings.get(ConfigTags.MaxQValue, 1.0d);
        return new OptimisticQValuesInitializationPolicy(new GreedyPolicy(defaultQValue), maxQValue);
    }

    private static Policy getEpsilonGreedyAndBoltzmannDistributedExplorationPolicy(final Settings settings) {
        final double defaultQValue = settings.get(ConfigTags.DefaultValue, 0.0d);
        final double decayRate = settings.get(ConfigTags.DecayRate, 0.0001d);
        final double temperature = settings.get(ConfigTags.Temperature, 1.0d);
        final float epsilon = settings.get(ConfigTags.Epsilon, 0.7f);
        return new EpsilonGreedyAndBoltzmannDistributedExplorationPolicy(new GreedyPolicy(defaultQValue), new BoltzmannDistributedExplorationPolicy(decayRate, temperature), epsilon);
    }

    private static BoltzmannDistributedExplorationPolicy getBoltzmannDistributedExplorationPolicy(final Settings settings) {
        final double decayRate = settings.get(ConfigTags.DecayRate, 0.0001d);
        final double temperature = settings.get(ConfigTags.Temperature, 1.0d);
        return new BoltzmannDistributedExplorationPolicy(decayRate, temperature);
    }
}
