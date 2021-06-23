package nl.ou.testar.visualvalidation.matcher;

import org.testar.settings.ExtendedSettingBase;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class MatcherConfiguration extends ExtendedSettingBase<MatcherConfiguration> {
    public Integer locationMatchMargin;

    @Override
    public String toString() {
        return "OcrConfiguration{" +
                "margin=" + locationMatchMargin +
                '}';
    }

    public static MatcherConfiguration CreateDefault() {
        MatcherConfiguration instance = new MatcherConfiguration();
        instance.locationMatchMargin = 0;
        return instance;
    }

    @Override
    public int compareTo(MatcherConfiguration other) {
        int result = -1;
        if (locationMatchMargin.equals(other.locationMatchMargin)) {
            result = 0;
        }
        return result;
    }
}
