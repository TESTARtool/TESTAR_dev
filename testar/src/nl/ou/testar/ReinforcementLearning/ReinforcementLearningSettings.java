package nl.ou.testar.ReinforcementLearning;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.testar.settings.ExtendedSettingBase;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ReinforcementLearningSettings extends ExtendedSettingBase<ReinforcementLearningSettings> {
    public Float alpha;
    public Float gamma;
    public Float defaultValue;
    public Float epsilon;
    public Float defaultReward;
    public String rewardFunction;
    public String policy;
    public Float decayRate;
    public Float temperature;
    public Float maxQValue;

    @Override
    public int compareTo(ReinforcementLearningSettings other) {
        if (this.alpha.equals(other.alpha)
                && this.gamma.equals(other.gamma)
                && this.defaultValue.equals(other.defaultValue)
                && this.epsilon.equals(other.epsilon)
                && this.defaultReward.equals(other.defaultReward)
                && this.rewardFunction.equals(other.rewardFunction)
                && this.policy.equals(other.policy)
                && this.decayRate.equals(other.decayRate)
                && this.temperature.equals(other.temperature)
                && this.maxQValue.equals(other.maxQValue)) {
            return 0;
        }
        return -1;
    }

    @Override
    public String toString() {
        return "ReinforcementLearningSettings{" +
                "alpha=" + alpha +
                "gamma=" + gamma +
                "defaultValue=" + defaultValue +
                "epsilon=" + epsilon +
                "defaultReward=" + defaultReward +
                "rewardFunction=" + rewardFunction +
                "policy=" + policy +
                "decayRate=" + decayRate +
                "temperature=" + temperature +
                "maxQValue=" + maxQValue +
                '}';
    }

    public static ReinforcementLearningSettings CreateDefault() {
        ReinforcementLearningSettings DefaultInstance = new ReinforcementLearningSettings();
        DefaultInstance.alpha = (float)1.0;
        DefaultInstance.gamma = (float)0.99;
        DefaultInstance.defaultValue = (float)0.0;
        DefaultInstance.defaultReward = (float)0.0;
        DefaultInstance.epsilon = (float)0.7;
        DefaultInstance.maxQValue = (float)1.0;
        DefaultInstance.decayRate = (float)0.0001;
        DefaultInstance.temperature = (float)1.0;
        DefaultInstance.rewardFunction = "WidgetTreeBasedRewardFunction";
        DefaultInstance.policy = "EpsilonGreedyPolicy";
        return DefaultInstance;
    }

}
