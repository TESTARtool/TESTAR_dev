package nl.ou.testar.ReinforcementLearning;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.testar.settings.ExtendedSettingBase;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ReinforcementLearningSettings extends ExtendedSettingBase<ReinforcementLearningSettings> {
    public Float alpha;
    public Float gamma;
    public Float defaultValue;
    public Float epsilon;
    public Float minEpsilon;
    public Float maxEpsilon;
    public Integer totalActions;
    public Float defaultReward;
    public String rewardFunction;
    public String policy;
    public Float decayRate;
    public Float temperature;
    public Float maxQValue;
    public String tagName;
    public String qFunction;

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
                && this.maxQValue.equals(other.maxQValue)
                && this.tagName.equals(other.tagName)
                && this.qFunction.equals(other.qFunction)) {
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
                "tagName=" + tagName +
                "qFunction=" + qFunction +
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
        DefaultInstance.tagName = "qvalue";
        DefaultInstance.qFunction = "QlearningFunction";
        return DefaultInstance;
    }
    
    /**
     * Update ReinforcementLearning ConfigTags settings with XML framework values, 
     * and change the Application Name adding the RL Extended Settings
     * 
     * @param settings
     * @return updated settings
     */
    public Settings updateXMLSettings(Settings settings) {
        System.out.println("Reinforcement Learning Debugging ExtendedSettingsFile: " + settings.get(ConfigTags.ExtendedSettingsFile, ""));
        settings.set(ConfigTags.Alpha, this.alpha);
        System.out.println("Alpha value : " + settings.get(ConfigTags.Alpha));
        settings.set(ConfigTags.Gamma, this.gamma);
        System.out.println("Gamma value : " + settings.get(ConfigTags.Gamma));
        settings.set(ConfigTags.DefaultValue, this.defaultValue);
        System.out.println("DefaultValue value : " + settings.get(ConfigTags.DefaultValue));
        settings.set(ConfigTags.Epsilon, this.epsilon);
        System.out.println("Epsilon value : " + settings.get(ConfigTags.Epsilon));
        settings.set(ConfigTags.MinEpsilon, this.minEpsilon);
        System.out.println("MinEpsilon value : " + settings.get(ConfigTags.MinEpsilon));
        settings.set(ConfigTags.MaxEpsilon, this.maxEpsilon);
        System.out.println("MaxEpsilon value : " + settings.get(ConfigTags.MaxEpsilon));
        settings.set(ConfigTags.TotalActions, this.totalActions);
        System.out.println("TotalActions value : " + settings.get(ConfigTags.TotalActions));
        settings.set(ConfigTags.DefaultReward, this.defaultReward);
        System.out.println("DefaultReward value : " + settings.get(ConfigTags.DefaultReward));
        settings.set(ConfigTags.RewardFunction, this.rewardFunction);
        System.out.println("RewardFunction value : " + settings.get(ConfigTags.RewardFunction));
        settings.set(ConfigTags.Policy, this.policy);
        System.out.println("Policy value : " + settings.get(ConfigTags.Policy));
        settings.set(ConfigTags.DecayRate, this.decayRate);
        System.out.println("DecayRate value : " + settings.get(ConfigTags.DecayRate));
        settings.set(ConfigTags.Temperature, this.temperature);
        System.out.println("Temperature value : " + settings.get(ConfigTags.Temperature));
        settings.set(ConfigTags.MaxQValue, this.maxQValue);
        System.out.println("MaxQValue value : " + settings.get(ConfigTags.MaxQValue));
        settings.set(ConfigTags.TagName, this.tagName);
        System.out.println("TagName value : " + settings.get(ConfigTags.TagName));
        settings.set(ConfigTags.QFunction, this.qFunction);
        System.out.println("QFunction value : " + settings.get(ConfigTags.QFunction));

        // Force new Application Name adding the RL Extended Settings
        // This is being done to differentiate experiments results
        String extendedSettingFileName = new File(settings.get(ConfigTags.ExtendedSettingsFile)).getName().replace(".xml", "");
        String forceRLName = settings.get(ConfigTags.ApplicationName, "") + "_" + extendedSettingFileName;
        settings.set(ConfigTags.ApplicationName, forceRLName);
        System.out.println("ApplicationName : " + settings.get(ConfigTags.ApplicationName));
        
        return settings;
    }

}
