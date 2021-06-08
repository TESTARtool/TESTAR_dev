package org.fruit.monkey.mysql;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Volume;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Main;
import org.fruit.monkey.Settings;
import org.fruit.monkey.docker.DockerPoolService;
import org.fruit.monkey.docker.DockerPoolServiceImpl;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class MySqlServiceImpl implements MySqlService {

    private static final String LOCAL_DATABASE_PATH = "bin" + File.separator + "db";
    private static final String LOCAL_DATABASE_INIT_PATH = "db-init";
    private static final String LAST_ID_QUERY = "SELECT LAST_INSERT_ID()";

    private DockerPoolService dockerPoolService;
    private Settings settings;
    private Connection connection;
    private PreparedStatement lastIdStatement;

    public MySqlServiceImpl(Settings settings) {
        this.settings = settings;
        dockerPoolService = new DockerPoolServiceImpl();
    }

    public void startLocalDatabase(String databaseName, String userName, String userPassword) throws IOException, ClassNotFoundException, SQLException {
        dockerPoolService.start("mysql");
        final String imageId = dockerPoolService.buildImage(new File(Main.databaseDir),
                "FROM mysql:latest\n" +
                "ENV MYSQL_ROOT_PASSWORD=" + userPassword + "\n" +
                "ENV MYSQL_DATABASE=" + databaseName + "\n" +
                "ENV MYSQL_USER=" + userName + "\n" +
                "ENV MYSQL_PASSWORD=" + userPassword);
        final File databaseDir = new File(settings.get(ConfigTags.DataStoreDirectory));
        if (databaseDir.isDirectory()) {
            System.out.println("Directory exists: " + databaseDir.getAbsolutePath());
        }
        else {
            System.out.println("Directory does not exist: " + databaseDir.getAbsolutePath());
        }
        final File databaseInitDir = new File(Main.databaseDir + File.separator + LOCAL_DATABASE_INIT_PATH);
        if (databaseInitDir.isDirectory()) {
            System.out.println("Directory exists: " + databaseInitDir.getAbsolutePath());
        }
        else {
            System.out.println("Directory does not exist: " + databaseInitDir.getAbsolutePath());
        }
//        final String databaseInitPath = Main.extrasDir + File.separator + LOCAL_DATABASE_INIT_PATH;
        final HostConfig hostConfig = HostConfig.newHostConfig()
                .withPortBindings(PortBinding.parse("13306:3306"))
                .withBinds(
                        new Bind(databaseDir.getAbsolutePath(), new Volume("/var/lib/mysql")),
                        new Bind(databaseInitDir.getAbsolutePath(), new Volume("/docker-entrypoint-initdb.d"))
                );
        dockerPoolService.startWithImage(imageId, "mysql", hostConfig);
        Class.forName("com.mysql.jdbc.Driver");
        while (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:13306/" + databaseName +
                        "?user=" + userName + "&password=" + userPassword);
            }
            catch (CommunicationsException e) {
                System.out.println("Still not ready");
                try {
                    Thread.sleep(2000);
                }
                catch (Exception ex) {}
            }
        }
        lastIdStatement = connection.prepareStatement(LAST_ID_QUERY);
    }

    public void connectExternalDatabase(String hostname, String databaseName, String userName, String userPassword) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + hostname + "/" + databaseName +
                "?user=" + userName + "&password=" + userPassword);
        lastIdStatement = connection.prepareStatement(LAST_ID_QUERY);
    }

    public int registerReport(String tag) throws SQLException {
        PreparedStatement addReportStatement = connection.prepareStatement("INSERT INTO reports (tag, time) VALUES (?, NOW())");
        addReportStatement.setString(1, tag);
        addReportStatement.executeUpdate();

        final ResultSet resultSet = lastIdStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int registerIteration(int reportId) throws SQLException {
        PreparedStatement addSequenceStatement = connection.prepareStatement("INSERT INTO iterations (report_id) VALUES (?)");
        addSequenceStatement.setInt(1, reportId);
        addSequenceStatement.executeUpdate();

        final ResultSet resultSet = lastIdStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public int registerAction(int iterationId, String name, String description, String status, String screenshot, Timestamp startTime) throws SQLException {
        PreparedStatement addActionStatement = connection.prepareStatement("INSERT INTO actions (iteration_id, name, description, status, screenshot, start_time) VALUES (?, ?, ?, ?, ?, ?)");
        addActionStatement.setInt(1, iterationId);
        addActionStatement.setString(2, name);
        addActionStatement.setString(3, description);
        addActionStatement.setString(4, status);
        addActionStatement.setString(5, screenshot);
        addActionStatement.setTimestamp(6, startTime);
        addActionStatement.executeUpdate();

        final ResultSet resultSet = lastIdStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public void storeVerdict(int iterationId, String info, Double severity) throws SQLException {
        PreparedStatement updateVerdictStatement = connection.prepareStatement("UPDATE iterations SET info=?, severity=? WHERE id=?");
        updateVerdictStatement.setString(1, info);
        updateVerdictStatement.setDouble(2, severity);
        updateVerdictStatement.setInt(3, iterationId);
        updateVerdictStatement.executeUpdate();
    }

    public void stopLocalDatabase() {
        dockerPoolService.dispose(false);
    }

    public DockerPoolService getDockerPoolService() {
        return dockerPoolService;
    }
}
