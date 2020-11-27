package nl.ou.testar.visualvalidation.ocr;

import org.testar.settings.ExtendedSettingBase;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class OcrConfiguration extends ExtendedSettingBase<OcrConfiguration> {
    public static final String TESSERACT_ENGINE = "tesseract";

    public Boolean enabled;
    public String engine;

    @Override
    public String toString() {
        return "OcrConfiguration{" +
                "enabled=" + enabled +
                ", engine='" + engine + '\'' +
                '}';
    }

    public static OcrConfiguration CreateDefault() {
        OcrConfiguration instance = new OcrConfiguration();
        instance.enabled = true;
        instance.engine = TESSERACT_ENGINE;
        return instance;
    }

    @Override
    public int compareTo(OcrConfiguration other) {
        int result = -1;
        if ((enabled.equals(other.enabled)) &&
                (engine.contentEquals(other.engine))) {
            result = 0;
        }
        return result;
    }
}
