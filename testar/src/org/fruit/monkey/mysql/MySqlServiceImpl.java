package org.fruit.monkey.mysql;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Volume;
import org.fruit.alayer.Action;
import org.fruit.alayer.Verdict;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Main;
import org.fruit.monkey.Settings;
import org.fruit.monkey.docker.DockerPoolService;
import org.fruit.monkey.docker.DockerPoolServiceImpl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Date;

public class MySqlServiceImpl implements MySqlService {

    private static final String LOCAL_DATABASE_PATH = "bin" + File.separator + "db";
    private static final String LOCAL_DATABASE_INIT_PATH = "db-init";
    private static final String LAST_ID_QUERY = "SELECT LAST_INSERT_ID";

    private DockerPoolService dockerPoolService;
    private Settings settings;
    private String jdbcURL;
    private Connection connection;
    private PreparedStatement lastIdStatement;

    public MySqlServiceImpl(Settings settings) {
        this.settings = settings;
    }

    public void startLocalDatabase() throws IOException, ClassNotFoundException, SQLException {
        dockerPoolService = new DockerPoolServiceImpl("mysql");
        final String imageId = dockerPoolService.buildImage(new File(Main.databaseDir), null);
        final String databaseInitPath = Main.extrasDir + File.separator + LOCAL_DATABASE_INIT_PATH;
        final HostConfig hostConfig = HostConfig.newHostConfig()
                .withPortBindings(PortBinding.parse("13306:3306"))
                .withBinds(
                        new Bind(settings.get(ConfigTags.DataStoreDirectory), new Volume("/var/lib/mysql")),
                        new Bind(databaseInitPath, new Volume("/docker-entrypoint-initdb.d"))
                );
        dockerPoolService.startWithImage(imageId, "mysql", hostConfig);

        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:13306/testar?user=testar&password=testar");
        lastIdStatement = connection.prepareStatement(LAST_ID_QUERY);
    }

    public void connectExternalDatabase(String databaseURL) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(databaseURL);
        lastIdStatement = connection.prepareStatement(LAST_ID_QUERY);
    }

    public int registerReport(String tag) throws SQLException {
        PreparedStatement addReportStatement = connection.prepareStatement("INSERT INTO reports (tag, time) VALUES (?, NOW())");
        addReportStatement.setString(1, tag);
        addReportStatement.executeUpdate();

        final ResultSet resultSet = lastIdStatement.executeQuery();
        return resultSet.getInt(1);
    }

    public int registerIteration(int reportId) throws SQLException {
        PreparedStatement addSequenceStatement = connection.prepareStatement("INSERT INTO iterations (report_id) VALUES (?)");
        addSequenceStatement.setInt(1, reportId);
        addSequenceStatement.executeUpdate();

        final ResultSet resultSet = lastIdStatement.executeQuery();
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

        final ResultSet resultSet = lastIdStatement.executeQuery();
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
