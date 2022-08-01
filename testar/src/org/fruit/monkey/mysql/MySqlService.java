package org.fruit.monkey.mysql;

import org.fruit.monkey.TestarServiceException;
import org.fruit.monkey.docker.DockerPoolService;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

public interface MySqlService {
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

    DockerPoolService getDockerPoolService();
}
