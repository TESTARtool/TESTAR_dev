package org.testar.settings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExampleSetting extends ExtendedSettingBase<ExampleSetting> {
    public String test;

    public static ExampleSetting CreateDefault() {
        ExampleSetting instance = new ExampleSetting();
        instance.test = "Hello";
        return instance;
    }

    @Override
    public int compareTo(ExampleSetting other) {
        if (test.contentEquals(other.test)){
            return 0;
        }
        return -1;
    }
}
