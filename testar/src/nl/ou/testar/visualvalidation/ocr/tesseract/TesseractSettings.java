package nl.ou.testar.visualvalidation.ocr.tesseract;

import org.testar.settings.ExtendedSettingBase;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TesseractSettings extends ExtendedSettingBase<TesseractSettings> {
    public String dataPath;
    public String language;

    public static TesseractSettings CreateDefault() {
        TesseractSettings instance = new TesseractSettings();
        instance.dataPath = System.getenv("LOCALAPPDATA") + "\\Tesseract-OCR\\tessdata";
        instance.language = "eng";
        return instance;
    }

    @Override
    public int compareTo(TesseractSettings other) {
        int result = -1;
        if ((language.contentEquals(other.language) && (dataPath.contentEquals(other.dataPath)))) {
            result = 0;
        }
        return result;
    }
}
