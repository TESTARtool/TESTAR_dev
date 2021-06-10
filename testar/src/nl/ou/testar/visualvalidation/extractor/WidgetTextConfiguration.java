package nl.ou.testar.visualvalidation.extractor;

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
    boolean loggingEnabled;

    public static WidgetTextConfiguration CreateDefault() {
        WidgetTextConfiguration instance = new WidgetTextConfiguration();

        // Desktop protocol
        WidgetTextSetting scrollBar = WidgetTextSetting.CreateIgnore("UIAScrollBar");
        WidgetTextSetting menuBar = WidgetTextSetting.CreateIgnore("UIAMenuBar");
        WidgetTextSetting statusBar = WidgetTextSetting.CreateIgnore("UIAStatusBar");
        WidgetTextSetting textEdit = WidgetTextSetting.CreateExtract("UIAEdit", "UIAValueValue");
        WidgetTextSetting icon = WidgetTextSetting.CreateIgnoreAncestorBased("UIAMenuItem",
                Arrays.asList("UIAMenuBar", "UIATitleBar", "UIAWindow", "Process"));
        WidgetTextSetting toolBarButtons = WidgetTextSetting.CreateIgnoreAncestorBased("UIAButton",
                Arrays.asList("UIATitleBar", "UIAWindow", "Process"));
        WidgetTextSetting scrollBarButtons = WidgetTextSetting.CreateIgnoreAncestorBased("UIAButton",
                Arrays.asList("UIAScrollBar", "UIAEdit", "UIAWindow", "Process"));

        // Webdriver protocol
        WidgetTextSetting skipToContentWdou = WidgetTextSetting.CreateIgnoreAncestorBased("WdA",
                Arrays.asList("WdDIV", "Process"));

        instance.widget = new ArrayList<>(Arrays.asList(scrollBar, statusBar, menuBar, textEdit, icon, toolBarButtons, scrollBarButtons, skipToContentWdou));
        instance.loggingEnabled = false;
        return instance;
    }

    @Override
    public int compareTo(WidgetTextConfiguration other) {
        int result = -1;

        if (widget.size() == other.widget.size() && loggingEnabled == other.loggingEnabled) {
            for (int i = 0; i < widget.size(); i++) {
                int index = i;
                if (other.widget.stream().noneMatch(it -> it.compareTo(widget.get(index)) == 0)) {
                    return result;
                }
            }
            return 0;
        }
        return result;
    }
}
