package nl.ou.testar.visualvalidation.extractor;

import org.apache.commons.lang.ObjectUtils;
import org.testar.settings.ExtendedSettingBase;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class WidgetTextConfiguration extends ExtendedSettingBase<WidgetTextConfiguration> {
    List<WidgetTextSetting> widget;

    public static WidgetTextConfiguration CreateDefault() {
        WidgetTextConfiguration instance = new WidgetTextConfiguration();

        WidgetTextSetting scrollbar = WidgetTextSetting.CreateIgnore("UIAScrollBar");
        WidgetTextSetting menuBar = WidgetTextSetting.CreateIgnore("UIAMenuBar");
        WidgetTextSetting textEdit = WidgetTextSetting.CreateExtract("UIAEdit", "UIAValueValue");
        WidgetTextSetting icon = WidgetTextSetting.CreateIgnoreAncestorBased("UIAMenuItem",
                Arrays.asList("UIAMenuBar", "UIATitleBar", "UIAWindow", "Process"));

        instance.widget = new ArrayList<>(Arrays.asList(scrollbar, menuBar, textEdit, icon));
        return instance;
    }

    @Override
    public int compareTo(WidgetTextConfiguration other) {
        int result = -1;

        if (widget.size() == other.widget.size()) {
            for (int i = 0; i < widget.size(); i++) {
                int index = i;
                if (other.widget.stream().noneMatch(it -> it.compareTo(widget.get(index)) == 0)){
                    return result;
                }
            }
            return 0;
        }
        return result;
    }
}
