package nl.ou.testar.visualvalidation;

import org.testar.settings.ExtendedSettingBase;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VisualValidationSettings extends ExtendedSettingBase<VisualValidationSettings> {
    public Boolean enabled;

    @Override
    public int compareTo(VisualValidationSettings other) {
        int res = -1;
        if (this.enabled.equals(other.enabled)) {
            res = 0;
        }
        return res;
    }

    @Override
    public String toString() {
        return "VisualValidationSettings{" +
                "enabled=" + enabled +
                '}';
    }

    public static VisualValidationSettings CreateDefault() {
        VisualValidationSettings DefaultInstance = new VisualValidationSettings();
        DefaultInstance.enabled = false;
        return DefaultInstance;
    }
}
