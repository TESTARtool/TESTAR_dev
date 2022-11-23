package org.fruit.monkey.mysql;

import nl.ou.testar.report.ReportDataAccess;

import org.fruit.monkey.docker.DockerPoolService;

import java.sql.SQLException;

public interface MySqlService extends ReportDataAccess {
    MySqlServiceDelegate getDelegate();
    void setDelegate(MySqlServiceDelegate delegate);

    void startLocalDatabase(String databaseName, String userName, String userPassword) throws Exception;
    //    void stopLocalDatabase();
    void connectExternalDatabase(String hostname, String databaseName, String userName, String userPassword) throws ClassNotFoundException, SQLException;
    DockerPoolService getDockerPoolService();
}
