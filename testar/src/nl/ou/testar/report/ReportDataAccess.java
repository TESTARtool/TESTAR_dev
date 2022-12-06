package nl.ou.testar.report;

import org.fruit.monkey.TestarServiceException;
import org.fruit.monkey.docker.DockerPoolService;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface ReportDataAccess {

    class IterationData {
        private int id;
        private int reportId;
        private String info;
        private Double severity;

        public IterationData(int id, int reportId, String info, Double severity) {
            this.id = id;
            this.reportId = reportId;
            this.info = info;
            this.severity = severity;
        }

        public int getId() {
            return id;
        }

        public String getInfo() {
            return info;
        }

        public Double getSeverity() {
            return severity;
        }
    }

    class ActionData {
        private int id;
        private int iterationId;
        private String name;
        private String description;
        private String status;
        private String screenshot;
        private Timestamp startTime;
        private String widgetPath;

        public ActionData(int id, int iterationId, String name, String description, String status, String screenshot, Timestamp startTime, String widgetPath) {
            this.id = id;
            this.iterationId = iterationId;
            this.name = name;
            this.description = description;
            this.status = status;
            this.screenshot = screenshot;
            this.startTime = startTime;
            this.widgetPath = widgetPath;
        }

        public int getId() {
            return id;
        }

        public int getIterationId() {
            return iterationId;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getStatus() {
            return status;
        }

        public String getScreenshot() {
            return screenshot;
        }

        public Timestamp getStartTime() {
            return startTime;
        }

        public String getWidgetPath() {
            return widgetPath;
        }
    }

    int registerReport(String tag) throws ReportDataException;

    int registerIteration(int reportId) throws ReportDataException;

    int registerIteration(int reportId, String info, Double severity) throws ReportDataException;

    int registerAction(String name, String description, String status, String screenshot, Timestamp startTime, boolean selected, int stateId, int targetStateId, String widgetPath) throws ReportDataException;

    int registerState(String concreteIdCustom, String abstractId, String abstractRId, String abstractRTId, String abstractRTPId, Double severity, String status) throws ReportDataException;

    int registerStateAction(int stateId, int actionId, boolean visited) throws ReportDataException;

    void registerTargetState(int actionId, int stateId) throws ReportDataException;

    void addActionToIteration(int actionId, int iterationId) throws ReportDataException;

    void addStateToIteration(int stateId, int iterationId) throws ReportDataException;

    void setSelectionInIteration(int iterationId, int lastExecutedActionId, int lastStateId) throws ReportDataException;

    void storeVerdict(int iterationId, String info, Double severity) throws ReportDataException;

    void finalizeReport(int reportId, int actionsPerSequence, int totalSequences, String url) throws ReportDataException;

    int findState(String concreteIdCustom, String abstractId) throws ReportDataException;


    // Reading methods
    int getReportId(String reportTag) throws ReportDataException;

    int getFirstIterationId(int reportId) throws ReportDataException;

    int getNextIterationId(int reportId, int iterationId) throws ReportDataException;

    ActionData getFirstAction(int iterationId) throws ReportDataException;

    ActionData getNextAction(int iterationId, Timestamp actionTime) throws ReportDataException;

    List<IterationData> getAllIterations(int reportId) throws ReportDataException;

    List<ActionData> getAllActions(int iterationId) throws ReportDataException;

    List<ActionData> getSelectedActions(int iterationId) throws ReportDataException;
}
