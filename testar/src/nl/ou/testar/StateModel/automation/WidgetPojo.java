package nl.ou.testar.StateModel.automation;

import com.opencsv.bean.CsvBindByName;

public class WidgetPojo {

    @CsvBindByName(column = "widget_id")
    private int widgetId;

    @CsvBindByName(column = "widget_config_name")
    private String widgetConfigName;

    @CsvBindByName(column = "widget_description")
    private String widgetDescription;

    @CsvBindByName(column = "use_in_abstraction")
    private String useInAbstraction;

    @CsvBindByName(column = "widget_group")
    private String widgetGroup;

    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }

    public String getWidgetConfigName() {
        return widgetConfigName;
    }

    public void setWidgetConfigName(String widgetConfigName) {
        this.widgetConfigName = widgetConfigName;
    }

    public String getWidgetDescription() {
        return widgetDescription;
    }

    public void setWidgetDescription(String widgetDescription) {
        this.widgetDescription = widgetDescription;
    }

    public String getUseInAbstraction() {
        return useInAbstraction;
    }

    public void setUseInAbstraction(String useInAbstraction) {
        this.useInAbstraction = useInAbstraction;
    }

    public String getWidgetGroup() {
        return widgetGroup;
    }

    public void setWidgetGroup(String widgetGroup) {
        this.widgetGroup = widgetGroup;
    }
}
