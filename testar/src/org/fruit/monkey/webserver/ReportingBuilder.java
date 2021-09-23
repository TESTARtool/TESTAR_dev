package org.fruit.monkey.webserver;

import org.fruit.monkey.docker.DockerPoolService;

import java.io.IOException;

public class ReportingBuilder {
    private final int port;
    private final boolean orientEnabled;
    private final boolean mysqlEnabled;
    private final DockerPoolService dockerPoolService;

    // Mysql reporting
    private String dbHostname = "127.0.0.1";
    private String dbUsername = "testar";
    private String dbPassword = "testar";
    private String dbDatabase = "testar";
    private int dbPort = 13306;

    // Statemodel reporting
    private String oHostname = "127.0.0.1";
    private String oUsername = "root";
    private String oPassword = "testar";
    private String oDatabase = "testar";
    private int oPort = 2424;

    public ReportingBuilder(int port, DockerPoolService dps, boolean orientEnabled, boolean mysqlEnabled) {
        this.port = port;
        this.orientEnabled = orientEnabled;
        this.mysqlEnabled = mysqlEnabled;
        this.dockerPoolService = dps;
    }

    public void setOrientDBConfiguraton(String hostname, String username, String password, String database, int port) {
        this.oHostname = hostname;
        this.oUsername = username;
        this.oPassword = password;
        this.oDatabase = database;
        this.oPort = port;
    }

    public void setMysqlDBConfiguraton(String hostname, String username, String password, String database, int port) {
        this.dbHostname = hostname;
        this.dbUsername = username;
        this.dbPassword = password;
        this.dbDatabase = database;
        this.dbPort = port;
    }

    public ReportingService build() throws IOException {
        String mysqlAdapter = (mysqlEnabled) ? "MYSQL" : "None";


        final String[] env = {"ADAPTER=" + mysqlAdapter, "MYSQL_HOST=" + dbHostname, "MYSQL_PORT=" + dbPort,
                "MYSQL_DATABASE=" + dbDatabase, "MYSQL_USER=" + dbUsername, "MYSQL_PASSWORD=" + dbPassword,
                "MYSQL_WAIT=5", "ORIENTDB_ENABLED=" + orientEnabled, "ORIENTDB_PORT=" + oPort,
                "ORIENTDB_DATABASE=" + oDatabase, "ORIENTDB_PASSWORD=" + oPassword, "ORIENTDB_USER=" + oUsername, "ORIENTDB_HOST=" + oHostname};

        return new ReportingServiceImpl(this.port, env, this.dockerPoolService);
    }

}
