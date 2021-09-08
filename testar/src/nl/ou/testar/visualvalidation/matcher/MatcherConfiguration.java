package nl.ou.testar.visualvalidation.matcher;

import org.testar.settings.ExtendedSettingBase;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class MatcherConfiguration extends ExtendedSettingBase<MatcherConfiguration> {
    public Integer locationMatchMargin;
    boolean loggingEnabled;
    public Integer failedToMatchPercentageThreshold;

    @Override
    public String toString() {
        return "OcrConfiguration{" +
                "margin=" + locationMatchMargin +
                '}';
    }

    public static MatcherConfiguration CreateDefault() {
        MatcherConfiguration instance = new MatcherConfiguration();
        instance.locationMatchMargin = 0;
        instance.loggingEnabled = false;
        instance.failedToMatchPercentageThreshold = 75;
        return instance;
    }

    @Override
    public int compareTo(MatcherConfiguration other) {
        int result = -1;
        if (locationMatchMargin.equals(other.locationMatchMargin) &&
                (loggingEnabled == other.loggingEnabled) &&
                (Objects.equals(failedToMatchPercentageThreshold, other.failedToMatchPercentageThreshold))
        ) {
            result = 0;
        }
        return result;
    }
}
