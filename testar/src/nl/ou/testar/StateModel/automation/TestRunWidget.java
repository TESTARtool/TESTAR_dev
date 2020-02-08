package nl.ou.testar.StateModel.automation;

import com.opencsv.bean.CsvBindByName;

public class TestRunWidget {

    @CsvBindByName(column = "combi_id")
    private int combiId;

    @CsvBindByName(column = "test_run_id")
    private int testRunId;

    @CsvBindByName(column = "widget_id")
    private int widgetId;

    public int getCombiId() {
        return combiId;
    }

    public void setCombiId(int combiId) {
        this.combiId = combiId;
    }

    public int getTestRunId() {
        return testRunId;
    }

    public void setTestRunId(int testRunId) {
        this.testRunId = testRunId;
    }

    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }
}
