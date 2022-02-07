package org.fruit.monkey.mysql;

import org.fruit.monkey.TestarServiceException;
import org.fruit.monkey.docker.DockerPoolService;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

public interface MySqlService {

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

        public ActionData(int id, int iterationId, String name, String description, String status, String screenshot, Timestamp startTime) {
            this.id = id;
            this.iterationId = iterationId;
            this.name = name;
            this.description = description;
            this.status = status;
            this.screenshot = screenshot;
            this.startTime = startTime;
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
    }

    MySqlServiceDelegate getDelegate();
    void setDelegate(MySqlServiceDelegate delegate);

    void startLocalDatabase(String databaseName, String userName, String userPassword) throws IOException, ClassNotFoundException, SQLException, TestarServiceException;
//    void stopLocalDatabase();
    void connectExternalDatabase(String hostname, String databaseName, String userName, String userPassword) throws ClassNotFoundException, SQLException;

    int registerReport(String tag) throws SQLException;
    int registerIteration(int reportId) throws SQLException;
    int registerIteration(int reportId, String info, Double severity) throws SQLException;
    int registerAction(String name, String description, String status, String screenshot, Timestamp startTime) throws SQLException;
    int registerState(String concreteIdCustom, String abstractId, String abstractRId, String abstractRTId, String abstractRTPId) throws SQLException;

    void addActionToIteration(int actionId, int iterationId) throws SQLException;
    void setSelectionInIteration(int iterationId, int lastExecutedActionId, int lastStateId) throws SQLException;
    void storeVerdict(int iterationId, String info, Double severity) throws SQLException;
    void finalizeReport(int reportId, int actionsPerSequence, int totalSequences, String url) throws SQLException;

    int findState(String concreteIdCustom, String abstractId) throws SQLException;


    // Reading methods
    int getReportId(String reportTag) throws SQLException;
    int getFirstIterationId(int reportId) throws SQLException;
    int getNextIterationId(int reportId, int iterationId) throws SQLException;
    ActionData getFirstAction(int iterationId) throws SQLException;
    ActionData getNextAction(int iterationId, Timestamp actionTime) throws SQLException;

    DockerPoolService getDockerPoolService();
}
