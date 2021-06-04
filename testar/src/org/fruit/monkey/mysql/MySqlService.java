package org.fruit.monkey.mysql;

import org.fruit.monkey.docker.DockerPoolService;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

public interface MySqlService {
    void startLocalDatabase() throws IOException, ClassNotFoundException, SQLException;
    void stopLocalDatabase();
    void connectExternalDatabase(String databaseURL) throws ClassNotFoundException, SQLException;

    int registerReport(String tag) throws SQLException;
    int registerIteration(int reportId) throws SQLException;
    int registerAction(int iterationId, String name, String description, String status, String screenshot, Timestamp startTime) throws SQLException;
    void storeVerdict(int iterationId, String info, Double severity) throws SQLException;

    DockerPoolService getDockerPoolService();
}
