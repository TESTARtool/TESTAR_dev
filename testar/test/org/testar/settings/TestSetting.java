package org.testar.settings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TestSetting implements Serializable, Comparable<TestSetting> {

    static final String DEFAULT_VALUE = "Default";
    public String value;

    public static TestSetting CreateDefault() {
        TestSetting DefaultInstance = new TestSetting();
        DefaultInstance.value = DEFAULT_VALUE;
        return DefaultInstance;
    }

    @Override
    public int compareTo(TestSetting other) {
        int res = -1;
        if (this.value.equals(other.value)) {
            res = 0;
        }
        return res;
    }
}
