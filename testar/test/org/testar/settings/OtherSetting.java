package org.testar.settings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OtherSetting implements Serializable, Comparable<OtherSetting> {
    static final int DEFAULT_VALUE = 5;
    public int speed;

    public static OtherSetting CreateDefault() {
        OtherSetting DefaultInstance = new OtherSetting();
        DefaultInstance.speed = DEFAULT_VALUE;
        return DefaultInstance;
    }

    @Override
    public int compareTo(OtherSetting other) {
        int res = -1;
        if (this.speed == other.speed) {
            res = 0;
        }
        return res;
    }
}

